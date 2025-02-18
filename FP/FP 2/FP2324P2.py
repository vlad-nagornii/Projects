#
# TAD INTERSECAO
#

def cria_intersecao(col, lin):
    '''
    cria_intersecao: str x int → intersecao

    Recebe uma letra maiuscula entre A e S e um numero entre 1 e 19,
    e devolve uma intersecao do tipo ("A",1)
    '''

    if not (isinstance(col, str)  
    and len(col) == 1 and ord("A") <= ord(col) <= ord("S") 
    and type(lin) == int and 1 <= lin <= 19):
        raise ValueError("cria_intersecao: argumentos invalidos")
        
    return (col, lin)

def obtem_col(i):
    '''obtem_col: intersecao → str

    Devolve a coluna da intersecao.
    '''

    return i[0]

def obtem_lin(i):
    '''obtem_col: intersecao → str

    Devolve a linha da intersecao.
    '''

    return i[1]
    
def eh_intersecao(arg):
    '''eh_intersecao: universal → booleano

    Verifica se o argumento e uma intersecao.
    '''
    if not (isinstance(arg, tuple) and len(arg) == 2 
    and isinstance(arg[0], str) and isinstance(arg[1], int) 
    and len(arg[0]) == 1 and ord("A") <= ord(arg[0]) <= ord("S") 
    and 1 <= arg[1] <= 19):
        return False
    return True
    
def intersecoes_iguais(i1, i2):
    """
    intersecoes iguais: universal x universal 7 → booleano
    
    Verifica se duas intersecoes sao iguais
    """

    if not eh_intersecao(i1) or not eh_intersecao(i2):
        return False

    if obtem_col(i1) == obtem_col(i2) and obtem_lin(i1) == obtem_lin(i2):
        return True

    return False

def intersecao_para_str(i):
    """
    intersecao para str: intersecao → str

    Devolve uma string correspodente a intersecao no argumento
    """

    return("{}{}".format(obtem_col(i), obtem_lin(i)))

def str_para_intersecao(s):
    """
    str para intersecao: str → intersecao

    Recebe uma string e devolve a intersecao correspondente
    """

    if len(s) == 2:
        return cria_intersecao(s[0], int(s[1]))
    else:
        return cria_intersecao(s[0], int(s[1:]))

# INTERSECAO ALTO NIVEL

def obtem_intersecoes_adjacentes(i , l):
    """
    obtem intersecoes adjacentes: intersecao X intersecao → tuplo

    Retorna um tuplo com as intersecoes adjacentes da intersecao (i)
    """

    col = obtem_col(i)
    lin = obtem_lin(i)
    adj_intersecoes= ()

    if lin != 1:
        adj_intersecoes += (cria_intersecao(col, lin - 1),)
    if lin != obtem_lin(l):
        adj_intersecoes += (cria_intersecao(col, lin + 1),)
    if col != "A":
        adj_intersecoes+= (cria_intersecao(chr(ord(col) - 1), lin),)
    if col != obtem_col(l):
        adj_intersecoes+= (cria_intersecao(chr(ord(col) + 1), lin),)
    return ordena_intersecoes(adj_intersecoes)

def ordena_intersecoes(t):
    """
    ordena intersecoes: tuplo → tuplo
    
    Recebe um tuplo de intersecoes, e devolve essas intersecoes por ordem de leitura
    """

    return tuple(sorted(t, key=lambda x: (obtem_lin(x), ord(obtem_col(x)))))

def str_intersecao_possivel(arg):  
    """
    str_intersecao_possivel: arg → booleano

    Recebe uma cadeia de caracteres e verifica se corresponde a representacao em string de uma intersecao
    """

    if isinstance(arg, str) and len(arg) in [2, 3] and isinstance(arg[0], str) and ord("A") <= ord(arg[0]) <= ord("S"):
        if len(arg) == 2 and ord("1") <= ord(arg[1]) <= ord("9"):
            return True
        elif len(arg) == 3 and ord(arg[1]) == ord("1") and ord("0") <= ord(arg[2]) <= ord("9"):
            return True

    return False

