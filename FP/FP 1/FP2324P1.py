def num_intersect(intersect):
    """
    Returns the numerical equivalent of the intersection. num_intersect(("C", 2)) -> (3, 2) 

    :param intersect: tuple
    :return: tuple
    """

    number = ord(intersect[0]) - ord("A") + 1
    num_int = (number, intersect[1])
    return num_int


def format_intersect(intersect):
    """
    Returns the formated version of a numerical intersection. format_intersect((3,2)) -> ("C", 2)

    :param intersect: tuple
    :return:  tuple
    """

    letter = chr(ord("A") + intersect[0] - 1)
    formated_int = (letter, intersect[1])
    return formated_int


def eh_territorio(territory):
    """
    Cheks for valid territory.

    :param territory: tuple
    :return: boolean
    """
    # Checks if the argument is a tuple, and gets the length of the first column.

    if not isinstance(territory, tuple):
        return False
    
    if territory == ():
        return False
    
    if isinstance(territory[0], tuple):
        firs_col_len = len(territory[0])

    # For each column in territory, it checks if it is a tuple,
    # if every element in the column is a 0 or 1, and if all columns have the same size.

    for col in territory:
        if not isinstance(col, tuple):
            return False
        if col == ():
            return False
        for num in col:
            if not isinstance(num, int):
                return False
            if num != 0 and num != 1:
                return False
        if len(col) != firs_col_len:
            return False
    if len(territory) > 26:
        return False
    if len(territory[0]) > 99:
        return False
    return True

        
def obtem_ultima_intersecao(territory):
    """
    Returns the intersection from the top right corner of the territory.

    :param territory: tuple
    :return: tuple 
    """
    # Gets the indexes of the top right corner of the territory, and returns the formated alphabetical form.

    i = len(territory)
    j = len(territory[0])
    last_int = (format_intersect((i, j)))
    return last_int


def eh_intersecao(intersect):
    """
    Checks if the argument is a valid territory

    :param intersect: any
    :return: boolean
    """
    # Checks if the intersection is a tuple, of length 2, containing a string with any uppercase letter of the alphabet,
    # and an integer between 1 and 99

    if not isinstance(intersect, tuple):
        return False
    if len(intersect) != 2:
        return False
    if not isinstance(intersect[0], str):
        return False
    if not isinstance(intersect[1], int):
        return False
    if not len(intersect[0]) == 1:
        return False
    if ord(intersect[0]) < ord("A") or ord(intersect[0]) > ord("Z"):
        return False
    if intersect[1] < 1 or intersect[1] > 99:
        return False
    return True


def eh_intersecao_valida(territory, intersect):
    """
    Cheks if the territory contains the given intersection.

    :param territory: tuple
    :param intersect: tuple
    :return: boolean
    """
    # Compares the upper right intersection of the territory, and if its coordenates are smaller
    # than the intersection passed in the argument returns True, otherwise False

    last_intersect = obtem_ultima_intersecao(territory)

    if ord(last_intersect[0]) < ord(intersect[0]):
        return False
    if last_intersect[1] < intersect[1]:
        return False
    if ord(intersect[0]) < ord("A") or intersect[1] < 1:
        return False
    return True


def eh_intersecao_livre(territory, intersect):
    """
    Checks if the intersection in the territory, is ocupied. 

    :param territory: tuple
    :param intersect: tuple
    :return: boolean
    """
    # Gets the numerical form of the intersection, and using its indexes checks 
    # wheter the corresponding intersection in the territory is 0 or 1

    intersect = num_intersect(intersect)

    if territory[intersect[0] - 1][intersect[1] - 1] == 0:
        return True
    return False


def obtem_intersecoes_adjacentes(territory, intersect):
    """
    Returns the adjacent intersections of the given intersection in the given territory.

    :param territory: tuple
    :param intersect: tuple
    :return: tuple
    """
    
    if not eh_territorio(territory):
        raise ValueError
    if not eh_intersecao_valida(territory, intersect):
        raise ValueError

    # The function gets the intersections above, under, right and left of the intersection given in the argument

    intersect = num_intersect(intersect)
    possible_int = ((intersect[0], intersect[1] - 1),) + ((intersect[0] - 1, intersect[1]),) + ((intersect[0] + 1, intersect[1]),) + ((intersect[0], intersect[1] + 1),)
    adj_int = ()

    # Iterates over each intersection, formats them to numerical form, and checks if they are valid intersections, 
    # if so, it adds them to a tuple and returns the tuple

    for tp in possible_int:
        if eh_intersecao_valida(territory, format_intersect(tp)):
            adj_int += (format_intersect(tp)),
    return adj_int


