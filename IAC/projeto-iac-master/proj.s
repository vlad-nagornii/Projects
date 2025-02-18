# IAC 2023/2024 k-means
# 
# Grupo: 56
# Campus: Alameda
#
# Autores:
# 110483, Chloe Poinsot Romao
# 110034, Marta Braga
# 110647, Vladislav Nagornii
#
# Tecnico/ULisboa


# ALGUMA INFORMACAO ADICIONAL PARA CADA GRUPO:
# - A "LED matrix" deve ter um tamanho de 32 x 32
# - O input e' definido na seccao .data. 
# - Abaixo propomos alguns inputs possiveis. Para usar um dos inputs propostos, basta descomentar 
#   esse e comentar os restantes.
# - Encorajamos cada grupo a inventar e experimentar outros inputs.
# - Os vetores points e centroids estao na forma x0, y0, x1, y1, ...


# Variaveis em memoria
.data

#Input A - linha inclinada
#n_points:    .word 9
#points:      .word 0,0, 1,1, 2,2, 3,3, 4,4, 5,5, 6,6, 7,7 8,8

#Input B - Cruz
#n_points:    .word 5
#points:     .word 4,2, 5,1, 5,2, 5,3 6,2

#Input C
n_points:    .word 23
points: .word 0,0, 0,1, 0,2, 1,0, 1,1, 1,2, 1,3, 2,0, 2,1, 5,3, 6,2, 6,3, 6,4, 7,2, 7,3, 6,8, 6,9, 7,8, 8,7, 8,8, 8,9, 9,7, 9,8


#Input D
#n_points:    .word 30
#points:      .word 16, 1, 17, 2, 18, 6, 20, 3, 21, 1, 17, 4, 21, 7, 16, 4, 21, 6, 19, 6, 4, 24, 6, 24, 8, 23, 6, 26, 6, 26, 6, 23, 8, 25, 7, 26, 7, 20, 4, 21, 4, 10, 2, 10, 3, 11, 2, 12, 4, 13, 4, 9, 4, 9, 3, 8, 0, 10, 4, 10

#Input E
#n_points: .word 1
#points: .word 1,1

# Valores de centroids e k a usar na 1a parte do projeto:
#centroids:   .word 0,0
#k:           .word 1

# Valores de centroids, k e L a usar na 2a parte do projeto:
centroids:   .word 0, 0, 10, 0, 0, 10
k:           .word 3
L:           .word 10

# Abaixo devem ser declarados o vetor clusters (2a parte) e outras estruturas de dados
# que o grupo considere necessarias para a solucao:
#centroids da ultima iteracao

buf_centroids: .word 0, 0, 0, 0, 0, 0    
    
#clusters:
clusters:    .zero 120

#time:
time:        .zero 4


#Definicoes de cores a usar no projeto 

colors:      .word 0xff0000, 0x00ff00, 0x0000ff  # Cores dos pontos do cluster 0, 1, 2, etc.

.equ         black      0
.equ         white      0xffffff


# Codigo
 
.text

    # Chama funcao principal da 1a parte do projeto
    jal mainSingleCluster

    # Descomentar na 2a parte do projeto:
    #jal mainKMeans
        
    #Termina o programa (chamando chamada sistema)
    li a7, 10
    ecall
    j end


### printPoint
# Pinta o ponto (x,y) na LED matrix com a cor passada por argumento
# Nota: a implementacao desta funcao ja' e' fornecida pelos docentes
# E' uma funcao auxiliar que deve ser chamada pelas funcoes seguintes que pintam a LED matrix.
# Argumentos:
# a0: x
# a1: y
# a2: cor

printPoint:
    li a3, LED_MATRIX_0_HEIGHT
    sub a1, a3, a1
    addi a1, a1, -1
    li a3, LED_MATRIX_0_WIDTH
    mul a3, a3, a1
    add a3, a3, a0
    slli a3, a3, 2
    li a0, LED_MATRIX_0_BASE
    add a3, a3, a0   # addr
    sw a2, 0(a3)
    jr ra