#
# TAD PEDRA
#

def cria_pedra_branca():
    """
    cria_pedra_branca: {} → pedra

    Devolve a pedra correspondente ao jogador branco
    """

    return "O"

def cria_pedra_preta():
    """
    cria_pedra_preta: {} → pedra

    Devolve a pedra correspondente ao jogador preto
    """

    return "X"

def cria_pedra_neutra():
    """
    cria_pedra_neutra: {} → pedra

    Devolve uma pedra neutra
    """

    return "."

def eh_pedra(arg):
    """
    eh_pedra: universal → booleano

    Devolve True se o argumento for uma pedra, False caso contrario
    """

    if not isinstance(arg, str):
        return False
    if not (arg == "X" or arg == "O" or arg == "."):
        return False
    return True

def eh_pedra_branca(p):
    """
    eh_pedra_branca: pedra → booleano

    Devolve True se a pedra corresponder ao jogador branco e False caso contrario
    """

    if p == "O":
        return True
    return False

def eh_pedra_preta(p):
    """
    eh_pedra_preta: pedra → booleano

    Devolve True se a pedra corresponder ao jogador preto e False caso contrario
    """

    if p == "X":
        return True
    return False

def eh_pedra_neutra(p):
    """
    eh_pedra_neutra: pedra → booleano

    Devolve True se a pedra for neutra e False caso contrario
    """

    if p == cria_pedra_neutra():
        return True
    return False

def pedras_iguais(p1, p2):
    """
    pedras iguais: universal universal → booleano

    Recebe dois argumentos, devolve True se forem duas pedra iguais
    """

    if eh_pedra(p1) and eh_pedra(p2):
        if p1 == p2:
            return True
        return False
    
def pedra_para_str(p):
    """
    pedra_para_str: pedra → str

    Devolve a string correspondente a cada pedra
    X - para o jogador preto
    O - para o jogador branco
    . - para uma pedra neutra
    """

    return p

# PEDRA ALTO NIVEL

def eh_pedra_jogador(p):
        """
        eh pedra jogador: pedra → booleano

        Retorna True se a perna corresponde a um jogador e False caso contrario
        """

        if eh_pedra_preta(p) or eh_pedra_branca(p):
            return True
        return False

#
# TAD GOBAN
#

def cria_goban_vazio(n):
    """
    cria_goban_vazio: int → goban

    Recebe um inteiro e devolve um goban desse tamanho
    """

    if not isinstance(n, int):
        raise ValueError("cria_goban_vazio: argumento invalido")
    if n != 9 and n != 13 and n != 19:
        raise ValueError("cria_goban_vazio: argumento invalido")

    return {"tamanho" : n, "jogador_branco": (), "jogador_preto": ()}

def cria_goban(n, jogador_branco, jogador_preto): 
    """
    cria_goban: int x tuplo x tuplo → goban

    Recebe um inteiro e dois tuplos com intersecoes correspondentes
    ao jogador branco e jogador preto e devolve um goban desse tamanho
    """
    # verifica o tamanho e se jogador_branco e jogador_preto sao tuplos

    if not isinstance(n, int) or (n not in [9, 13, 19]) \
    or not isinstance(jogador_branco, tuple) \
    or not isinstance(jogador_preto, tuple):
        raise ValueError("cria_goban: argumentos invalidos")

    # verifica se os tuplos jogador_branco e jogador_preto nao tem intersecoes repetidas

    if len(jogador_branco) != len(set(jogador_branco)) or len(jogador_preto) != len(set(jogador_preto)):
        raise ValueError("cria_goban: argumentos invalidos")

    # verifica se cada elemento nos tuplos e intersecao e se a mesma intersecao nao aparece em dois tuplos ao mesmo tempo

    for intersecao in jogador_branco + jogador_preto:
        if not eh_intersecao(intersecao) or jogador_branco.count(intersecao) + jogador_preto.count(intersecao) > 1:
            raise ValueError("cria_goban: argumentos invalidos")
    for intersecao in jogador_branco + jogador_preto:
        if (ord(obtem_col(intersecao)) + 1  - ord("A")) > n or obtem_lin(intersecao) > n:
            raise ValueError("cria_goban: argumentos invalidos")

    return {"tamanho" : n, "jogador_branco": jogador_branco, "jogador_preto": jogador_preto}