def ordena_intersecoes(tp_intersect):
    """
    Returs a tuple of sorted intersections.

    :param tp_intersect: tuple
    :return: tuple
    """
    # Transforms all the intersection into the numerical form for easy mannipulation

    num_intersects = ()
    
    for intersect in tp_intersect:
        num_intersects += (num_intersect(intersect),)
    
    # Sorting algorithm

    num_intersects = (sorted(num_intersects, key=lambda x: (x[1], x[0])))

    # Transforms all the sorted intersections back to the alphabetical form
    
    format_ord_intersects = ()

    for intersect in num_intersects:
        format_ord_intersects += (format_intersect(intersect),)
    
    return format_ord_intersects


def territorio_para_str(territory):
    """
    Returns a string representing the territory

    :param territory: tuple
    :return: string
    """


    if not eh_territorio(territory):
        raise ValueError("territorio_para_str: argumento invalido")
    
    t_str = "   "
    n_col = len(territory)
    n_row = len(territory[0])

    # First row of letters

    for i in range(n_col):
        t_str += chr(ord("A") + i) + " "
    t_str = t_str[:-1]
    t_str += "\n"

    # The numbers corresponding to the rows and the montains and blanc spaces

    for j in range(n_row - 1, -1, -1):
        if j >= 9:
            t_str += str(j + 1) + " " 
        else:
            t_str += " " + str(j + 1) + " "
        for i in range(n_col):
            if territory[i][j] == 1:
                t_str += "X "
            else:
                t_str += ". "
        if j >= 9:
            t_str += str(j + 1) + "\n"
        else:
            t_str += " " + str(j + 1) + "\n"
    
    # Last row of letters

    t_str += "   "
    
    for i in range(n_col):
        t_str += chr(ord("A") + i) + " "
    t_str = t_str[:-1]

    return(t_str)


def obtem_cadeia(territory, intersect):
    """
    Returns the chain of intersections (mountains) connected to the given intersection (mountain).

    :param territory: tuple
    :param intersect: tuple
    :return: tuple
    """
    """"""
    def recursive_chain(intersect, chain):    
        """
        For every adjacent intersection, it cheks if it belongs int the chain and if so,
        adds it to the chain and recursively calls itself again with the intersection and the chain as the new arguments.

        :param intersect: tuple
        :param chain: tuple
        :return: tuple
        """
        # Gets the adjacent intersections, checks if they are not in the chain, if not so, adds them and calls
        # itself recursively again

        adj_ints = obtem_intersecoes_adjacentes(territory, intersect)

        for adj_intersect in adj_ints:
            if adj_intersect not in chain:
                if eh_intersecao_livre(territory, adj_intersect) == is_empty:
                    chain += (adj_intersect,)                    
                    chain = recursive_chain(adj_intersect, chain)           
        return chain

    if not eh_territorio(territory):
        raise ValueError("obtem_cadeia: argumentos invalidos")
    if not eh_intersecao(intersect):
        raise ValueError("obtem_cadeia: argumentos invalidos")
    if not eh_intersecao_valida(territory, intersect):
        raise ValueError("obtem_cadeia: argumentos invalidos")

    # Adds the first element to tha chain, which is the intersection, passed into the function,
    # checks if the intersection is empty or not and calls the recursive_chain function.

    tp_chain = (intersect,)

    if eh_intersecao_livre(territory, intersect):
        is_empty = 1
    else:
        is_empty = 0
    """
    Here this function calls recursive_chain, and after that orders the tuple of tulpes with the right intersections. 
    """
    return ordena_intersecoes(recursive_chain(intersect, tp_chain))


