# Hopeful Backend

O README a seguir é focado para como executar o projeto.  
Para mais informações vá para a [wiki do projeto](https://tools.ages.pucrs.br/gestao-de-planos-de-contingencia-em-desastres/hopeful-wiki/-/wikis/home).

## Pré-requisitos para executar o projeto

Para executar este projeto em sua máquina, é necessário instalar as seguintes tecnologias:

- **Java 21**
- **Spring Boot 3.5.4**
- **Docker**
- **PostgreSQL 15** 
- **Maven 3.8.6**

Para saber como baixar uma dessas tecnologias, acesse a [wiki do projeto](https://tools.ages.pucrs.br/gestao-de-planos-de-contingencia-em-desastres/hopeful-wiki/-/wikis/home) na parte de downloads.


Recomendação de ferramentas para instalar: 
## Executando o projeto

### Opção 1 - Executando aplicação e banco de dados em containers (RECOMENDADO)

Execute o comando abaixo na raiz do repositório local:
```bash
docker compose up -d --build
```

Com este comando, realiza-se o build das imagens dos containers utilizados pela aplicação e se executa estes containers.
No caso deste projeto, são criados dois containers: um para o banco de dados PostgreSQL; e outro para a API Spring Boot 
do projeto, sendo que esta já é configurada para se conectar com o banco de dados.

A aplicação Spring Boot estará disponível em ``http://localhost:8080``
.
O Swagger, que permite acessar a documentação e testar os endpoints, estará disponível em ``http://localhost:8080/swagger-ui/index.html`` 
.

Quando quiser parar a execução da aplicação e remover os recursos criados (containers e redes), basta executar o comando 
oposto abaixo:
```bash
docker compose down
```

Por padrão, o comando acima não deleta os volumes do Docker utilizados pela aplicação, e para este projeto persistimos 
os dados do container do PostgreSQL em um volume Docker de nome "pgdata". Isso permite que, ao subir os 
containers novamente via Docker Compose em sua máquina, os dados persistidos sejam utilizados novamente.

No entanto, caso se queira remover também este volume, basta adicionar a opção `-v` conforme abaixo:
```bash
docker compose down -v
```

### Opção 2 - Executando apenas banco de dados em container

Execute o comando abaixo na raiz do repositório local, para executar apenas o banco de dados PostgreSQL em um container 
Docker (facilita a execução da aplicação durante o desenvolvimento):
```bash
docker compose up -d db
```