def obtem_tamanho(g):
    """
    obtem_tamanho: goban → int

    Recebe um goban e devolve o tamanho
    """

    return obtem_lin(obtem_ultima_intersecao(g))

def obtem_jogador_branco(g):
    """
    obtem_jogador_branco: g → tuplo

    Recebe um goban e devolve o tuplo de intersecoe que correspondem ao jogador branco
    """

    # para cada intersecao no goban verifico se a pedra e branca, se sim adiciono ao tuplo jogador_branco

    todas_intersecoes = obtem_todas_intersecoes(g)
    jogador_branco = ()

    for intersecao in todas_intersecoes:
        if (obtem_pedra(g, intersecao)) == cria_pedra_branca():
            jogador_branco += (intersecao,)

    return jogador_branco

def obtem_jogador_preto(g):
    """
    obtem_jogador_preto: g → tuplo

    Recebe um goban e devolve o tuplo de intersecoe que correspondem ao jogador preto
    """

    # para cada intersecao no goban verifico se a pedra e preta, se sim adiciono ao tuplo jogador_preto

    todas_intersecoes = obtem_todas_intersecoes(g)
    jogador_preto = ()

    for intersecao in todas_intersecoes:
        if (obtem_pedra(g, intersecao)) == cria_pedra_preta():
            jogador_preto += (intersecao,)

    return jogador_preto

def cria_copia_goban(g):
    """
    cria_copia_goban: goban → goban
    
    Recebe um goban e devolve uma copia do goban.
    """

    jogador_branco = obtem_jogador_branco(g)
    jogador_preto =  obtem_jogador_preto(g)
    n = obtem_tamanho(g)
    return cria_goban(n, jogador_branco, jogador_preto)

def obtem_ultima_intersecao(g):
    """
    obtem_utlima_intersecao: goban → intersecao 

    Recebe um goban e devolve a ultima intersecao desse goban
    """

    n = g["tamanho"]
    if n == 9:
        return cria_intersecao("I", 9)
    if n == 13:
        return cria_intersecao("M", 13)
    return cria_intersecao("S", 19)

def obtem_pedra(g, i):
    """ 
    obtem_pedra: goban x intersecao → pedra

    Recebe um goban e uma intersecao, e devolve a pedra que esta na intersecao i do goban
    """

    if i in g["jogador_branco"]:
        return cria_pedra_branca()
    elif i in g["jogador_preto"]:
        return cria_pedra_preta()
    else:
        return cria_pedra_neutra()

def obtem_cadeia(g, i):
    """
    obtem_cadeia: goban x intersecao → tuplo
    
    Recebe uma intersecao e devolve o tuplo de intersecoes das pedras da cadeia que passa pela intersecao i.
    """

    ultima_intersecao = obtem_ultima_intersecao(g)
    cadeia = [i,]
    jogador = obtem_pedra(g, i)
    
    # itero sobre cada intersecao na cadeia, obtenho as intersecoes adjacentes, e se as pedras
    # da intersecao e intersecao adjacentes forem iguais, adiciono a intersecao adjacente a cadeia

    for int in cadeia:
        intersecoes_adj = obtem_intersecoes_adjacentes(int, ultima_intersecao)
        for int_adj in intersecoes_adj:
            if pedras_iguais(obtem_pedra(g,int_adj), jogador):
                if int_adj not in cadeia:
                    cadeia.append(int_adj,)
            
    return ordena_intersecoes(tuple(cadeia))