def obtem_vale(territory, intersect):
    """
    Returns a tuple with all the valleys of a given mountain chain.

    :param territory: tuple
    :param intersect: tuple
    :return: tuple
    """

    if not eh_territorio(territory):
        raise ValueError("obtem_vale: argumentos invalidos")
    if not eh_intersecao(intersect):
        raise ValueError("obtem_vale: argumentos invalidos")
    if not eh_intersecao_valida(territory, intersect):
        raise ValueError("obtem_vale: argumentos invalidos")
    
    a = num_intersect(intersect)
    
    if territory[a[0] - 1][a[1] - 1] != 1:
        raise ValueError("obtem_vale: argumentos invalidos")

    # It gets the mountain chain, and for the adjacent intersections of every element of the chain,
    # if the element equal to 0, ads it to the valley_chain 

    valley_chain = ()
    chain = obtem_cadeia(territory, intersect)
    
    for mountain in chain:
        adj_ints = obtem_intersecoes_adjacentes(territory, mountain)
        for adj_intersect in adj_ints:
            if adj_intersect not in valley_chain:
                num_adj_intersect = num_intersect(adj_intersect)
                if territory[num_adj_intersect[0] - 1][num_adj_intersect[1] - 1] == 0:
                    valley_chain += (adj_intersect,)
    return(ordena_intersecoes(valley_chain))


def verifica_conexao(territory, intersect1, intersect2):
    """
    Returns True if two given intersections are connected, False if not.

    :param territory: tuple
    :param intersect1: tuple
    :param intersect2: tuple
    :return: boolean
    """

    if not eh_territorio(territory):
        raise ValueError("verifica_conexao: argumentos invalidos")
    if not eh_intersecao(intersect1):
        raise ValueError("verifica_conexao: argumentos invalidos")
    if not eh_intersecao(intersect2):
        raise ValueError("verifica_conexao: argumentos invalidos")
    if not eh_intersecao_valida(territory, intersect1):
        raise ValueError("verifica_conexao: argumentos invalidos")
    if not eh_intersecao_valida(territory, intersect2):
        raise ValueError("verifica_conexao: argumentos invalidos")

    # It gets the chain of the first intersection, if the second intersection is in the chain
    # returns True, otherwise False

    chain = obtem_cadeia(territory, intersect1)
    if intersect2 in chain:
        return True
    return False


def calcula_numero_montanhas(territory):
    """
    Returns the number of mountains in a territory

    :param territory: tuple
    :return: int
    """

    if not eh_territorio(territory):
        raise ValueError("calcula_numero_montanhas: argumento invalido")
    
    # Iterates over all the elements of the terriotry and if they are equal to 1, adds 1 to n_mountains.

    n_mountains = 0
    for col in territory:
        n_mountains += col.count(1)
    return n_mountains


def calcula_numero_cadeias_montanhas(territory):
    """
    Returns the number of mountain chains

    :param territory: tuple
    :return: int
    """

    if not eh_territorio(territory):
        raise ValueError("calcula_numero_cadeias_montanhas: argumento invalido")
    
    n_col = len(territory)
    n_row = len(territory[0])
    n_chains = 0
    all_num_intersect = ()

    # Iterates over all the intersections of the territory, and adds them to a tuple

    for i in range(1, n_col + 1):
        for j in range(1, n_row + 1):
            all_num_intersect += (i,j),
    
    all_formated_intersect = ()

    # Formats all the intersections to the numerical form

    for intersect in all_num_intersect:
       all_formated_intersect += (format_intersect(intersect)),

    seen_intersects = ()

    # For every intersection it gets their chain. Cheks if the chain ahs not already been added to the total,
    # and if not, adds it to seen_chains and adds 1 to n_chains

    for intersect in all_formated_intersect:
        if territory[num_intersect(intersect)[0] - 1][num_intersect(intersect)[1] - 1] == 1:
            if intersect not in seen_intersects:
                n_chains += 1
                chain = obtem_cadeia(territory, intersect)
                seen_intersects += chain
    return n_chains


def calcula_tamanho_vales(territory):
    """
    Returns the length of all the valleys in a territory

    :param territory: tuple
    :return: int
    """

    if not eh_territorio(territory):
        raise ValueError("calcula_tamanho_vales: argumento invalido")
    
    n_col = len(territory)
    n_row = len(territory[0])
    num_mountain_intersects = ()
    formated_mountain_intersects = ()

    # Gets all the mountains in the territory 

    for i in range(n_col):
        for j in range(n_row):
            if territory[i][j] == 1:
                num_mountain_intersects += (i + 1,j + 1),
    
    # Transforms the intersections into the numerical form

    for intersect in num_mountain_intersects:
        formated_mountain_intersects += format_intersect(intersect),

    seen_valleys = ()

    # For each mountain it gets their valleys, compares if they are not in seen_valleys
    # if not so, adds them to seen_valleys.

    for intersect in formated_mountain_intersects:
        valleys = obtem_vale(territory, intersect)
        for valley in valleys:
            if valley not in seen_valleys:
                seen_valleys += (valley),
    return len(seen_valleys)
