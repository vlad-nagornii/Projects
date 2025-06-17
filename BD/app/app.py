import os
from flask import Flask, request, jsonify
from logging.config import dictConfig
import psycopg
from psycopg.rows import namedtuple_row
from psycopg import DatabaseError, Error
from datetime import datetime, timedelta
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from psycopg_pool import ConnectionPool

dictConfig(
    {
        "version": 1,
        "formatters": {
            "default": {
                "format": "[%(asctime)s] %(levelname)s in %(module)s:%(lineno)s - %(funcName)20s(): %(message)s",
            }
        },
        "handlers": {
            "wsgi": {
                "class": "logging.StreamHandler",
                "stream": "ext://flask.logging.wsgi_errors_stream",
                "formatter": "default",
            }
        },
        "root": {"level": "INFO", "handlers": ["wsgi"]},
    }
)

app = Flask(__name__)
RATELIMIT_STORAGE_URI = os.environ.get("RATELIMIT_STORAGE_URI", "memory://")

app = Flask(__name__)
app.config.from_prefixed_env()
log = app.logger
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["200 per day", "50 per hour"],
    storage_uri=RATELIMIT_STORAGE_URI,
)


DATABASE_URL = os.environ.get("DATABASE_URL", "postgres://aviacao:aviacao@postgres/aviacao")
pool = ConnectionPool(
    conninfo=DATABASE_URL,
    kwargs={
        "autocommit": True,  # If True don’t start transactions automatically.
        "row_factory": namedtuple_row,
    },
    min_size=4,
    max_size=10,
    open=True,
    # check=ConnectionPool.check_connection,
    name="postgres_pool",
    timeout=5,
)

@app.route('/', methods=['GET'])
@limiter.limit("1 per second")
### Lista os Aeroportos para os quais a Empresa efetua Voos ###
def listar_aeroportos():
    with pool.connection() as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT nome, cidade FROM aeroporto ORDER BY cidade, nome")
            aeroportos = cur.fetchall()
            log.debug(f"Found {cur.rowcount} rows.")
            if not aeroportos:
                return jsonify({"message": "No Airports found.", "status": "error"}), 404
    return jsonify(aeroportos), 200



@app.route('/voos/<string:partida>/', methods=['GET'])
@limiter.limit("1 per second")
### Lista os Voos que partem de um Aeroporto nas próximas 12 horas ###
def listar_voos_partida(partida):
    partida = partida.upper()
    if len(partida) != 3:
        return jsonify({"message": "No Airport found with the given id.", "status": "error"}), 404

    tempo_actual = datetime.now()
    limite = tempo_actual + timedelta(hours=12)

    with pool.connection() as conn:
        with conn.cursor() as cur:
            #Check if airport exists
            cur.execute("""
                SELECT * FROM Aeroporto a WHERE a.codigo = %s
            """, (partida,))
            check_airport = cur.fetchone()
            if check_airport is None:  
                return jsonify({"message": "Invalid Airport code.", "status": "error"}), 400
            
            cur.execute("""
                SELECT v.no_serie, v.hora_partida, a_chegada.nome as aeroporto_chegada
                FROM voo v
                JOIN aeroporto a_chegada ON v.chegada = a_chegada.codigo
                WHERE v.partida = %s AND v.hora_partida BETWEEN %s AND %s
                ORDER BY v.hora_partida
            """, (partida, tempo_actual, limite))
            voos = cur.fetchall()
            log.debug(f"Found {cur.rowcount} rows.")
    
    if not voos:
        return jsonify({"message": "No flights found.", "status": "error"}), 404

    return jsonify(voos), 200