def coloca_pedra(g, i, p):
    """
    coloca_pedra: goban x intersecao x pedra → goban

    Recebe um goban, uma pedra, e uma intersecao. Coloca a pedra p na intersecao i do goban.
    """

    jogador_branco = obtem_jogador_branco(g)
    jogador_preto = obtem_jogador_preto(g)
    
    # se a intersecao estiver no tuplo do jogador oposto, removo essa intersecao
    # verifico se a intersecao esta no tuplo do jogador correspondente e se nao adiciono

    if eh_pedra_branca(p):
        if i in jogador_preto:
            remove_pedra(g , i)
            jogador_preto = obtem_jogador_preto(g)
        if i not in jogador_branco:
            jogador_branco = jogador_branco + (i,)
    elif eh_pedra_preta(p):
        if i in jogador_branco:
            remove_pedra(g, i)
            jogador_branco = obtem_jogador_branco(g)
        if i not in jogador_preto:
            jogador_preto = jogador_preto + (i,)

    g["jogador_branco"] = ordena_intersecoes(jogador_branco)
    g["jogador_preto"] = ordena_intersecoes(jogador_preto)
    return g

def remove_pedra(g, i):
    """
    remove_pedra: goban x intersecao → goban

    Recebe um goban e uma intersecao e remove a pedra da intersecao i do goban
    """

    jogador_branco = obtem_jogador_branco(g)
    jogador_preto = obtem_jogador_preto(g)
    
    if i in jogador_branco:
        jogador_branco = list(jogador_branco)
        jogador_branco.remove(i)
        jogador_branco = tuple(jogador_branco)
    if i in jogador_preto:
        jogador_preto = list(jogador_preto)
        jogador_preto.remove(i)
        jogador_preto = tuple(jogador_preto)
    
    g["jogador_branco"] = ordena_intersecoes(jogador_branco)
    g["jogador_preto"] = ordena_intersecoes(jogador_preto)
    return g

def remove_cadeia(g, tp):
    """
    remove_cadeia: goban x tuplo → goban
    
    Recebe um goban e um tuplo e de intersecoes e 
    remove as pedras correspondentes a cada intersecao no tuplo tp
    """

    for intersecao in tp:
        remove_pedra(g, intersecao)
    return g
    
def eh_goban(arg):
    """
    eh_goban: universal → booleano
    
    Recebe um argumento universal e devolve True se corresponder a um goban
    """
    # verifico se o argumento e u dicionario composto por 3 chaves "tamanho", "jogador_branco" e "jogador_preto"
    # verifico se tamanho e 9, 13 ou 19 e se jogador_branco e jogador_preto sao tuplos de intersecoes validas

    if isinstance(arg, dict) and len(arg) == 3 and "tamanho" in arg and "jogador_branco" in arg and "jogador_preto" in arg:
        if isinstance(arg["tamanho"], int) and arg["tamanho"] in [9, 13, 19] \
        and isinstance(arg["jogador_branco"], tuple) and isinstance(arg["jogador_preto"], tuple):
            for jogador in [arg["jogador_branco"], arg["jogador_preto"]]:
                if any(not eh_intersecao(intersecao) for intersecao in jogador):
                    return False
            return True

    return False

def eh_intersecao_valida(g, i):
    """
    eh_intersecao_valida: goban intersecao → booleano

    Recebe um goban e uma intersecao e verifica se a intersecao i faz parte do goban
    """

    ultima_intersecao = obtem_ultima_intersecao(g)
    ultima_col = obtem_col(ultima_intersecao)
    ultima_lin = obtem_lin(ultima_intersecao)
    col = obtem_col(i)
    lin = obtem_lin(i)

    if not eh_intersecao(i):
        return False

    if lin < 1 or lin > ultima_lin:
        return False
    if ord(col) < ord("A") or col > ultima_col:
        return False
    return True

def gobans_iguais(g1, g2):
    """
    gobans_iguais: universal x universal → booleano

    Recebe dois argumentos, se forem ambos gobans e ambos iguais, devolve True, caso contrario False
    """

    if not eh_goban(g1) or not eh_goban(g2):
        return False

    tamanho_1 = obtem_tamanho(g1)
    jogador_branco_1 = obtem_jogador_branco(g1)
    jogador_preto_1 = obtem_jogador_preto(g1)

    tamanho_2 = obtem_tamanho(g2)
    jogador_branco_2 = obtem_jogador_branco(g2)
    jogador_preto_2 = obtem_jogador_preto(g2)

    if tamanho_1 != tamanho_2:
        return False
    if jogador_branco_1 != jogador_branco_2:
        return False
    if jogador_preto_1 != jogador_preto_2:
        return False
    return True

