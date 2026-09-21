# Documentação Arquitetural e Técnica: LibraryManagement Web (MVP)

**Versão:** 1.0 (MVP Foundation)
**Arquitetura:** Monolito Modular & Arquitetura Hexagonal (Ports and Adapters)
**Stack Tecnológica:** Java, Spring Boot, PostgreSQL, Flyway DB

---

## 1. Visão Geral do MVP

O Produto Mínimo Viável (MVP) tem como foco isolar e validar o core business da aplicação: o fluxo de cadastro e organização de livros físicos através do ISBN. Para reduzir a barreira de desenvolvimento inicial e permitir testes mais rápidos, este MVP será construído como uma aplicação Web, adiando o desenvolvimento mobile.

## 2. Escopo Funcional do MVP

### Requisitos Funcionais (RF)
*   **RF001 - Autenticação Básica:** Cadastro e login de usuários para garantir o isolamento da biblioteca de cada conta.
*   **RF002 - Consulta por ISBN:** Uma barra de busca onde o usuário insere o ISBN do livro.
*   **RF003 - Cadastro Manual de Obra:** Formulário para inserção manual dos dados de um livro caso o ISBN seja desconhecido pelas fontes automatizadas.
*   **RF004 - Gestão da Estante:** Capacidade de adicionar o livro resolvido ou recém-cadastrado à biblioteca pessoal do usuário, além de listar os livros salvos.

### Regras de Negócio (RN)
*   **RN001 - Fluxo de Resolução de ISBN:** O sistema deve obedecer a seguinte ordem rigorosa ao buscar um ISBN:
    1.  **API Externa (ex: Google Books/Open Library):** Tenta buscar os metadados primários em fontes globais.
    2.  **Banco de Dados Local (Fallback):** Se a API externa não encontrar, o sistema busca no próprio banco de dados da aplicação para verificar se outro usuário já cadastrou esta obra manualmente no passado.
    3.  **Cadastro Manual:** Se falhar nas duas camadas acima, o Front-end exibe um formulário. Quando o usuário salvar, esse novo livro é persistido no banco local. Na próxima vez que qualquer usuário buscar esse mesmo ISBN, o passo 2 retornará sucesso.

## 3. Arquitetura do Sistema

O projeto será desenvolvido utilizando **Java** com o framework **Spring Boot** e **PostgreSQL**. A estrutura de diretórios e o acoplamento seguirão rigorosamente a **Arquitetura Hexagonal (Ports and Adapters)** e os princípios **SOLID**, favorecendo o uso da Programação Orientada a Objetos clássica e código limpo.

### 3.1. Estrutura Hexagonal
A divisão será baseada no isolamento do domínio:
*   **Domain (Core):** Contém entidades puras Java e regras de negócio. Zero dependência de frameworks.
*   **Application (Use Cases):** Orquestra o fluxo. Contém as interfaces (Ports) para comunicação com o mundo externo.
*   **Adapters (Infrastructure/Web):** Implementações concretas de acesso a dados (Repositories JPA) e integrações HTTP (WebClients para APIs de ISBN), além dos Controllers REST.

### 3.2. Princípios SOLID
*   **SRP (Responsabilidade Única):** O serviço que busca na API externa não é o mesmo que salva no banco.
*   **OCP (Aberto/Fechado):** A interface `IsbnResolverPort` no domínio permite adicionar novas APIs no futuro sem alterar a regra de fluxo.
*   **DIP (Inversão de Dependência):** Os casos de uso no domínio dependem apenas de abstrações. O Controller depende do caso de uso, e o caso de uso depende da interface do repositório, não da sua implementação JPA.

### 3.3. Escolha do Banco de Dados (PostgreSQL)
A escolha pelo **PostgreSQL** é definitiva para este MVP. O domínio do LibraryManagement é estruturado e relacional (Usuários possuem Estantes <-> Livros em relação N:N). O uso do PostgreSQL garante a integridade referencial, evita duplicação de dados e simplifica o mapeamento (ACID). 

