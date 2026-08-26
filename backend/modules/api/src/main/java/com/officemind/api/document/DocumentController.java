package com.officemind.api.document;

import com.officemind.application.document.*;
import com.officemind.application.user.UserRepositoryPort;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import com.officemind.api.user.PageResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final UserRepositoryPort userRepository;

    public DocumentController(UploadDocumentUseCase uploadDocumentUseCase,
                               ListDocumentsUseCase listDocumentsUseCase,
                               DownloadDocumentUseCase downloadDocumentUseCase,
                               DeleteDocumentUseCase deleteDocumentUseCase,
                               UserRepositoryPort userRepository) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.listDocumentsUseCase = listDocumentsUseCase;
        this.downloadDocumentUseCase = downloadDocumentUseCase;
        this.deleteDocumentUseCase = deleteDocumentUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse upload(@RequestParam("file") MultipartFile file,
                                    JwtAuthenticationToken authentication) {
        try {
            String keycloakSubjectId = authentication.getToken().getSubject();
            String internalUserId = userRepository.findByKeycloakSubjectId(keycloakSubjectId)
                    .map(u -> u.getId().value().toString())
                    .orElse(null); // null is fine - uploaded_by_user_id is nullable

            Document document = uploadDocumentUseCase.execute(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream(),
                    internalUserId
            );
            return DocumentResponse.from(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }
    }

    @GetMapping
    public PageResponse<DocumentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(listDocumentsUseCase.execute(page, size), DocumentResponse::from);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        DownloadDocumentUseCase.Result result = downloadDocumentUseCase.execute(EntityId.of(id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        result.document().getContentType() != null
                                ? result.document().getContentType()
                                : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + result.document().getFileName() + "\"")
                .body(new InputStreamResource(result.content()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteDocumentUseCase.execute(EntityId.of(id));
        return ResponseEntity.noContent().build();
    }
}
