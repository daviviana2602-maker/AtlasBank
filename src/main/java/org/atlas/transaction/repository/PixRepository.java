package org.atlas.transaction.repository;

import org.atlas.transaction.entity.PixEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PixRepository extends JpaRepository<PixEntity, Long> {
}