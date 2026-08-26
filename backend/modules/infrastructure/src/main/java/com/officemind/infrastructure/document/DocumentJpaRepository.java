package com.officemind.infrastructure.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DocumentJpaRepository extends JpaRepository<DocumentJpaEntity, UUID> {

    Page<DocumentJpaEntity> findAll(Pageable pageable);
}
