CREATE TABLE pix (
                     id BIGSERIAL PRIMARY KEY,
                     sender_account_id BIGINT NOT NULL,
                     receiver_account_id BIGINT NOT NULL,
                     amount NUMERIC(19,2) NOT NULL,
                     description VARCHAR(255),
                     status VARCHAR(50) NOT NULL,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                     CONSTRAINT fk_pix_sender_account
                         FOREIGN KEY (sender_account_id)
                             REFERENCES account(id),

                     CONSTRAINT fk_pix_receiver_account
                         FOREIGN KEY (receiver_account_id)
                             REFERENCES account(id)
);