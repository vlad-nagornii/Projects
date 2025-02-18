#
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
#n_points:    .word 23
#points: .word 0,0, 0,1, 0,2, 1,0, 1,1, 1,2, 1,3, 2,0, 2,1, 5,3, 6,2, 6,3, 6,4, 7,2, 7,3, 6,8, 6,9, 7,8, 8,7, 8,8, 8,9, 9,7, 9,8

#Input D
n_points:    .word 30
points:      .word 16, 1, 17, 2, 18, 6, 20, 3, 21, 1, 17, 4, 21, 7, 16, 4, 21, 6, 19, 6, 4, 24, 6, 24, 8, 23, 6, 26, 6, 26, 6, 23, 8, 25, 7, 26, 7, 20, 4, 21, 4, 10, 2, 10, 3, 11, 2, 12, 4, 13, 4, 9, 4, 9, 3, 8, 0, 10, 4, 10

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
    #jal test
    # Chama funcao principal da 1a parte do projeto
    #jal mainSingleCluster

    # Descomentar na 2a parte do projeto:
    jal mainKMeans
        
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
    

### cleanScreen
# Limpa todos os pontos do ecra
# Argumentos: nenhum
# Retorno: nenhum

cleanScreen:
    # guarda na stack os registos a serem usados 
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
            blt t1, x0, end_lines_loopCS # se t0 = 0, termina o loop quando terminar de ver a linha
                
            # guarda em a0, a1 e a2 as informacoes necessarias para chamar printPoint
            mv a1, t1 # cordenada y
            mv a0, t0 # cordenada x
            jal printPoint # mete a preto o ponto (t0, t1)                

            addi t1, t1, -1 # decrementa o y (loop interior)
            j lines_loopCS
            
        end_lines_loopCS:
            addi t0, t0, -1 # decrementa o x (loop exterior)
            addi t1, x0, 31 # repoe o t1 a 31 para comecar a pintar uma nova coluna
            j columns_loopCS
        
    end_columns_loopCS:            
        # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
        lw ra, 0(sp)
        lw a0, 4(sp)
        lw a1, 8(sp)
        lw a2, 12(sp)
        lw t0, 16(sp)
        lw t1, 20(sp)
        addi sp, sp, 24
        jr ra

# OPTIMIZATION
### cleanPoints
# Pinta a branco os antigos centroids
# Argumentos: nenhum
# Retorno: nenhum
cleanPoints:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw s1, 16(sp)
    sw a4, 20(sp)
    sw t0, 24(sp)
    
    li a2, white
    la s1, centroids # endereco do vetor de centroids
    lw a4, k # numero total de centroids
    li t0, 0 # index do ponto atual
           
    loopCleanPoints:
        bge t0, a4, end_loopCleanPoints # continua enquanto ainda nao tiver percorrido todos os antigos centroids
        
       # guarda em a0, a1 e a2 as informacoes necessarias para chamar printPoint
        lw a0, 0(s1) # x
        lw a1, 4(s1) # y
        jal printPoint # pinta a branco o ponto (a0, a1) 
               
        addi s1, s1, 8 # passa para a coordenada seguinte
        addi t0, t0, 1 # incrementa o index atual
        j loopCleanPoints

    end_loopCleanPoints:
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
    # guarda na stack os registos a serem usados
    addi sp, sp, -48
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
    sw t2, 44(sp)
        
    lw s2, n_points # vetor dos pontos
    la s3, points # num total de pontos
    lw s4, k # num de clusters
    la s6, clusters # endereco dos clusters
    li t0, 0 # contador
    li t2, 1 # auxiliar
    la s5, colors # endereco do vetor de cores
    
    bgt s4, t2, loopMultiplePC
     
    lw a2, 0(s5)
    
    # percorre todos os pontos de um cluster
    loopSinglePC:
        bge t0, s2, end_PC  # corre o loop enquanto que o contador (t0) for menor que o num de pontos
   
        # coloca em a0 e a1 os argumentos necessarios para chamar printPoint
        lw a0, 0(s3) # cordenada x
        lw a1, 4(s3) # cordenada y 
        jal printPoint

        addi s3, s3, 8 # passa para a coordenada seguinte
        addi t0, t0, 1 # incrementa o contador 
        j loopSinglePC
        
    loopMultiplePC:
        bge t0, s2, end_PC # verfica se todos os pontos foram percorridos
        
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
        j loopMultiplePC
            
    end_PC:
        # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
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
        lw t2, 44(sp)
        addi sp, sp, 48
        jr ra