# OPTMIZATION
# A funcao no loop exterior corre 31 vezes e o loop interior corre i vezes
# (i: numero da iteracao atual do loop exterior).
# Assim sendo a funcao e muito mais rapida por pintar os pontos a esquerda e a direita
# ao mesmo tempo, em vez de ter que iterar coordenada a coordenada (31 * 31)

### cleanScreen
# Limpa todos os pontos do ecra
# Argumentos: nenhum
# Retorno: nenhum

cleanScreen:
    addi sp, sp, -24
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw t0, 16(sp)
    sw t1, 20(sp)

    li t0, 31 # x
    li t1, 31 # y
    li a2, white
    
    columns_loopCS:
        blt t0, x0, end_columns_loopCS # se x = 0, todas as colunas foram vistas -> termina o loop
            
        lines_loopCS:
            # Corre todos os pontos correndo a esquerda e a direita ao mesmo tempo
            # em vez de correr os pontos um a um
            sub t2, t1, t0 
            bltz t2, end_lines_loopCS # termina quando o t1 for menor que o t0
            
            # guarda em a0, a1 e a2 as informacoes necessarias para chamar printPoint
            mv a1, t1 # cordenada y
            mv a0, t0 # cordenada x
            jal printPoint # mete a preto o ponto (t0, t1)                
            
            mv a1, t0 # cordenada y
            mv a0, t1 # coordenada x
            jal printPoint # mete a preto o ponto (t1, t0)   
            
            addi t1, t1, -1 # decrementa o y (loop interior)
            j lines_loopCS
            
        end_lines_loopCS:
            addi t0, t0, -1 # decrementa o x (loop exterior)
            addi t1, x0, 31 # repoe o t1 a 31 para comecar a pintar uma nova coluna
            j columns_loopCS
        
    end_columns_loopCS:            
        lw ra, 0(sp)
        lw a0, 4(sp)
        lw a1, 8(sp)
        lw a2, 12(sp)
        lw t0, 16(sp)
        lw t1, 20(sp)
        addi sp, sp, 24
        jr ra

# OPTIMIZATION
# Funcao auxiliar que otimiza o codigo, pois, entre cada iteracao da main, para pintar o ecra com
# outro conjunto de centroids/clusters, basta "apagar" os centroids antigos, em vez de limpar o ecra todo.
# A funcao e substancialmente mais rapida, uma vez que o o "cleanCentroids" itera k vezes
# enquanto o "CleanScreen" necessita sempre de 62 * 62  iteracoes.

### cleanCentroids
# Pinta a branco os antigos centroids
# Argumentos: nenhum
# Retorno: nenhum

cleanCentroids:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw s1, 16(sp)
    sw a4, 20(sp)
    sw t0, 24(sp)
    
    li a2, white
    la s1, buf_centroids # endereco do vetor de centroids
    lw a4, k # numero total de centroids
    li t0, 0 # index do ponto atual
           
    loopCleanCentroids:
        bge t0, a4, end_loopCleanCentroids # continua enquanto ainda nao tiver percorrido todos os antigos centroids
        
       # guarda em a0, a1 e a2 as informacoes necessarias para chamar printPoint
        lw a0, 0(s1) # x
        lw a1, 4(s1) # y
        jal printPoint # pinta a branco o ponto (a0, a1) 
               
        addi s1, s1, 8 # passa para a coordenada seguinte
        addi t0, t0, 1 # incrementa o index atual
        j loopCleanCentroids

    end_loopCleanCentroids:
        lw ra, 0(sp)
        lw a0, 4(sp)
        lw a1, 8(sp)
        lw a2, 12(sp)
        lw s1, 16(sp)
        lw a4, 20(sp)
        lw t0, 24(sp)
        addi sp, sp, 28
        jr ra
        
        
### printClusters
# Pinta os agrupamentos na LED matrix com a cor correspondente.
# Argumentos: nenhum
# Retorno: nenhum

