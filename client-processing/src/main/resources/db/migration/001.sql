CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         client_id VARCHAR(12) NOT NULL UNIQUE,
                         user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         first_name VARCHAR(100) NOT NULL,
                         middle_name VARCHAR(100),
                         last_name VARCHAR(100) NOT NULL,
                         date_of_birth DATE NOT NULL,
                         document_type VARCHAR(20) NOT NULL,
                         document_id VARCHAR(50) NOT NULL,
                         document_prefix VARCHAR(10),
                         document_suffix VARCHAR(10)
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL,
                          key VARCHAR(10) NOT NULL,
                          create_date DATE NOT NULL,
                          product_id VARCHAR(50) UNIQUE
);

CREATE TABLE client_products (
                                 id BIGSERIAL PRIMARY KEY,
                                 client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
                                 product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                 open_date DATE NOT NULL,
                                 close_date DATE,
                                 status VARCHAR(20) NOT NULL
);
