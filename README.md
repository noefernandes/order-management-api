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

**Passo 2.** Na pasta root do projeto:

`mvn package`
`mv target/order-management-api-0.0.1-SNAPSHOT.jar target/order-management-api.jar`
`docker image build -t seu_nome_de_usuario_do_docker/order-management-api .`
`docker push seu_nome_de_usuario_do_docker/order-management-api`