printClusters:
    addi sp, sp, -44
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw s4, 12(sp)
    sw s5, 16(sp)
    sw s6, 20(sp)
    sw a0, 24(sp)
    sw a1, 28(sp)
    sw a2, 32(sp)
    sw t0, 36(sp)
    sw t1, 40(sp)
        
    lw s2, n_points # num total de pontos
    la s3, points # vetor dos pontos
    lw s4, k # num de clusters
    la s6, clusters # endereco dos clusters
    li t0, 0 # contador
    la s5, colors # endereco do vetor de cores
    
        
    loopPC:
        bge t0, s2, end_PC # verifica se todos os pontos foram percorridos
        
        lw t1, 0(s6) # t1 = num do cluster atual
        slli t1, t1, 2 
        add t1, s5, t1 # coloca em t1 o endereco da cor do cluster atual
        
        lw a0, 0(s3) # cordenada x
        lw a1, 4(s3) # cordenada y   
        lw a2, 0(t1) # cor

        jal printPoint

        addi t0, t0, 1 # incrementa o contador 
        addi s3, s3, 8 # passa para a coordenada seguinte
        addi s6, s6, 4 # anda uma posicao para a frente no vetor dos clusters
        j loopPC
            
    end_PC:
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw s4, 12(sp)
        lw s5, 16(sp)
        lw s6, 20(sp)
        lw a0, 24(sp)
        lw a1, 28(sp)
        lw a2, 32(sp)
        lw t0, 36(sp)
        lw t1, 40(sp)
        addi sp, sp, 44
        jr ra


### printCentroids
# Pinta os centroides na LED matrix
# Nota: deve ser usada a cor preta (black) para todos os centroides
# Argumentos: nenhum
# Retorno: nenhum

printCentroids:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw a0, 12(sp)
    sw a1, 16(sp)
    sw a2, 20(sp)
    sw t0, 24(sp)
    
    la s2, centroids
    lw s3, k
    li a2, black 
    li t0, 0 # contador

    loopPrintCentroids:
        bge t0, s3, end_loopPrintCentroids # termina quando tiver percorrido todos os centroids
        
        # faz load dos argumentos necessarios para a chamada da funcao printPoint
        lw a0, 0(s2) # x
        lw a1, 4(s2) # y
        
        jal printPoint

        addi s2, s2, 8 # passa para o centroid seguinte
        addi t0, t0, 1 # incrementa o contador
        j loopPrintCentroids
        
    end_loopPrintCentroids:
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw a0, 12(sp)
        lw a1, 16(sp)
        lw a2, 20(sp)
        lw t0, 24(sp)
        addi sp, sp, 28
        jr ra
    
    
### calculateCentroids
# Calcula os k centroides, a partir da distribuicao atual de pontos associados a cada agrupamento (cluster)
# Nota: destroi o t6
# Argumentos: nenhum
# Retorno: nenhum

calculateCentroids:  
    addi sp, sp, -52
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw s4, 12(sp)
    sw s5, 16(sp)
    sw s6, 20(sp)
    sw a3, 24(sp)
    sw a4, 28(sp)
    sw a5, 32(sp)
    sw a6, 36(sp)
    sw a7, 40(sp)
    sw t0, 44(sp)
    sw t1, 48(sp)
    
    lw s2, n_points # total de pontos
    lw s4, k # num de clusters/centroids
    la s5, centroids # vetor das coordenadas dos centroids
    
    li t1, 0 # indice dos clusters
        
    loopClusters_CC:
        bge t1, s4, end_CC
        
        la s3, points # endereco do vetor de pontos
        la s6, clusters # endereco do vetor dos clusters
        
        li a3, 0 # media x
        li a4, 0 # media y
          
        li t0, 0 # indice dos pontos
        li t6, 0 # num de pontos de cada cluster
        
        loopPoints_CC:
            bge t0, s2, end_loopPointsCC
            lw a5, 0(s6)
            bne a5, t1, differentCluster # caso o cluster for diferente do cluster a ser calculado 
            
            lw a6, 0(s3) # x
            lw a7, 4(s3) # y
            
            add a3, a3, a6 # soma dos valores coordenadas de x
            add a4, a4, a7 # soma dos valores das coordenadas de y
            
            addi t6, t6, 1 # incrementa o numero de pontos do cluster atual
                        
            differentCluster:
                addi t0, t0, 1 # incrementa o indice dos pontos
                addi s6, s6, 4 # avanca o vetor de clusters
                addi s3, s3, 8 # avanca no vetor dos pontos
                j loopPoints_CC
            
       end_loopPointsCC:
           beqz t6, end_CC # se o centroid nao tiver nenhum ponto associado
           div a3, a3, t6 # media de x
           div a4, a4, t6 # media de y
           
           sw a3, 0(s5) # guarda o x do novo centroid no vetor "centroids"
           sw a4, 4(s5) # guarda o y do novo centroid no vetor "centroids"
           addi s5, s5, 8 # avanca no vetor de centroids
           
           addi t1, t1, 1 # incrementa o indice dos clusters
           
           j loopClusters_CC
    end_CC:
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw s4, 12(sp)
        lw s5, 16(sp)
        lw s6, 20(sp)
        lw a3, 24(sp)
        lw a4, 28(sp)
        lw a5, 32(sp)
        lw a6, 36(sp)
        lw a7, 40(sp)
        lw t0, 44(sp)
        lw t1, 48(sp)
        addi sp, sp, 52
        
        # OPTIMIZATION
        # Verifica se o centroid escolhido tem pontos associados.
        # Caso nao tenha, salta para a main e volta a escolher k centroids aleatorios.
        # Esta otimizacao evita que o algoritmo entre em loop com centroids sem pontos associados
        beqz t6, newRandomCentroids
        jr ra

