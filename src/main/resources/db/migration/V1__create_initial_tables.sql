-- Criação da tabela de Usuários (IAM)
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(150) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de Livros (Catalog)
CREATE TABLE books (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       isbn VARCHAR(20) UNIQUE,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publisher VARCHAR(150),
                       published_year INT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de Estante (Library - Relação N:N)
CREATE TABLE user_books (
                            user_id UUID NOT NULL,
                            book_id UUID NOT NULL,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,

                            PRIMARY KEY (user_id, book_id)
);