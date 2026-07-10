ALTER TABLE account
    ADD COLUMN account_password_reset_token VARCHAR(255),
    ADD COLUMN account_password_reset_expires_at TIMESTAMP,
    ADD COLUMN new_account_password VARCHAR(255);