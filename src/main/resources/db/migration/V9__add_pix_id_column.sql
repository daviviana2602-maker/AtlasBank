ALTER TABLE ledger_entries
    ADD COLUMN pix_id BIGINT;

ALTER TABLE ledger_entries
    ADD CONSTRAINT fk_ledger_pix
        FOREIGN KEY (pix_id)
            REFERENCES pix(id);