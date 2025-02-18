# TESTS
* A-01-01-M-ok - Abrir aplicação sem import e ver os menus todos
* A-01-02-M-ok - Abrir aplicação com Habitat vazio e vê e guardar em app01.dat
* A-01-03-M-ok - Abrir aplicação vazia, carrega app01.dat e vê Habitats
* A-01-04-M-ok - Abrir aplicação com import com vários habitats ordenados
* A-01-05-M-ok - Abrir aplicação com import com vários habitats desordenados
* A-01-06-M-ok - Abrir aplicação com import com vários habitats desordenados e com maiúsculas/minúsculas
* A-01-07-M-ok - Abrir aplicação com import com um habitat e um animal
* A-01-08-M-ok - Abrir aplicação com import com um habitat e vários animais ordenados
* A-01-09-M-ok - Abrir aplicação com import com vários habitats e vários animais ordenados
* A-01-10-M-ok - Abrir aplicação com import com vários habitats e vários animais de diferentes espécies ordenados
* A-01-11-M-ok - Abrir aplicação com import com um habitat e vários animais desordenados
* A-01-12-M-ok - Abrir aplicação com import com vários habitats e vários animais desordenados
* A-01-13-M-ok - Abrir aplicação com import com vários habitats, animais e espécies ordenados e guardar em app01.dat
* A-01-14-M-ok - Abrir aplicação vazia, carrega app01.dat e vê Habitats
* A-01-15-M-ok - Abrir aplicação com import com um tratador
* A-01-16-M-ok - Abrir aplicação com import com vários tratadores
* A-01-17-M-ok - Abrir aplicação com import com vários veterinários
* A-01-18-M-ok - Abrir aplicação com import com vários funcionários
* A-01-19-M-ok - Abrir aplicação com import com vários funcionários não ordenados
* A-01-20-M-ok - Abrir aplicação com import com vários funcionárions com maiúsculas/minúscula
* A-01-21-M-ok - Abrir aplicação vazia e ver habitas, animais, funcionários e vacinas
* A-01-22-M-ok - Abrir aplicação com import com uma vacina sem espécies
* A-01-23-M-ok - Abrir aplicação com import com uma vacina com uma  espécie
* A-01-24-M-ok - Abrir aplicação com import com várias vacinas e espécies ordenadas
* A-01-25-M-ok - Abrir aplicação com import com várias vacinas desordenadas e espécies ordenadas
* A-01-26-M-ok - Abrir aplicação com import com várias vacinas e espécies desordenadas
* A-01-27-M-ok - Abrir aplicação com import, Abrir ap01.dat e guardar estado anterior em ap02.dat
* A-01-28-M-ok - Abrir aplicação com import, Guardar em ap02.dat e abrir ap01.dat
* A-01-29-M-ok - Abrir aplicação com import com funcionários,vacinas, animais e habitats e guardat em ap01.dat
* A-01-30-M-ok - Abrir ap01.dat e ver tudo
* A-01-31-M-ok - Abrir aplicação com import com animal, habitat, vacina, espécie e funcionário com o mesmo id
* A-01-32-M-ok - Abrir aplicação com import com funcionários com várias responsabilidades 
* A-01-33-M-ok - Abrir aplicação com import com habitats com várias árvores

* A-02-01-M-ok - Vê lista de habitats vazia 
* A-02-02-M-ok - Vê lista de habitats com um habitat de import 
* A-02-03-M-ok - Vê lista de habitats com vários habitat de import ordenados 
* A-02-06-M-ok - Vê lista de habitats com árvores e sem árvores 

* A-03-01-M-ok - Registar habitat e ver
* A-03-02-M-ok - Registar vários habitat com diferentes id's e ver   
* A-03-03-M-ok - Registar 2º habitat com o mesmo id 

* A-04-01-M-ok - Alterar área de habitat existente  
* A-04-02-M-ok - Alterar área de habitat inexistente 

* A-05-01-M-ok - referir espécie não existente e habitat não existente

* A-06-01-M-ok - Avançar estação duas vezes 
* A-06-02-M-ok - Avançar estação cinco vezes, guardar em ap03.dat 
* A-06-03-M-ok - Abrir ap03.dat e avançar estação 

* A-07-01-M-ok - Mostrar árvores de habitat não existente e de habitat sem árvores
* A-07-02-M-ok - Plantar árvore em hotel sem habitats  
* A-07-03-M-ok - Plantar árvore em habitat não existente
* A-07-04-M-ok - Plantar árvore em habitat existente e ver
* A-07-05-M-ok - Plantar árvore em habitat existente com id duplicado no mesmo habitat e ver   
* A-07-09-M-ok - Plantar várias árvores ordenadas em habitat e ver árvores de habitat
* A-07-10-M-ok - Plantar várias árvores ordenadas em habitat e visualizar habitat
* A-07-12-M-ok - plantar árvore perene e ver que o ciclo biológico altera com alteração da estação 

