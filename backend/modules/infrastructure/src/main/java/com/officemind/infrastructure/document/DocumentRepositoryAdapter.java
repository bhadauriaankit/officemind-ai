package com.officemind.infrastructure.document;

import com.officemind.application.document.DocumentRepositoryPort;
import com.officemind.common.paging.PageResult;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentJpaRepository jpaRepository;

    public DocumentRepositoryAdapter(DocumentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Document save(Document document) {
        DocumentJpaEntity saved = jpaRepository.save(DocumentMapper.toJpa(document));
        return DocumentMapper.toDomain(saved);
    }

    @Override
    public Optional<Document> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(DocumentMapper::toDomain);
    }

    @Override
    public PageResult<Document> findAll(int page, int size) {
        Page<DocumentJpaEntity> result = jpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(DocumentMapper::toDomain).toList(),
                page,
                size,
                result.getTotalElements()
        );
    }

    @Override
    public void deleteById(EntityId id) {
        jpaRepository.deleteById(id.value());
    }
}