# OPTIMIZATION
# Escolhe, aleatoriamente, um numero entre 0 e o numero maximo de pontos - 1
# Esta funcao optimiza substancialmente o numero de iteracoes que o algoritmo precisa
# de fazer ate chegar a resposta final, porque premite escolher um centroid aleatorio
# que pretenca ao conjunto de pontos. Assim, impede que as coordenadas escolhidas para o 
# primeiro centroid nao estejam associadas a nenhum ponto

###initializeCentroids
# Inicializa os valores do vetor centroids de forma pseudo aleatoria
# Argumentos: nenhum
# Retorno: nenhum

initializeCentroids:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw s0, 4(sp)
    sw s2, 8(sp)
    sw s3, 12(sp)
    sw a1, 16(sp)
    sw a2, 20(sp)
    sw s4, 24(sp)
 
    
    la s2, centroids # endereco do vetor de centroids
    lw s3, k # num de centroids
    li s4, 0 # contador
    la s5, points

    loopInitializeCentroids:
        bge s4, s3, end_loopInitializeCentroids # termina quando tiver inicializado todos os centroids
        
        
        jal random_number # recebe um indice de um ponto no vetor points
        slli s0, s0, 3 # multiplicar por 8
        add s0, s5, s0 # ponto de indice s0 no vetor points
        
        lw a1, 0(s0) # coloca uma coordenada aleatoria em a1
        lw a2, 4(s0) # coloca uma coordenada aleatoria em a2
         
        # verifica se a coordenada (a1, a2) ja existia no vetor de centroids        
        jal repeated_number
    
        # faz store das novas coordenadas aleatorias
        sw a1, 0(s2) # x
        sw a2, 4(s2) # y
    
        addi s2, s2, 8 # passa para o centroid seguinte
        addi s4, s4, 1 # incrementa o contador
        j loopInitializeCentroids
        
    end_loopInitializeCentroids:
        lw ra, 0(sp)
        lw s0, 4(sp)
        lw s2, 8(sp)
        lw s3, 12(sp)
        lw a1, 16(sp)
        lw a2, 20(sp)
        lw s4, 24(sp)
        addi sp, sp, 28
        jr ra


### repeated_number
# Verifica se as coordenadas (a1 e a2) ja se encontram no vetor centroids
# Caso sim volta a chamar random_number e obtem um centroide novo
# Argumentos: 
# a1, a2: (x,y)
# Retorno: 
# a1, a2: (x,y) pseudo aleatorios

