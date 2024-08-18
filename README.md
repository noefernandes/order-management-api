# Order Management Api

## Como Executar usando localmente docker-compose

## Como implantar na AWS

### 1. Criação de banco de dados MySql usando AWS RDS

**Passo 1**. Entra-se no console da AWS e digita na busca RDS

**Passo 2**. Já na tela principal do RDS, clica-se em Criar banco de dados.

**Passo 3**. A seguinte tela de criação será exibida

<img src="https://github.com/user-attachments/assets/8c019102-eb80-4c12-8f79-9296cc271f5e" alt="Image 1" align="center" style="width: 600px"/></td>

Nela mantêm-se a maioria das opções predefinidas com a exceção da escolha de:

* Tipo de mecanismo: MySQL

* Modelo: Nível gratuito

* Opções de implantação: Instância de banco de dados única

* Identificador da instância de banco de dados: nome do seu banco de dados

* Nome do usuário principal: nome do seu usuário de banco de dados

* Gerenciamento de credenciais: define-se como autogerenciada e preenche-se com sua senha de banco de dados escolhida

* Configuração da instância: Marca-se "Classes com capacidade de intermitência" e escolhe-se db.t3.micro

* Acesso público: sim

* Habilitar backups automatizados: não

* Habilitar monitoramento avançado: não

**Passo 4**. Clica-se em Criar banco de dados

Ao final, retornando à tela de listagem de banco de dados do RDS é possível ver o banco criado:

<img src="https://github.com/user-attachments/assets/5510cb0a-4581-4b0b-aaa4-c10caff9d933" alt="Image 1" align="center" style="width: 800px"/></td>

Clicando-se no banco de dados criado, pode-se achar o endpoint em que pode-se estabelecer uma coneção ao banco.

### Criação de instância RabbitMQ usando Amazon MQ

**Passo 1**. No console da AWS busca-se Amazon MQ

**Passo 2**. Na tela principal do Amazon MQ, clica-se em Criar agentes

**Passo 3**. A tela de criação será a seguinte:

<img src="https://github.com/user-attachments/assets/5eaa4b9d-3ef0-493d-9505-c91066bb6f56" alt="Image 1" align="center" style="width: 800px"/></td>

A partir daqui a maioria das opções serão mantidas, à exceção de:

* Tipos de mecanismo de operador: RabbitMQ
 
* Modo de implantação: Operador de instância única
 
* Nome do operador: nome escolhido para a instância RabbitMQ

* Tipo de instâncias de operador: mq.t3.micro

* Nome de usuário: seu nome de usuário para o RabbitMQ

* Senha: sua senha para o RabbitMQ

**Passo 3**. Clica-se em Criar broker

Ao fim, retornando-se à tela principal do RabbitMQ é possível notar a instanância criada:

<img src="https://github.com/user-attachments/assets/5f51656d-9796-440d-aceb-5c84c944246e" alt="Image 1" align="center" style="width: 800px"/></td>

Clicando-se na instância, ao buscar-se por Conexões ficam visíveis o endereço para o console web do RabbitMQ e o endpoint da instância, que permitirão as futuras conexões.

### Criação da instância da Order Management Api no Amazon Elastic Container Service (ECS)

**Passo 1.** No arquivo application.properties da aplicação Spring Boot deve-se conectar as instâncias do MySQL e do Rabbit hospedadas na AWS.

```
spring.application.name=order-management-api

spring.datasource.url=jdbc:mysql://{RDS_ENDPOINT}:{RDS_PORT}/{RDS_DATABASE}
spring.datasource.username={RDS_USERNAME}
spring.datasource.password={RDS_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.rabbitmq.host={RABBITMQ_ENDPOINT}
spring.rabbitmq.port={RABBITMQ_PORT}
spring.rabbitmq.username={RABBITMQ_USER}
spring.rabbitmq.password={RABBITMQ_PASSWORD}
spring.rabbitmq.ssl.enabled=true
```

**Passo 2.** Na pasta root do projeto, executa-se os seguintes comandos para criar a imagem da aplicação e enviá-la ao DockerHub:

`mvn package`

`mv target/order-management-api-0.0.1-SNAPSHOT.jar target/order-management-api.jar`

`docker image build -t seu_nome_de_usuario_do_docker/order-management-api .`

`docker push seu_nome_de_usuario_do_docker/order-management-api`

**Passo 3.** No console da AWS, busca-se Amazon Elastic Container Service.

**Passo 4.** Clica-se em Criar cluster.

**Passo 5.** Aparacerá a seguinte tela de criação:


<img src="https://github.com/user-attachments/assets/66e4fa09-a3c6-4437-99df-43b2476a52fb" alt="Image 1" align="center" style="width: 800px"/></td>

Nela, insere-se o nome do cluster e clica em criar, mantendo as outras configurações predefinidas. Pronto, o cluster estará criado.

Obs: é importante que AWS Fargate seja mantido para se trabalhar com serveless.

**Passo 6.** Logo depois, clica-se em Definições de tarefa na barra lateral e em seguida em Criar nova definição de tarefa.

**Passo 7.** A seguinte tela aparecerá:


<img src="https://github.com/user-attachments/assets/2b8c8329-2569-4204-a1b4-2d02baf14040" alt="Image 1" align="center" style="width: 800px"/></td>

As mudanças nesta tela se limitarão a:

* Família da definição de tarefa: Especifique um nome de família de definição de tarefa exclusivo. Pode ser nome_do_container-task, por exemplo.

* Em Tamanho da Tarefa, para CPU e Memória: .5 vCPU e 1GB, respectivamente.

* Função de execução de tarefas: Criar novo perfil

* Em Detalhes do contêiner, adicione um nome para o container, atribua a URI da sua imagem Docker no DockerHub e mude a porta do contêiner para 8080.

**Passo 8.** Clica-se em Criar.

**Passo 9.** Após criar a Definição de tarefa, clica-se em Clusters no menu lateral e em seguida no cluster anteriormente criado.

**Passo 10.** Na tab Serviços, clica-se em Criar. A seguinte tela será apresentada:

<img src="https://github.com/user-attachments/assets/cd3f72f1-c931-4d13-8f65-6a8b3e9ee81e" alt="Image 1" align="center" style="width: 800px"/></td>

Nessa tela, as mudanças serão feitas em:

* Família: selecione a tarefa que você criou.

* Balanceamento de carga: selecione Application Load Balancer.

* Nome do load balancer: escolha um nome para o load balancer.

* Em Grupo de destino, escolha Criar novo grupo de destino e defina um nome à sua escolha.

**Passo 11.** Finalmente, clica-se em Criar. Após alguns, minutos será possível notar que o serviço foi criado e a tarefa criada anteriormente está em execução.

<img src="https://github.com/user-attachments/assets/e5236499-e3e1-44cd-a79c-387bd9232829" alt="Image 1" align="center" style="width: 800px"/></td>

**Passo 12.** Clicando no serviço, procura-se a tab Configuração e redes. Nela procure por Nomes de DNS e lá estará o endereço de load balancer onde será possível acessar a aplicação.

Ao final, pode-se notar que a implantação da aplicação foi um sucesso fazendo uma consulta simples à api:

<img src="https://github.com/user-attachments/assets/84dece0c-73ef-40b8-a2c0-57d3c364ca2c" alt="Image 1" align="center" style="width: 800px"/></td>

Obs: A implantação logo depois dos testes foi desativada por questão de segurança.