* A-08-01-M-ok - Ver satisfação de hotel vazio 
* A-08-02-M-ok - Ver satisfação de hotel com animais sem funcionários 

* A-09-01-M-ok  - Registar animal de espécie existente 
* A-09-02-M-ok  - Registar animal de espécie existente mas com habitat não existente
* A-09-03-M-ok  - Registar animal de espécie existente e id duplicado no mesmo habitat 
* A-09-08-M-ok  - Visualizar  sem animais e habitat, Registar habitats e visualizar, registar animal e visualizar

* A-10-01-M-ok  - Transferir animal não existente 
* A-10-02-M-ok  - Transferir animal existente para habitat não existente 
* A-10-03-M-ok  - Transferir animal existente para habitat existente 
* A-10-04-M-ok  - Transferir animal existente para habitat vazio e transferir de habitat só com um animal

* A-11-01-M-ok -Calcular satisfação de animal não existente 
* A-11-02-M-ok -Calcular satisfação de animal único no habitat/Hotel com influência neutra
* A-11-04-M-ok -Calcular satisfação de animal único no habitat com influência positiva 
* A-11-05-M-ok -Calcular satisfação de animal único no habitat com influência positiva e passar a neutra 
* A-11-08-M-ok -Calcular satisfação de animal em habitat, com vários animais da mesma espécie 
* A-11-11-M-ok -Calcular satisfação de animal único no habitat após alterar influência de outra espécie no habitat
* A-11-12-M-ok -Calcular satisfação de animal único no habitat após alterar influência da espécie noutro habitat
* A-11-14-M-ok -Calcular satisfação de animal com vários animais da mesma espécie  noutro habitat

* A-16-01-M-ok - Regista Tratador com novo id em hotel vazio
* A-16-02-M-ok - Regista Vet com novo id 
* A-16-03-M-ok - Regista Vet/Trt  com id duplicado  

* A-17-02-M-ok - atribui responsabilidade existente a TRT/VET não existente 
* A-17-04-M-ok - atribui responsabilidade existente a TRT existente  
* A-17-05-M-ok - atribui responsabilidade existente a VET existente  
* A-17-06-M-ok - atribui várias responsabilidades existente por ordem a vários TRT/VET existente 
* A-17-08-M-ok - atribui várias responsabilidades existente ordenadas via import a um TRT/VET existente 

* A-18-02-M-ok - retirar responsabilidade existente a TRT/VET existente 
* A-18-04-M-ok - retirar responsabilidade existente a TRT existente  
* A-18-05-M-ok - retirar responsabilidade existente a VET existente  

* A-19-01-M-ok - Satisfação de Vet sem espécies e Vet inexistente
* A-19-02-M-ok - Satisfação de Vet com uma espécie sem animais 
* A-19-03-M-ok - Satisfação de Vet único com uma espécie com animais 
* A-19-04-M-ok - Satisfação de Vet único com várias espécies com animais  
* A-19-09-M-ok - Satisfação de Trt com um habitat sem animais
* A-19-10-M-ok - Satisfação de Trt com um habitat com animais 
* A-19-11-M-ok - Satisfação de Trt com vários habitat com animais 
* A-19-13-M-ok - Satisfação de Trt com vários habitats com vários trt's e com animais

* A-20-01-M-ok - Registar vacina válida com uma espécie e com várias espécies
* A-20-02-M-ok - Registar vacina válida com uma espécie inexistente
* A-20-04-M-ok - Registar vacina com id repetido e com maiúsculas/minúsculas 

* A-21-02-M-ok - Aplicar vacina válida a animal existente com id de tratador 
* A-21-03-M-ok - Aplicar vacina não existente animal/vet válidos
* A-21-05-M-ok - Aplicar vacina válida a animal e vet válidos só com uma espécie  
* A-21-06-M-ok - Aplicar vacina válida a animal e vet válidos só com uma espécie e ver animal NORMAL
* A-21-07-M-ok - Aplicar vacina válida com várias espécies a animal e vet válidos   
* A-21-08-M-ok - Aplicar vacina válida com várias espécies a animal e vet válidos e ver animal NORMAL
* A-21-10-M-ok - Aplicar vacina válida a animal e vet com várias espécies válidos  
* A-21-13-M-ok - Aplicar vacina inválida a animal e vet válidos - Confusão
* A-21-14-M-ok - Aplicar vacina inválida a animal vet válidos - Acidente

* A-22-02-M-ok - Listar animais de habitat sem animais e com um animal
 
* A-23-01-M-ok - Consultar animal não existente e não vacinado

* A-24-01-M-ok - veterinário não existente e sem vacinações  
* A-24-03-M-ok - Veterinário só com um animal e mesma vacina (bem aplicadas)  

* A-25-01-M-ok - sem vacinas, com várias vacinas bem, com uma errada
