package org.atlas.transaction.repository;

import org.atlas.transaction.entity.LedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LedgerRepository extends JpaRepository<LedgerEntity, Long> {
}