CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          client_id BIGINT NOT NULL,
                          product_id BIGINT NOT NULL,
                          balance DECIMAL(15,2) NOT NULL,
                          interest_rate DECIMAL(5,4) NOT NULL,
                          is_recalc BOOLEAN NOT NULL,
                          card_exist BOOLEAN NOT NULL,
                          status VARCHAR(20) NOT NULL
);

CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                       card_id VARCHAR(16) NOT NULL UNIQUE,
                       payment_system VARCHAR(20) NOT NULL,
                       status VARCHAR(20) NOT NULL
);

CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                          payment_date DATE NOT NULL,
                          amount DECIMAL(15,2) NOT NULL,
                          is_credit BOOLEAN NOT NULL,
                          payed_at TIMESTAMP,
                          type VARCHAR(50) NOT NULL
);

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                              card_id BIGINT REFERENCES cards(id) ON DELETE SET NULL,
                              type VARCHAR(50) NOT NULL,
                              amount DECIMAL(15,2) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              timestamp TIMESTAMP NOT NULL
);
