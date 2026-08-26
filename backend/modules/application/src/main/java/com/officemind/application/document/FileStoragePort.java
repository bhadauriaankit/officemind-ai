package com.officemind.application.document;

import java.io.InputStream;

public interface FileStoragePort {

    /** Stores the file bytes under the given key, returning nothing — the key is caller-assigned. */
    void store(String storageKey, InputStream content, long sizeBytes, String contentType);

    InputStream retrieve(String storageKey);

    void delete(String storageKey);
}
