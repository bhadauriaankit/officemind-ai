package com.officemind.application.documentchunk;

import java.io.InputStream;

/**
 * Extracts plain text from an uploaded file's raw bytes, regardless of
 * format (PDF, DOCX, TXT, ...). Implementation auto-detects format.
 */
public interface TextExtractorPort {

    String extractText(InputStream content, String contentType, String fileName);
}