repeated_number:
    addi sp, sp, -24
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw t0, 12(sp)
    sw t1, 16(sp)
    sw t2, 20(sp)
    
    la s2, centroids # endereco do vetor dos centroids
    lw s3, k # num de centroids
    li t0, 0 # contador
    
    loopCheckCentroids:
        bge t0, s3, end_loopCheckCentroids # termina se ja tiverem sido percorridos todos os centroids
        lw t1, 0(s2) # x do centroid atual
        lw t2, 4(s2) # y do centroid atual
        
        check_x:
            beq t1, a1 check_y
        
        addi s2, s2, 8 # passa para o centroid seguinte
        addi t0, t0, 1 # incrementa o contador
        j loopCheckCentroids
        
        check_y:
            beq t2, a2, repeated
            addi s2, s2, 8 # passa para o centroid seguinte
            addi t0, t0, 1 # incrementa o contador
            j loopCheckCentroids
        
        end_loopCheckCentroids:
            lw ra, 0(sp)
            lw s2, 4(sp)
            lw s3, 8(sp)
            lw t0, 12(sp)
            lw t1, 16(sp)
            lw t2, 20(sp)
            addi sp, sp, 24
            jr ra
        
        # caso as coordenadas (x,y) ja constem no vetor centroids
        repeated:
            jal get_time
            jal random_number
            mv a1, s0 # novo x aleatorio
            jal random_number
            mv a2, s0 # novo y aleatorio
            
            j end_loopCheckCentroids

 
### get_time
# Guarda na memoria em "time" o tempo atual em milissegundos
# Argumentos: Nenhum
# Retorno: Nenhum  

get_time:
    addi sp, sp, -16
    sw ra, 0(sp)
    sw a7, 4(sp)
    sw t1, 8(sp)
    sw a0, 12(sp)

    li a7, 30 # codigo do evironment call Time_msec      
    ecall # faz o system call para o tempo atual e guarda no registo a0

    la t1, time 
    sw a0, 0(t1) # guarda o tempo na memoria
    
    lw ra, 0(sp)
    lw a7, 4(sp)
    lw t1, 8(sp)
    lw a0, 12(sp)
    addi sp, sp, 16
    jr ra

### random_number
# Calcula um numero pseudo aleatorio de 0 a 31 usando a seguinte formula: Xn = (a * Xn-1 + b) % m
# Argumentos: nenhum
# Retorno: 
# s0: coordenada pseudo aleatoria

random_number:
    addi sp, sp, -24
    sw ra, 0(sp)
    sw s1, 4(sp)
    sw s2, 8(sp)
    sw s3, 12(sp)
    sw s4, 16(sp)
    sw t0, 20(sp)
    
    lw s1, time # load do tempo atual
    
    li s2, 7 # a
    li s3, 29 # b
    lw s4, n_points # m
    addi s4, s4, -1
    
    mul s1, s1, s2 # a * Xn-1
    add s1, s1, s3 # a * Xn-1 + b
    
    mv s0, s1 # variavel temporaria
    
    remu s1, s1, s4  #Xn = (a * Xn-1 + b) % m
    
    la t0, time 
    sw s0, 0(t0) # atualiza o tempo (Xn-1)
    mv s0, s1
    
    lw ra, 0(sp)
    lw s1, 4(sp)
    lw s2, 8(sp)
    lw s3, 12(sp)
    lw s4, 16(sp)
    lw t0, 20(sp)
    addi sp, sp, 24
    jr ra
    
    
### manhattanDistance
# Calcula a distancia de Manhattan entre (x0,y0) e (x1,y1)
# Argumentos:
# a0, a1: x0, y0
# a2, a3: x1, y1
# Retorno:
# a0: distance

manhattanDistance:
    addi sp, sp, -20
    sw ra, 0(sp)
    sw a1, 4(sp)
    sw a2, 8(sp)
    sw a3, 12(sp)
    sw t0, 16(sp)

    # subtrai as coordenadas x e soma o valor abosoluto
    sub t0, a0, a2
    jal absolute
    add a0, t0, x0
    
    # subtrai as coordenadas y e soma o valor abosoluto
    sub t0, a1, a3
    jal absolute
    add a0, t0, a0
    
    lw ra, 0(sp)
    lw a1, 4(sp)
    lw a2, 8(sp)
    lw a3, 12(sp)
    lw t0, 16(sp)
    addi sp, sp, 20
    jr ra
    
    absolute:
        # obtem o valor absoluto da subtracao a0 - a1 e a1 - a3
        
        bge t0, x0, end_absolute # se t0 for positivo salta a frente
        neg t0, t0 # se t0 for negativo troca o sinal
        
        end_absolute:
            jr ra
     

