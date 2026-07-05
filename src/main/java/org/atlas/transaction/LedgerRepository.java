package org.atlas.transaction;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LedgerRepository extends JpaRepository<LedgerEntity, Long> {
}