@app.route('/voos/<string:partida>/<string:chegada>/', methods=['GET'])
@limiter.limit("1 per second")
### Lista os próximos 3 Voos planeados pela Empresa com os respetivos Aeroportos de partida e chegada ###
def proximos_voos_disponiveis(partida, chegada):
    partida = partida.upper()
    chegada = chegada.upper()

    if len(partida) != 3:
        return jsonify({'message': 'Invalid departure Airport code.', 'status': 'error'}), 400
    
    if len(chegada) != 3:
        return jsonify({'message': 'Invalid arival Airport code.', 'status': 'error'}), 400

    tempo_actual = datetime.now()

    with pool.connection() as conn:
        with conn.cursor() as cur:
            #Check if departure airport exists
            cur.execute("""
                SELECT * FROM Aeroporto a WHERE a.codigo = %s
            """, (partida,))
            check_airport = cur.fetchone()
            if check_airport is None:  
                return jsonify({"message": "No Airport found with the given id.", "status": "error"}), 404
            
            #Check if arrival airport exists
            cur.execute("""
                SELECT * FROM Aeroporto a WHERE a.codigo = %s
            """, (chegada,))
            check_airport = cur.fetchone()
            if check_airport is None:  
                return jsonify({"message": "No Airport found with the given id.", "status": "error"}), 404
            
            cur.execute("""
                WITH capacidade_aviao AS (
                    SELECT no_serie, 
                            COUNT(CASE WHEN prim_classe THEN 1 END) as cap_primeira,
                            COUNT(CASE WHEN NOT prim_classe THEN 1 END) as cap_segunda
                    FROM assento 
                    GROUP BY no_serie
                ),
                bilhetes_vendidos AS (
                    SELECT v.id as voo_id,
                            COUNT(CASE WHEN b.prim_classe THEN 1 END) as vendidos_primeira,
                            COUNT(CASE WHEN NOT b.prim_classe THEN 1 END) as vendidos_segunda
                    FROM voo v
                    LEFT JOIN bilhete b ON v.id = b.voo_id
                    WHERE v.partida = %s AND v.chegada = %s AND v.hora_partida > %s
                    GROUP BY v.id
                )
                SELECT v.no_serie, v.hora_partida
                FROM voo v
                JOIN capacidade_aviao ca ON v.no_serie = ca.no_serie
                LEFT JOIN bilhetes_vendidos bv ON v.id = bv.voo_id
                WHERE v.partida = %s AND v.chegada = %s AND v.hora_partida > %s
                AND (
                    COALESCE(bv.vendidos_primeira, 0) < ca.cap_primeira OR
                    COALESCE(bv.vendidos_segunda, 0) < ca.cap_segunda
                )
                ORDER BY v.hora_partida
                LIMIT 3
            """, (partida, chegada, tempo_actual, partida, chegada, tempo_actual))
            voos = cur.fetchall()
            log.debug(f"Found {cur.rowcount} rows.")
    
    if not voos:
        return jsonify({"message": "No flights found.", "status": "error"}), 404

    return jsonify(voos), 200



