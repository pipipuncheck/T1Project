CREATE TABLE error_log (
                           id BIGSERIAL PRIMARY KEY,
                           timestamp TIMESTAMP NOT NULL,
                           service_name VARCHAR(200),
                           method_signature VARCHAR(1000),
                           stack_trace TEXT,
                           error_message VARCHAR(2000),
                           method_arguments TEXT,
                           kafka_error TEXT
);

CREATE TABLE http_log (
                          id BIGSERIAL PRIMARY KEY,
                          timestamp TIMESTAMP NOT NULL,
                          service_name VARCHAR(200),
                          method_signature VARCHAR(1000),
                          uri VARCHAR(2000),
                          parameters TEXT,
                          body TEXT,
                          direction VARCHAR(20)
);