### nearestCluster
# Determina o centroide mais perto de um dado ponto (x,y).
# Argumentos:
# a0, a1: (x, y) point
# Retorno:
# a0: cluster index

nearestCluster:
    addi sp, sp, -32
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw a3, 12(sp)
    sw a4, 16(sp)
    sw a5, 20(sp)
    sw a6, 24(sp)
    sw a7, 28(sp)

    la s2, centroids
    lw s3, k
    
    mv a4, a0 # copia do valor de x
    li a5, 0 # indice atual
    li a6, 64 # 32 + 32 = 64, distancia de manhattan maxima
    li a7, 0 # indice da menor distancia
    
    loopNearestCluster:
        bge a5, s3, end_loopNearestCluster # termina quando tiver percorrido todos os centroids
        
        mv a0, a4 # volta a meter o x original
        
        lw a2, 0(s2) # x
        lw a3, 4(s2) # y
        
        jal manhattanDistance
        
        blt a0, a6, updateDistance # caso a distancia calculada for menor a distancia em a7
    back:
        addi s2, s2, 8 # passa para o centroid seguinte
        addi a5, a5, 1 # incrementa o indice
        j loopNearestCluster
        
    updateDistance:
        mv a6, a0 # update a menor distancia de manhattan
        mv a7, a5 # update ao indice
        j back
    
    end_loopNearestCluster:
        mv a0, a7 # move o indice para o registo de retorno
        
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw a3, 12(sp)
        lw a4, 16(sp)
        lw a5, 20(sp)
        lw a6, 24(sp)
        lw a7, 28(sp)
        addi sp, sp, 32
        jr ra
    
### SetClusters
# Define a que cluster pertence cada ponto, em funcao da distancia entre o seu centroid e o ponto.
# Guarda no vetor clusters o indice do cluster associado cada ponto, por ordem.
# Argumentos: nenhum
# Retorno: nenhum
setClusters:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw s4, 12(sp)
    sw t0, 16(sp)
    sw a0, 20(sp)
    sw a1, 24 (sp)
    
    lw s2, n_points
    la s3, points
    la s4, clusters
    li t0, 0 # contador dos pontos
    
    loopSetClusters:
        bge t0, s2, end_loopSetClusters # corre o loop enquanto nao tiver percorrido todos os pontos
        lw a0, 0(s3)
        lw a1, 4(s3)
        jal nearestCluster # devolve no a0 o indice do cluster ao qual o ponto vai pertencer
        sw a0, 0(s4) # guarda o indice no vetor clusters
  
        addi s3, s3, 8 # passa para as cordenadas do proximo ponto
        addi s4, s4, 4 # passa para a proxima posicao do vetor clusters
        addi t0, t0, 1 # incrementa o contador dos pontos
        j loopSetClusters

    end_loopSetClusters:    
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw s4, 12(sp)
        lw t0, 16(sp)
        lw a0, 20(sp)
        lw a1, 24 (sp)
        addi sp, sp, 28
        jr ra
        
### CompareCentroids
# Compara os vetores centroids (iteracao atual) e buf_centroids (iteracao passada)
# Argumentos: nenhum
# Retorno: a0: 0 se forem iguais, 
             # 1 se forem diferentes
