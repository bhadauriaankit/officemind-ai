package com.officemind.application.document;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;

import java.util.Optional;

public interface DocumentRepositoryPort {

    Document save(Document document);

    Optional<Document> findById(EntityId id);

    PageResult<Document> findAll(int page, int size);

    void deleteById(EntityId id);
}
