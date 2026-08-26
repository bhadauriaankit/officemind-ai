package com.officemind.application.document;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.document.Document;
import org.springframework.stereotype.Service;

@Service
public class ListDocumentsUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final DocumentRepositoryPort documentRepository;

    public ListDocumentsUseCase(DocumentRepositoryPort documentRepository) {
        this.documentRepository = documentRepository;
    }

    public PageResult<Document> execute(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return documentRepository.findAll(safePage, safeSize);
    }
}