### printCentroids
# Pinta os centroides na LED matrix
# Nota: deve ser usada a cor preta (black) para todos os centroides
# Argumentos: nenhum
# Retorno: nenhum

printCentroids:
    # guarda na stack os registros a serem usados (ra, s2, s3, a0, a1)
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
       # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
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
# Argumentos: nenhum
# Retorno: nenhum

calculateCentroids:  
    # guarda na stack os registos a serem usados 
    addi sp, sp, -60
    sw ra, 0(sp)
    sw s2, 4(sp)
    sw s3, 8(sp)
    sw s4, 12(sp)
    sw a3, 16(sp)
    sw a4, 20(sp)
    sw a5, 24(sp)
    sw a6, 28(sp)
    sw t0, 32(sp)
    sw t1, 36(sp)
    sw t2, 40(sp)
    sw t3, 44(sp)
    sw t4, 48(sp)
    sw t5, 52(sp)
    sw t6, 56(sp)
    
    lw s2, n_points # total de pontos
    la s3, points # endereco do vetor de pontos
    lw s4, k # num de clusters/centroids
    la a5, centroids # vetor das coordenadas dos centroids
    
    li a3, 0 # media x
    li a4, 0 # media y  
    
    li t0, 0 # indice dos pontos
    li t1, 0 # contador clusters
    li t2, 1 # auxiliar
    
    bgt s4, t2, loopMultipleCC_Clusters # verifica se ha mais que um cluster
        
    loopSingleCC:
        bge t0, s2, end_loopSingleCC # corre o loop enquanto que o contador for menor que o num de pontos
        
        lw t3, 0(s3) # x
        lw t4, 4(s3) # y
        add a3, a3, t3 # soma valores de x
        add a4, a4, t4 # soma valores de y
            
        addi s3, s3, 8 # percorre as cordenadas
        addi t0, t0, 1 # incrementa o contador de pontos
        j loopSingleCC
            
    end_loopSingleCC:
        div a3, a3, s2 # media de x
        div a4, a4, s2 # media de y
        
        # regista as coordenadas do centroid na lista de centroids (a5)
        sw a3, 0(a5) # x
        sw a4, 4(a5) # y
        
    j end_CC
    
    # percorre o vetor dos clusters
    loopMultipleCC_Clusters:
            bge t1, s4, end_CC # termina quando tiver percorrido todos os clusters
            
            la a6, clusters # endereco do vetor de clusters
            
            addi a6, a6, -4 # o vetor dos clusters comeca um para tras 
                            # para depois no inicio do loop quando iterar comecar na posicao 0
                            
            li t6, 0 # contador de pontos pertencentes ao clusters
            li t0, -1 # indice dos pontos
            la s3, points # endereco dos vetor dos pontos
            addi s3, s3, -8 # comeca atras para quando incrementar pelo primeira vez no inicio do loop, estar na posicao 0
            
            # percorre o vetor dos pontos
            loopMultipleCC_Points:
                addi a6, a6, 4 # passa para o espaco seguinte no vetor dos clusters
                addi s3, s3, 8 # passa para as coordenadas seguintes
                addi t0, t0, 1 # incrementa num de pontos percorridos
                
                bge t0, s2, end_loopMultipleCC_Points # se ja tiverem percorrido todos os pontos, o loop termina
                                                      # (se o t0 (num de pontos percorridos) = s2 (num total de pontos))                
                lw t3, 0(a6)
                bne t3, t1, loopMultipleCC_Points # nao calcula media se o ponto atual (t1)
                                                  # nao pretencer ao cluster (t3) atual
                
                lw t4, 0(s3) # x
                lw t5, 4(s3) # y
                add a3, a3, t3 # soma valores de x
                add a4, a4, t4 # soma valores de y
                addi t6, t6, 1 # incrementar contador pontos pertencentes cluster atual
                j loopMultipleCC_Points
            
            end_loopMultipleCC_Points:
                div a3, a3, t6 # media de x
                div a4, a4, t6 # media de y
                
                # regista as coordenadas do centroid na lista de centroids (a5)
                sw a3, 0(a5) # x
                sw a4, 4(a5) # y
                addi a5, a5, 8 # anda para a frente com o vetor dos centroids

                addi t1, t1, 1 # incrementa o num de clusters percorridos
                j loopMultipleCC_Clusters
                
    
    end_CC:        
        # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
        lw ra, 0(sp)
        lw s2, 4(sp)
        lw s3, 8(sp)
        lw s4, 12(sp)
        lw a3, 16(sp)
        sw a4, 20(sp)
        lw a5, 24(sp)
        lw a6, 28(sp)
        lw t0, 32(sp)
        lw t1, 36(sp)
        lw t2, 40(sp)
        lw t3, 44(sp)
        lw t4, 48(sp)
        lw t5, 52(sp)
        lw t6, 56(sp)
        addi sp, sp, 60
    
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

    #2. cleanScreen
    jal cleanScreen
    
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