compareCentroids: 
    addi sp, sp, -24
    sw ra, 0(sp)
    sw a1, 4(sp)
    sw a2, 8(sp)
    sw s1, 12(sp)
    sw t2, 16(sp)
    sw t0, 20(sp)
    
 
    li a0, 0 # set da flag em verdadeiro
    la a1, buf_centroids
    la a2, centroids
    lw s1, k # numero de centroids
    li t2, 0 # contador de centroids
    
    loopCompareCentroids:
        bge t2, s1, end_loopCompareCentroids # corre o loop enquanto nao tiver percorrido todos os centroids
        
        lw t0, 0(a1)
        lw t1, 0(a2)
        bne t0, t1, setFlag # compara os x
        
        lw t0, 4(a1)
        lw t1, 4(a2)
        bne t0, t1, setFlag # compara os y
        
        addi a1, a1, 8 # passa para as proximas cordenadas no vetor buf_centroids
        addi a2, a2, 8 # passa para as proximas cordenadas no vetor centroids
        addi t2, t2, 1 # incrementa o contador de centroids
        j loopCompareCentroids
    
    setFlag:
        li a0, 1 # muda a flag para falso
        
    end_loopCompareCentroids:
        lw ra, 0(sp)
        lw a1, 4(sp)
        lw a2, 8(sp)
        lw s1, 12(sp)
        lw t2, 16(sp)
        lw t0, 20(sp)
        addi sp, sp, 24
        jr ra

### SaveCentroids
# Cria uma copia dos centroids atuais no buf_centroids
# Argumentos: nenhum
# Retorno: nenhum
      
saveCentroids:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw s1, 12(sp)
    sw t0, 16(sp)
    sw t1, 20(sp)
    sw t2, 24(sp)
    
    la a0, centroids
    la a1, buf_centroids
    lw s1, k # numero de centroids
    li t0, 0 # contador do numero de centroids
    
    loopSaveCentroids:
        beq t0, s1, end_loopSaveCentroids # corre o loop enquanto nao tiver percorrido todos os centroids
        lw t1, 0(a0) # x
        lw t2, 4(a0) # y
        
        sw t1, 0(a1) # x
        sw t2, 4(a1) # y
        
        addi a0, a0, 8 # passa para as proximas cordenadas no vetor centroids
        addi a1, a1, 8 # passa para as proximas cordenadas no vetor buf_centroids
        addi t0, t0, 1 # incrementa o contador do numero de centroids
        j loopSaveCentroids
        
    end_loopSaveCentroids:
        lw ra, 0(sp)
        lw a0, 4(sp)
        lw a1, 8(sp)
        lw s1, 12(sp)
        lw t0, 16(sp)
        lw t1, 20(sp)
        lw t2, 24(sp)
        addi sp, sp, 28
        jr ra


### mainSingleCluster
# Funcao principal da 1a parte do projeto.
# Argumentos: nenhum
# Retorno: nenhum

mainSingleCluster:
    addi sp, sp, -4
    sw ra, 0(sp)

    #1. Coloca k=1 (caso nao esteja a 1)
    la t0, k
    addi t1, x0, 1
    sw t1, 0(t0) 
    
    # inicializar o centroid a (0,0)
    la t2, centroids
    sw x0, 0(t2)
    sw x0, 4(t2)

    #2. cleanScreen
    jal cleanScreen
    
    jal setClusters
    
    #3. printClusters
    jal printClusters
    
    #4. calculateCentroids
    jal calculateCentroids

    #5. printCentroids
    jal printCentroids
   
    #6. Termina
    lw ra, 0(sp)
    addi sp, sp, 4
    jr ra

### mainKMeans
# Executa o algoritmo k-means.
# Argumentos: nenhum
# Retorno: nenhum

mainKMeans:
    # guarda na stack o endereco de retorno
    addi sp, sp, -4
    sw ra, 0(sp)
    
    lw s1, L
    li t0, 0 # contador de iteracoes (no max igual a L)
    li t3, 1
    
    jal cleanScreen
    newRandomCentroids:
    jal get_time
    jal initializeCentroids

    loopKMeans:   
    bge t0, s1, end_loopKMeans # condicao de paragem: ja executou L iteracoes
    jal cleanCentroids
    jal setClusters
    jal printClusters
    jal printCentroids
    
    jal saveCentroids
    jal calculateCentroids
    jal compareCentroids
    beq a0, x0, end_loopKMeans # condicao de paragem: os centroids da ultima iteracao e os da atuais sao iguais
    
    addi t0, t0, 1
    j loopKMeans
    
    end_loopKMeans:
    lw ra, 0(sp)
    addi sp, sp, 4
    jr ra
     
end:
    nop
