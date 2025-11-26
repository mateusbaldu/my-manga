# MyManga

MyManga é um projeto de e-commerce Full Stack desenvolvido para matéria de Programação Web da FATEC Ipiranga. O sistema inclui funcionalidades de autenticação de usuários, gerenciamento de catálogo, carrinho de compras, processamento de pedidos e um painel administrativo.

## Tecnologias Utilizadas

### Backend
- Java 21
- Spring Boot 3.5.5
- Spring Security (Autenticação via JWT e OAuth2 Resource Server)
- Spring Data JPA
- PostgreSQL (Banco de dados de produção)
- H2 Database (Banco de dados de teste)
- Maven (Gerenciador de dependências)
- MapStruct (Mapeamento de objetos)
- Swagger/OpenAPI (Documentação da API)
- Java Mail Sender (Envio de e-mails transacionais)

### Frontend
- Angular (versão mais recente)
- TypeScript
- Bootstrap 5
- SCSS
- RxJS

## Funcionalidades

- **Autenticação e Autorização**: Registro de usuários, login, ativação de conta por e-mail, recuperação de senha e controle de acesso baseado em funções (ADMIN, BASIC, SUBSCRIBER).
- **Catálogo de Produtos**: Listagem de mangás com paginação, busca por palavras-chave e ordenação por avaliação de mangá.
- **Detalhes do Produto**: Visualização de detalhes do mangá e seleção de volumes específicos.
- **Carrinho de Compras**: Gerenciamento de itens selecionados antes da compra.
- **Gestão de Pedidos**: Criação de pedidos, histórico de pedidos do usuário e cancelamento (quando aplicável).
- **Painel Administrativo**: 
  - Cadastro, edição e exclusão de mangás.
  - Gerenciamento de volumes e estoque.
  - Visualização de todos os pedidos do sistema.
- **Endereços**: Integração com ViaCEP para cadastro automático de endereços.

## Pré-requisitos

Para executar este projeto, você precisará ter instalado em sua máquina:

- Java JDK 21
- Node.js e npm
- PostgreSQL
- Git

## Configuração e Instalação

### 1. Clonar o Repositório

Clone o projeto para sua máquina local.

### 2. Configuração do Banco de Dados

Crie um banco de dados no PostgreSQL com o nome desejado (ex: mymanga).

### 3. Configuração do Backend

O backend utiliza variáveis de ambiente ou um arquivo `secrets.properties` para configurações sensíveis. Você pode configurar as seguintes propriedades no arquivo `src/main/resources/application.properties` ou passar como variáveis de ambiente:

- `db.url`: URL de conexão JDBC (ex: jdbc:postgresql://localhost:5432/mymanga)
- `db.username`: Usuário do banco de dados
- `db.password`: Senha do banco de dados
- `mail.password`: Senha de aplicativo para o envio de e-mails (Gmail SMTP)

Para executar o backend:

```
cd mymanga
./mvnw spring-boot:run
```

A API estará acessível em: http://localhost:8080/my-manga

### 4. Configuração do Frontend

Navegue até a pasta do frontend e instale as dependências:

```
cd mymanga-frontend/mymanga-frontend
npm install
```

Para iniciar o servidor de desenvolvimento:

```
ng serve
```

A aplicação estará acessível em: http://localhost:4200

## Credenciais de Acesso Padrão

Na primeira execução, o sistema cria automaticamente dois usuários para testes (definidos em `AdminUserConfig.java`):

**Administrador:**
- E-mail: admin@mymanga.com
- Senha: admin123

**Usuário Comum:**
- E-mail: usertest@mymanga.com
- Senha: teste123

## Documentação da API

Com o backend em execução, você pode acessar a documentação completa dos endpoints através do Swagger UI:

```
URL: http://localhost:8080/my-manga/swagger-ui/index.html
```

## Testes

Para executar os testes unitários e de integração do backend:

```
./mvnw test
```

## Estrutura do Projeto

O projeto é dividido em dois módulos principais:

- `mymanga/`: Contém o código fonte do backend em Spring Boot.
- `mymanga-frontend/`: Contém o código fonte do frontend em Angular.