###initializeCentroids
# Inicializa os valores do vetor centroids de forma pseudo aleatoria
# Argumentos: nenhum
# Retorno: nenhum

initializeCentroids:
    # guarda na stack os registos a serem usados 
    addi sp, sp, -36
    sw ra, 0(sp)
    sw s0, 4(sp)
    sw s2, 8(sp)
    sw s3, 12(sp)
    sw a0, 16(sp)
    sw a7, 20(sp)
    sw a1, 24(sp)
    sw a2, 28(sp)
    sw s4, 32(sp)
    
    la s2, centroids # endereco do vetor de centroids
    lw s3, k # num de centroids
    li s4, 0 # contador

    loopInitializeCentroids:
        bge s4, s3, end_loopInitializeCentroids # termina quando tiver inicializado todos os centroids
        
        jal random_number
        mv a1, s0 # coloca uma coordenada aleatoria em a1
        jal random_number
        mv a2, s0 # coloca uma coordenada aleatoria em a2
         
        # verifica se a coordenada (a1, a2) ja existia no vetor de centroids        
        jal repeated_number
    
        # faz store das novas coordenadas aleatorias
        sw a1, 0(s2) # x
        sw a2, 4(s2) # y
    
        addi s2, s2, 8 # passa para o centroid seguinte
        addi s4, s4, 1 # incrementa o contador
        j loopInitializeCentroids
        
    end_loopInitializeCentroids:
        # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
        lw ra, 0(sp)
        lw s0, 4(sp)
        lw s2, 8(sp)
        lw s3, 12(sp)
        lw a0, 16(sp)
        lw a7, 20(sp)
        lw a1, 24(sp)
        lw a2, 28(sp)
        lw s4, 32(sp)
        addi sp, sp, 36
        jr ra


### repeated_number
# Verifica se as coordenadas (a1 e a2) ja constam no vetor centroids
# Caso sim volta a chamar random_number com um time novo
# Argumentos: 
# a1, a2: (x,y)
# Retorno: 
# a1, a2: (x,y) pseudo aleatorios

repeated_number:
    # guarda na stack os registos a serem usados
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
            # devolve as variaveis o seu conteudo antes da chamada inicial da funcao
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

   
get_time:
    addi sp, sp, -16
    sw ra, 0(sp)
    sw a7, 4(sp)
    sw t1, 8(sp)
    sw a0, 12(sp)


    li a7, 30       
    ecall # faz o system call para o tempo atual e guarda no registo a0

    la t1, time
    sw a0, 0(t1)
    
    lw ra, 0(sp)
    lw a7, 4(sp)
    lw t1, 8(sp)
    lw a0, 12(sp)
    addi sp, sp, 16
    jr ra
 
### random_number
# Calcula um numero pseudo aleatorio de 0 a 31 com LFSR
# Argumentos: nenhum
# Retorno: 
# s0: coordenada pseudo aleatoria
random_number:
    # Xn = (a * Xn-1 + b) % m.
    # poem um numero random de 0 a 31 em s0
    addi sp, sp, -20
    sw ra, 0(sp)
    sw s1, 4(sp)
    sw s2, 8(sp)
    sw s3, 12(sp)
    sw s4, 16(sp)
    
    la s0, time # load do tempo para s0
    lw s1, 0(s0)
    
    li s2, 7 # a
    li s3, 29 # b
    li s4, 31
    
    mul s1, s1, s2 # a * Xn-1
    add s1, s1, s3 # a * Xn-1 + b
    
    mv s0, s1
    
    remu s1, s1, s4   
    
    la t0, time 
    sw s0, 0(t0)
    mv s0, s1
    lw ra, 0(sp)
    lw s1, 4(sp)
    lw s2, 8(sp)
    lw s3, 12(sp)
    lw s4, 16(sp)
    addi sp, sp, 20
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
    # guarda na stack os registos a serem usados
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
        
        blt a0, a6, updateDistance
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
    li t0, 0 # contador
    
    loopSetClusters:
        bge t0, s2, end_loopMainKMeans
        lw a0, 0(s3)
        lw a1, 4(s3)
        jal nearestCluster
        sw a0, 0(s4)
  
        addi s3, s3, 8
        addi s4, s4, 4
        addi t0, t0, 1 
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
        