@app.route('/compra/<int:voo>/', methods=['POST'])
### Efetua uma compra de Bilhete(s) para um dado Voo ###
def comprar_bilhetes(voo):
    nif_cliente = request.json.get('nif_cliente')
    bilhetes = request.json.get('bilhetes')

    if not nif_cliente:
        return jsonify({'message': 'Client NIF is required.', 'status': 'error'}), 400
    if not bilhetes:
        return jsonify({'message': 'Ticket list is required.', 'status': 'error'}), 400
    for bilhete in bilhetes:
        if 'nome_passageiro' not in bilhete:
            return jsonify({'message': 'Passenger name is required.', 'status': 'error'}), 400
        if "prim_classe" not in bilhete:
            return jsonify({'message': 'Ticket class is required.', 'status': 'error'}), 400
        if not isinstance(bilhete.get('prim_classe'), bool):
            return jsonify({"message": "Invalid format for prim_classe.", "status": "error"}), 400

    if len(nif_cliente) != 9 or not nif_cliente.isdigit():
        return jsonify({'message': 'Invalid client NIF', 'status': 'error'}), 400

    with pool.connection() as conn:
        with conn.transaction():
            with conn.cursor() as cur:
                #lock
                cur.execute("SELECT no_serie FROM voo WHERE id = %s FOR UPDATE", (voo,))
                
                args = cur.fetchone()
                if args is None:
                    return jsonify({"message": "No flight found with the given id.", "status": "error"}), 404
                
                no_serie = args[0]
                
                cur.execute("""
                    INSERT INTO venda (nif_cliente, balcao ,hora) 
                    VALUES (%s, NULL, NOW()) 
                    RETURNING codigo_reserva
                """, (nif_cliente,))
                codigo_reserva = cur.fetchone()[0]
                log.debug(f"Inserted {cur.rowcount} rows.")

                try:
                    for bilhete in bilhetes:
                        nome_passageiro = bilhete.get('nome_passageiro')
                        primeira_classe = bilhete.get('prim_classe')

                        preco = 250.00 if primeira_classe else 100.00

                        cur.execute("""
                            INSERT INTO bilhete (voo_id, codigo_reserva, nome_passegeiro, preco, prim_classe, lugar, no_serie)
                            VALUES (%s, %s, %s, %s, %s, NULL, %s)
                        """, (voo, codigo_reserva, nome_passageiro, preco, primeira_classe, no_serie))
                    log.debug(f"Inserted {cur.rowcount} rows.")

                except DatabaseError as e:
                    if "Excedida a capacidade da classe" in str(e):
                        return jsonify({"message": "Plane capacity has already been reached for the chosen seat class.", "status": "error"}), 400
                    else:
                        raise
    return jsonify({
        'status': "success",
        'codigo_reserva': codigo_reserva,
        'message': f'{len(bilhetes)} bilhete(s) comprado(s) com sucesso'
    }), 200



@app.route('/checkin/<int:bilhete>/', methods=['POST'])
### Efetua o check-in do Bilhete com o respetivo código de reserva ###
def fazer_checkin(bilhete):
    with pool.connection() as conn:
        with conn.transaction():
            with conn.cursor() as cur:
                #lock
                cur.execute("""
                    SELECT b.voo_id, b.no_serie, b.prim_classe, b.lugar
                    FROM bilhete b
                    WHERE b.id = %s
                    FOR UPDATE
                """, (bilhete,))

                args = cur.fetchone()
                if args is None:
                    return jsonify({'message': 'No ticket found with the given id.', 'status': 'error'}), 400
                
                voo_id =  args[0]
                no_serie = args[1]
                prim_classe = args[2]
                lugar = args[3]

                if lugar is not None:
                    return jsonify({'message': 'Check-in has already been completed.', 'status': 'error'}), 400

                #lock
                cur.execute("""SELECT lugar
                    FROM assento a
                    WHERE a.no_serie = %s
                    AND a.prim_classe = %s
                    AND NOT EXISTS (
                        SELECT 1
                        FROM bilhete b
                        WHERE b.voo_id = %s
                            AND b.prim_classe = %s
                            AND b.lugar = a.lugar
                    )
                    ORDER BY CAST(REGEXP_REPLACE(lugar, '[A-Z]', '', 'g') AS INTEGER), 
                    REGEXP_REPLACE(lugar, '[0-9]', '', 'g')  
                    LIMIT 1
                    FOR UPDATE;""", (no_serie, prim_classe, voo_id, prim_classe)) ##organiza lugares livres por fila e número e devolve apenas o primeiro
                
                fila_assento = cur.fetchone()
                
                #em princípio nunca acontece...
                if not fila_assento:
                    return jsonify({'message': 'No available seats.', 'status': 'error'}), 400
                
                assento = fila_assento[0]

                cur.execute("""
                    UPDATE bilhete 
                    SET lugar = %s
                    WHERE id = %s
                """, (assento, bilhete))
                log.debug(f"Updated {cur.rowcount} rows.")

    return jsonify({
        'status': "success",
        'lugar': assento,
        'message': 'Check-in realizado com sucesso'
    }), 200


@app.errorhandler(404)
def not_found(error):
    return jsonify({'erro': 'Endpoint não encontrado'}), 404

@app.errorhandler(500)
def internal_error(error):
    return jsonify({'erro': 'Erro interno do servidor'}), 500

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=8080)

