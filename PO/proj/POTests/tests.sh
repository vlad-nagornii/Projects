#!/bin/bash

# Diretório onde está o hva.app.App (ajuste se necessário)
test_dir="auto-tests"
expected_dir="${test_dir}/expected"

# Cores para a saída do terminal
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # Sem cor (reseta para a cor padrão)

GROUP="$1"

cd "$GROUP" || { echo "Erro: Grupo $GROUP não encontrado."; exit 1; }
make
cd ../POTests

# Loop para iterar de 01 até 24
for n in $(seq -w 1 35); do

    # Define o prefixo dos arquivos com base no valor atual do teste
    prefix="A-24-${n}-M-ok"
    #prefix="A-01-33-M-ok"
    
    # Criação dos caminhos dos arquivos
    import_file="${test_dir}/${prefix}.import"
    in_file="${test_dir}/${prefix}.in"
    out_file="${expected_dir}/${prefix}.out"

    # Verifica se os arquivos existem
    if [[ ! -f "$in_file" ]]; then
        continue
    fi
    if [[ ! -f "$out_file" ]]; then
        continue
    fi

    # Executa o programa com os arquivos de teste e verificar se existe import ou não
    if [[ ! -f "$import_file" ]]; then
        java -Din=${test_dir}/${prefix}.in -Dout=test.outhyp hva.app.App
    else
        java -Dimport=${test_dir}/${prefix}.import -Din=${test_dir}/${prefix}.in -Dout=test.outhyp hva.app.App
    fi
    
    # Compara a saída gerada com o arquivo de referência
    diff -b ${expected_dir}/${prefix}.out test.outhyp
    
    # Mostra uma mensagem de sucesso ou insucesso
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Test ${n}: Success - No differences${NC}"
    else
        echo -e "${RED}Test ${n}: Failure - Differences found${NC}"
    fi
done

rm -f *.dat