compareCentroids:
    addi sp, sp, -24
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw s1, 16(sp)
    sw t2, 20(sp)
    
 
    li a0, 0 # flag, set da flag em verdadeiro inicialmente
    la a1, buf_centroids
    la a2, centroids
    lw s1, k
    li t2, 0 # contador
    
    loopCompareCentroids:
        bge t2, s1, end_loopCompareCentroids
        
        lw t0, 0(a1)
        lw t1, 0(a2)
        bne t0, t1, setFlag
        
        addi a0, a0, 4
        addi a1, a1, 4
        addi t2, t2, 1
        j loopCompareCentroids
    
    setFlag:
        li a0, 1
        
    end_loopCompareCentroids:
        sw ra, 0(sp)
        sw a0, 4(sp)
        sw a1, 8(sp)
        sw a2, 12(sp)
        sw s1, 16(sp)
        sw t2, 20(sp)
        addi sp, sp, 24
        
resetCentroids:
    addi sp, sp, -24
    sw ra, 0(sp)
    sw a0, 4(sp)
    sw a1, 8(sp)
    sw s1, 12(sp)
    sw t0, 16(sp)
    sw t1, 20(sp)
    
    la a0, centroids
    la a1, buf_centroids
    lw s1, k
    li t0, 0 # contador
    
    loopResetCentroids:
        beq t0, s1, end_loopResetCentroids
        lw t1, 0(a0)
        sw t1, 0(a1)
        
        addi a0, a0, 4
        addi a1, a1, 4
        addi t0, t0, 1
        j loopResetCentroids
        
    end_loopResetCentroids:
        lw ra, 0(sp)
        lw a0, 4(sp)
        lw a1, 8(sp)
        lw s1, 12(sp)
        lw t0, 16(sp)
        lw t1, 20(sp)
        addi sp, sp, 24
        jr ra

test:
    addi sp, sp, -4
    sw ra, 0(sp)
    
    jal get_time
    
    jal cleanScreen
    
    jal initializeCentroids
    
    jal printCentroids
    
    
   # definicao de clusters #PODEMOS POR ISTO NUMA FUNCAO AUXILIAR # ACRESCENTEI
    lw s2, n_points
    la s3, points
    la s4, clusters
    li t0, 0 # contador
    
    loopMainKMeans:
        bge t0, s2, end_loopMainKMeans
        lw a0, 0(s3)
        lw a1, 4(s3)
        jal nearestCluster
        sw a0, 0(s4) # VALE A PENA lw a0, 4(s4)?
                    # ????????????????? OPTMIZATION        
        addi s3, s3, 8
        addi s4, s4, 4
        addi t0, t0, 1 
        j loopMainKMeans

    end_loopMainKMeans:
    nop
    # fim de definicao de clusters
    
    jal printClusters
    jal cleanPoints
    
    jal printCentroids

    
    lw ra, 0(sp)
    addi sp, sp, 4
    jr ra
    
### mainKMeans
# Executa o algoritmo k-means.
# Argumentos: nenhum
# Retorno: nenhum

mainKMeans:
    addi sp, sp, -4
    sw ra, 0(sp)
    
    lw s1, L
    li t0, 0 # contador de iteracoes (no max igual a L)
    li t3, 1
    
    jal cleanScreen
    jal get_time
    jal initializeCentroids
    jal setClusters
    jal resetCentroids
    jal calculateCentroids
    jal printCentroids
    jal printClusters
    
    loopKMeans:
        
    bge t0, s1, end_loopKMeans
    jal calculateCentroids
    jal compareCentroids
    beq a0, x0, end_loopKMeans #ou bne a0, x0, end_loopKMeans #pode ser menos preciso, nao preciso de um registro extra, ou senao so beq mm mas mudar flag na funcao
    
    jal cleanPoints
    #jal cleanScreen
    jal setClusters
    jal calculateCentroids
    jal printCentroids
    jal printClusters
    
    addi t0, t0, 1
    j loopKMeans
    
    end_loopKMeans:
    lw ra, 0(sp)
    addi sp, sp, 4
    jr ra
     
end:
    nop