## 4. Desenho de Microserviços (Monolito Modular)

O MVP nascerá como um **Monolito Modular**. Os contextos delimitados (Bounded Contexts) atuarão como microserviços lógicos dentro do mesmo projeto.
*   **Módulo IAM:** Identity & Access Management (Usuários e Tokens JWT).
*   **Módulo Catalog:** Acervo global e fluxo de ISBN.
*   **Módulo Library:** Estante e relacionamento Usuário <-> Livro.

## 5. Contratos de API (Endpoints Essenciais)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| POST | `/api/v1/auth/register` | Criação de usuário. |
| POST | `/api/v1/auth/login` | Autenticação e retorno de token JWT. |
| GET | `/api/v1/catalog/isbn/{isbn}` | Aciona a RN001. Retorna os dados do livro ou HTTP 404. |
| POST | `/api/v1/catalog/books` | Salva dados do formulário manual no banco local. |
| POST | `/api/v1/library/books` | Adiciona um ID de livro à estante do usuário autenticado. |
| GET | `/api/v1/library` | Retorna a lista de livros da estante do usuário. |

## 6. Principais Dependências (Spring Boot)

*   **Spring Boot Starter Web:** Para criação dos endpoints REST (Controllers).
*   **Spring Boot Starter Data JPA:** Para a persistência de dados.
*   **PostgreSQL Driver:** Comunicação com o banco de dados.
*   **Flyway Core & Flyway PostgreSQL:** Controle de versionamento do banco de dados (Migrations).
*   **Spring Boot Starter Security:** Autenticação e IAM (incluindo JWT).
*   **Spring Boot Starter Validation:** Validação de dados de entrada (`@NotNull`, `@NotBlank`).
*   **Spring WebFlux ou OpenFeign:** Para requisições HTTP externas.

## 7. Modelo de Banco de Dados (Flyway Migration)

Para garantir consistência e versionamento, o Hibernate DDL Auto será desabilitado em favor do Flyway. O script abaixo deve ser salvo em `src/main/resources/db/migration/V1__create_initial_tables.sql`. A arquitetura utiliza **UUID** nativo.

```sql
-- Módulo IAM
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Módulo Catalog
CREATE TABLE books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(150),
    published_year INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Módulo Library (Relação N:N)
CREATE TABLE user_books (
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    
    PRIMARY KEY (user_id, book_id)
);
```

## 8. Estrutura de Pacotes (Arquitetura Hexagonal Rigorosa)

A estrutura adota o padrão estrito de segmentação hexagonal por módulo, sem a presença de adapters de mensageria assíncrona (queue), focando no tráfego HTTP síncrono do MVP.

````
src/main/java/com/vinicius/library
├── catalog/
│   ├── adapters/
│   │   ├── in/web/                     # Controllers REST (Ex: BookController)
│   │   └── out/
│   │       ├── client/                 # Integrações API Externa (Ex: GoogleBooksAdapter)
│   │       └── persistence/            # JPA Entities, Repositories e Mappers
│   ├── application/
│   │   ├── ports/
│   │   │   ├── in/                     # Interfaces Inbound (Use Cases Contracts)
│   │   │   └── out/                    # Interfaces Outbound (Ex: BookRepositoryPort)
│   │   └── services/                   # Implementações dos Casos de Uso
│   ├── domain/
│   │   ├── exceptions/                 # Exceções de Regra de Negócio
│   │   └── model/                      # Entidades Puras (Ex: Book)
│   └── infra/config/                   # Configurações do Spring (Beans)
├── iam/
│   └── (Estrutura Espelhada)
└── library/
    └── (Estrutura Espelhada)
````


## 9. Especificação do Domínio e Aplicação (Hexagonal Core)
   O domínio é escrito em Java puro, sendo o guardião das regras de negócio. O momento da criação (data) é gerado no ato da instânciação.