def goban_para_str(g):
    """
    goban_para_str: goban → str

    Recebe um goban, e devolve a sua representaco em forma de string
    """

    tamanho = obtem_tamanho(g)
    jogador_branco = obtem_jogador_branco(g)
    jogador_preto = obtem_jogador_preto(g)
    goban_str = "   "

    # primeira fila de letras

    for i in range(tamanho):
        goban_str += chr(ord("A") + i) + " "
    goban_str = goban_str[:-1]
    goban_str += "\n"

    # numeros e pedras

    for j in range(tamanho, 0, -1):
        if j > 9:
            goban_str += str(j) + " "
        else:
            goban_str += " " + str(j) + " "
        for i in range(tamanho):
            intersecao = cria_intersecao(chr(ord("A") + i), j)
            if intersecao in jogador_branco:
                goban_str += pedra_para_str(obtem_pedra(g, intersecao)) + ' '
            elif intersecao in jogador_preto:
                goban_str += pedra_para_str(obtem_pedra(g, intersecao)) + ' '
            else:
                goban_str += pedra_para_str(obtem_pedra(g, intersecao)) + ' '
        if j > 9:
            goban_str += str(j) + "\n"
        else:
            goban_str += " " + str(j) + "\n"

    # ultima fila de letras

    goban_str += "   "

    for i in range(tamanho):
        goban_str += chr(ord("A") + i) + " "
    goban_str = goban_str[:-1]
    
    
    return goban_str

# GOBAN ALTO NIVEL

def obtem_todas_intersecoes(g):
    """
    obtem_todas_intersecoes: goban → tuplo  

    Recebe um goban e devolve todas as intersecoes desse goban
    """

    # obtenho o tamanho do goban e crio um tuplo com todas as intersecoes

    tamanho = obtem_tamanho(g)
    intersecoes = ()
    for coluna in range(1, tamanho + 1):
        for linha in range(1, tamanho + 1):
            intersecoes += (cria_intersecao(chr(ord("A") + coluna - 1), linha)),
    return ordena_intersecoes(intersecoes)

def obtem_territorios(g):
    """
    obtem_territorios: goban → tuplo

    Recebe um goban e devolve um tuplo com todos os territorios do goban
    """

    todas_intersecoes = obtem_todas_intersecoes(g)
    jogador_branco = obtem_jogador_branco(g)
    jogador_preto = obtem_jogador_preto(g)
    territorios = ()
    vistas = []

    # para toda as intersecoesm, verifico se nao foram vistas, obtenho a cadeia e se for de pedras neutras adicono a um tuplo

    for intersecao in todas_intersecoes:
        if intersecao not in vistas:
            if intersecao not in jogador_branco and intersecao not in jogador_preto:
                territorio = obtem_cadeia(g, intersecao)
                if territorio not in territorios:
                    territorios += (territorio,)
                    vistas += list(territorio)
    
    return territorios

def obtem_adjacentes_diferentes(g, tp):
    """
    obtem_adjacentes_diferentes: goban x tuplo → tuplo

    Recebe um goban e um tuplo de intersecoes 
    e devolve o tuplo ordenado formado pelas intersec oes adjacentes as intersecoes do tuplo tp:
    1)livres, se as intersecoes do tuplo tp esta o ocupadas por pedras de jogador
    2)ocupadas por pedras de jogador, se as intersecoes do tuplo tp esta o livres
    """

    adjacentes_diferentes = []
    ultima_intersecao = obtem_ultima_intersecao(g)
    livre = not eh_pedra_jogador(obtem_pedra(g, tp[0]))
    
    # para cada intersecao em tp, obtenho as intersecoes adjacentes e se forem livres ou nao adiciono a uma lista

    for intersecao in tp:
        for int_adj in obtem_intersecoes_adjacentes(intersecao, ultima_intersecao):
            if not eh_pedra_jogador(obtem_pedra(g, int_adj)) != livre:
                if int_adj not in adjacentes_diferentes:
                    adjacentes_diferentes.append(int_adj)
    
    return ordena_intersecoes(tuple(adjacentes_diferentes))

