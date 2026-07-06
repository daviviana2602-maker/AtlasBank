CREATE TABLE ledger_entries (
                                id BIGSERIAL PRIMARY KEY,
                                account_id BIGINT NOT NULL,
                                type VARCHAR(255) NOT NULL,
                                amount DECIMAL(19,2) NOT NULL,
                                description VARCHAR(255),
                                balance_after DECIMAL(19,2) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                reference_id BIGINT NOT NULL,

                                CONSTRAINT fk_ledger_account
                                    FOREIGN KEY (account_id) REFERENCES account(id)
);