### 9.1. Entidades de Domínio (POJOs)
*   Book (Módulo Catalog): id (UUID), isbn, title, author, publisher, publishedYear, createdAt (LocalDateTime). A entidade se auto-valida no construtor e inicializa createdAt com LocalDateTime.now() para novos registros.

*   User (Módulo IAM): id (UUID), name, email, passwordHash, createdAt (LocalDateTime).

### 9.2. Portas de Saída (Outbound Ports)
*   BookRepositoryPort: findByIsbn(String isbn) -> Book, save(Book book) -> Book

*   IsbnResolverPort: fetchBookFromExternalApi(String isbn) -> Book

*   LibraryRepositoryPort: addBookToUser(UUID userId, UUID bookId) -> void, getUserBooks(UUID userId) -> Book[] (Retorno de Array procedural visando laços clássicos de iteração).

### 9.3. Casos de Uso (Application Services)
#### ResolveIsbnUseCase:
*   Aciona IsbnResolverPort.

*   Se null, aciona BookRepositoryPort.findByIsbn().

*   Se null, devolve objeto vazio ativando formulário de cadastro manual no Front-end.

* RegisterManualBookUseCase: Recebe DTO, converte e instancia Book puro (gerando UUID provisório/nulo e createdAt), chama BookRepositoryPort.save().

## 10. Modelagem de Classes (Diagrama Mermaid)
O diagrama reflete a Inversão de Dependência (DIP), onde a camada de infraestrutura (Adapters) implementa os contratos do núcleo da aplicação

```mermaid
classDiagram
%% DOMAIN LAYER (CORE)
namespace Domain_Core {
class Book {
+UUID id
+String isbn
+String title
+String author
+String publisher
+Integer publishedYear
+LocalDateTime createdAt
+validate()
}
class User {
+UUID id
+String name
+String email
+String passwordHash
+LocalDateTime createdAt
}
}

    %% APPLICATION LAYER (USE CASES & PORTS)
    namespace Application_Ports {
        class BookRepositoryPort {
            <<interface>>
            +findByIsbn(String isbn) Book
            +save(Book book) Book
        }
        class IsbnResolverPort {
            <<interface>>
            +fetchBookFromExternalApi(String isbn) Book
        }
        class LibraryRepositoryPort {
            <<interface>>
            +addBookToUser(UUID userId, UUID bookId)
            +getUserBooks(UUID userId) Book[]
        }
        
        class ResolveIsbnUseCase {
            -IsbnResolverPort isbnResolverPort
            -BookRepositoryPort bookRepositoryPort
            +resolve(String isbn) Book
        }
        class RegisterManualBookUseCase {
            -BookRepositoryPort bookRepositoryPort
            +register(Book book) Book
        }
        class AddBookToLibraryUseCase {
            -LibraryRepositoryPort libraryRepositoryPort
            +addBook(UUID userId, UUID bookId)
        }
    }

    %% INFRASTRUCTURE LAYER (ADAPTERS)
    namespace Infrastructure_Adapters {
        class BookJpaAdapter {
            +findByIsbn(String isbn) Book
            +save(Book book) Book
        }
        class GoogleBooksAdapter {
            +fetchBookFromExternalApi(String isbn) Book
        }
        class LibraryJpaAdapter {
            +addBookToUser(UUID userId, UUID bookId)
            +getUserBooks(UUID userId) Book[]
        }
    }

    %% DEPENDENCY RELATIONSHIPS
    ResolveIsbnUseCase --> IsbnResolverPort : uses
    ResolveIsbnUseCase --> BookRepositoryPort : uses
    RegisterManualBookUseCase --> BookRepositoryPort : uses
    AddBookToLibraryUseCase --> LibraryRepositoryPort : uses

    BookJpaAdapter ..|> BookRepositoryPort : implements
    GoogleBooksAdapter ..|> IsbnResolverPort : implements
    LibraryJpaAdapter ..|> LibraryRepositoryPort : implements

    ResolveIsbnUseCase ..> Book : returns
    RegisterManualBookUseCase ..> Book : uses
    LibraryRepositoryPort ..> Book : returns array
    
  ````
---