def jogada(g, i, p):
    """
    jogada: goban x intersecao x pedra → goban
    
    Recebe um goban, uma intersecao e uma pedra. Coloca a pedra na intersecao i do goban
    e se necessario remove todas as pedras do jogador contrario pertencentes a cadeias adjacentes
    a i sem liberdades,remove todas as pedras do jogador contrario pertencentes a cadeias adjacentes a i sem liberdades
    """

    # coloco a pedra p na intersecao i e removo as cadeias adjacentes do adversario sem liberdades

    coloca_pedra(g, i, p)
    intersecoes_adjacentes_i = obtem_intersecoes_adjacentes(i, obtem_ultima_intersecao(g))
    for intersecao in intersecoes_adjacentes_i:
        if not eh_pedra_neutra(intersecao): 
            if obtem_pedra(g, intersecao) != obtem_pedra(g, i):
                cadeia = obtem_cadeia(g, intersecao)
                if not obtem_adjacentes_diferentes(g, cadeia):
                    remove_cadeia(g, cadeia)
    return g

def obtem_pedras_jogadores(g):
    """
    obtem_pedras_jogadores: goban → tuplo

    Recebe um goban e devolve o numero de pedras que cada jogador tem no tabuleiro
    """

    pedras_jogador_branco = 0
    pedras_jogador_preto = 0

    # para cada intersecao do goban, verifico a pedra dessa intersecao e somo as pedras correspondente ao jogador

    for intersecao in obtem_todas_intersecoes(g):
            if eh_pedra_branca(obtem_pedra(g, intersecao)):
                pedras_jogador_branco += 1
            elif eh_pedra_preta(obtem_pedra(g, intersecao)):
                pedras_jogador_preto += 1

    return (pedras_jogador_branco, pedras_jogador_preto)

#
# FUNCOES ADICIONAIS
#

def calcula_pontos(g):
    """
    calcula_pontos: goban → tuplo

    Recebe um goban e devolve os pontos de cada jogador
    """

    # adiciono o numero de pedras aos pontos de cada jogador

    pedras_jogadores = obtem_pedras_jogadores(g)
    pontos_branco = pedras_jogadores[0]
    pontos_preto = pedras_jogadores[1]

    territorios = obtem_territorios(g)

    # verifico as intersecoes adjacentes de cada territorio de g
    # para cada intersecao, verifico se pertence a um jogador, no caso de pertencer o flag_jogador fica verdadeiro
    # se ambos os flags estiver True o territorio nao pertence a nenhum jogador, se apenas um flag for True pertence 
    # ao jogador cujo flag for True
    
    for territorio in territorios:
        flag_branco = False
        flag_preto = False
        adjacentes_diferentes = obtem_adjacentes_diferentes(g, territorio)
        for adjacente in adjacentes_diferentes:
            if eh_pedra_branca(obtem_pedra(g, adjacente)):
                flag_branco = True
            if eh_pedra_preta(obtem_pedra(g, adjacente)):
                flag_preto = True
        if flag_branco:
            if flag_preto:
                pass
            else:
                pontos_branco += len(territorio)
        elif flag_preto:
            pontos_preto += len(territorio)
    
    return(pontos_branco, pontos_preto)
        
def eh_jogada_legal(g, i, p, l):
    """
    eh_jogada_legal: goban x intersecao x pedra x goban → booleano
    
    Recebe um goban, uma intersecao, uma pedra, e um goban. Devolve True se a jogada
    for legal e False caso contrario
    """

    jogador_branco = obtem_jogador_branco(g)
    jogador_preto = obtem_jogador_preto(g)

    # verifica se a intersecao ja tem uma pedra

    if i in jogador_branco or i in jogador_preto:
        return False
    
    n = obtem_tamanho(g)
    if (ord(obtem_col(i)) + 1  - ord("A")) > n or obtem_lin(i) > n:
        return False
    
    # verifica suicidio

    copia_g = cria_copia_goban(g)
    jogada(copia_g, i, p)

    cadeia = obtem_cadeia(copia_g, i)
    adjacentes_diferentes = obtem_adjacentes_diferentes(copia_g , cadeia)
    if not adjacentes_diferentes:
        return False
    
    # verifica a repeticao do goban

    copia_g = cria_copia_goban(g)
    jogada(copia_g, i, p)
    if gobans_iguais(copia_g, l):
        return False
    return True

