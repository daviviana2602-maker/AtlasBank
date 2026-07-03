CREATE TABLE account (
                    id BIGSERIAL PRIMARY KEY,
                    user_id BIGINT NOT NULL UNIQUE,
                    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,

                    CONSTRAINT fk_accounts_user
                        FOREIGN KEY (user_id)
                        REFERENCES users(id)
                        ON DELETE CASCADE
);