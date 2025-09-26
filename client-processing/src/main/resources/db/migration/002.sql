CREATE TABLE black_list_registry (
                                     id BIGSERIAL PRIMARY KEY,
                                     document_type VARCHAR(20) NOT NULL,
                                     document_id VARCHAR(50) NOT NULL,
                                     blacklisted_at TIMESTAMP NOT NULL,
                                     reason TEXT,
                                     blacklist_expiration_date TIMESTAMP
);