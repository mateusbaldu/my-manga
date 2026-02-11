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

- [Docker](https://www.docker.com/get-started) e Docker Compose
- Git

## Configuração e Instalação

### 1. Clonar o Repositório

Clone o projeto para sua máquina local.

```bash
git clone https://github.com/seu-usuario/my-manga.git
cd my-manga
```

### 2. Configurar as Variáveis de Ambiente

Crie um arquivo `.env` na **raiz do projeto** com as seguintes variáveis:

```env
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui

MAIL_USERNAME=seu_email@gmail.com
MAIL_PASSWORD=sua_senha_de_app_aqui
```

> **Nota:** O `MAIL_PASSWORD` deve ser uma [senha de aplicativo do Google](https://myaccount.google.com/apppasswords), não a senha da sua conta Gmail.

### 3. Primeira Execução (popular o banco de dados)

Na primeira vez, é necessário popular o banco com os dados iniciais (roles e mangás). Para isso, altere temporariamente no arquivo `docker-compose.yaml`:

```yaml
SPRING_JPA_HIBERNATE_DDL_AUTO: create
SPRING_SQL_INIT_MODE: always
```

Em seguida, execute:

```bash
docker compose up --build
```

Aguarde até ver no log:

```
Started MymangaApplication in X seconds
```

### 4. Execuções Seguintes

Após a primeira execução, volte as configurações no `docker-compose.yaml` para:

```yaml
SPRING_JPA_HIBERNATE_DDL_AUTO: update
SPRING_SQL_INIT_MODE: never
```

E rode normalmente:

```bash
docker compose up --build
```

> **Dica:** Para subir em background, use `docker compose up --build -d`.

### 5. Acessando a Aplicação

| Serviço | URL |
|---|---|
| Frontend (Angular) | http://localhost:4200 |
| API (Spring Boot) | http://localhost:8080/my-manga |
| Swagger UI | http://localhost:8080/my-manga/swagger-ui/index.html |

### 6. Parar e Limpar

```bash
# Parar os containers
docker compose down

# Parar e remover os dados do banco (volume)
docker compose down -v
```

> **Atenção:** Usar `-v` remove o volume do PostgreSQL. Na próxima execução será necessário repetir o passo 3 para popular o banco novamente.

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
http://localhost:8080/my-manga/swagger-ui/index.html
```

## Testes

Para executar os testes unitários e de integração do backend (sem Docker):

```bash
cd mymanga
./mvnw test
```

## Estrutura do Projeto

O projeto é dividido em dois módulos principais:

- `mymanga/` — Backend em Spring Boot (Java 21)
- `mymanga-frontend/` — Frontend em Angular
- `docker-compose.yaml` — Orquestração dos 3 containers (PostgreSQL, API, Frontend)