def turno_jogador(g, p, l):
    """
    turno_jogador: goban x pedra x goban → booleano
    
    Recebe um goban, uma pedra e um goban, pede um input ao jogador ate que este
    passe uma intersecao que corresponda a uma jogada legal
    """

    while True:
        def pergunta(p):
            resposta = input(f"Escreva uma intersecao ou 'P' para passar [{pedra_para_str(p)}]:")
            return resposta
        i = pergunta(p)
        if i == "P":
            return False
        if str_intersecao_possivel(i):
            i = str_para_intersecao(i)
            if eh_intersecao_valida(g, i):
                    if eh_jogada_legal(g, i, p, l):
                        jogada(g, i, p)
                        return True
    
def go(n, ib, ip):
    """
    go: int x tuple x tuple → booleano
    
    Recebe o tamanho do goban e dois tuplos correpondentes as intersecoes de cada jogador e corre o jogo go.
    """

    jogador_branco = ()
    jogador_preto = ()

    # verificacao de argumentos

    if not isinstance(ib, tuple):
        raise ValueError("go: argumentos invalidos")
    if not isinstance(ip, tuple):
        raise ValueError("go: argumentos invalidos")
    
    # verificacao de arguentos e transformacao dos tuplos dos jogadores

    for intersecao in ib:
        if str_intersecao_possivel(intersecao):
            jogador_branco += (str_para_intersecao(intersecao),)
        else:
            raise ValueError("go: argumentos invalidos")
    for intersecao in ip:
        if str_intersecao_possivel(intersecao):
            jogador_preto += (str_para_intersecao(intersecao),)
        else:
            raise ValueError("go: argumentos invalidos")


    jogador_branco = ordena_intersecoes(jogador_branco)
    jogador_preto = ordena_intersecoes(jogador_preto)

    # verificacao de argumentos

    try:
        cria_goban(n, jogador_branco, jogador_preto)
    except ValueError:
        raise ValueError("go: argumentos invalidos")



    def go_aux(g, p, anterior_passou, g_ko):
        """
        go_aux: goban x pedra x bool x goban → tuplo

        Funcao que corre o jogo. Escreve os pontos de cada jogador
        chama a funcao turno jogador, e chama de novo a si mesma recursivamente 
        atualizando o g_ko, e o anterior_passou.
        Se anterior_passou for True e o jogador passar, retorna a pontuacao de cada jogador
        """

        g_ko_copia = cria_copia_goban(g)
        pontuacao = calcula_pontos(g)
        print(f"Branco (O) tem {pontuacao[0]} pontos")
        print(f"Preto (X) tem {pontuacao[1]} pontos")
        print(goban_para_str(g))
        passou = not (turno_jogador(g, p, g_ko))
        

        if passou == True and anterior_passou == True:
            print(f"Branco (O) tem {pontuacao[0]} pontos")
            print(f"Preto (X) tem {pontuacao[1]} pontos")
            print(goban_para_str(g))
            return pontuacao
        
        if eh_pedra_branca(p):
            return go_aux(g, cria_pedra_preta(), passou, g_ko_copia)
        else:
            return go_aux(g, cria_pedra_branca(), passou, g_ko_copia)
    
    # criacao do goban usando os argumentos de go e chamento da funcao go_aux que corre o jogo

    g = cria_goban(n, jogador_branco, jogador_preto)
    g_ko = cria_goban(n, jogador_branco, jogador_preto)
    pontuacao = go_aux(g, cria_pedra_preta(), False, g_ko)
    
    # verificacao de quem ganhou
    
    if pontuacao[0] >= pontuacao[1]:
        return True
    return False
