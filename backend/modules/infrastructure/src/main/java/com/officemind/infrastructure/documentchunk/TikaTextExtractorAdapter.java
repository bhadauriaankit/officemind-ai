package com.officemind.infrastructure.documentchunk;

import com.officemind.application.documentchunk.TextExtractorPort;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaTextExtractorAdapter implements TextExtractorPort {

    private final Tika tika = new Tika();

    @Override
    public String extractText(InputStream content, String contentType, String fileName) {
        try {
            // Tika auto-detects format from content/name; contentType is
            // advisory only here since MinIO's stored contentType isn't
            // always trustworthy (browser-supplied on upload).
            return tika.parseToString(content);
        } catch (IOException | org.apache.tika.exception.TikaException e) {
            throw new RuntimeException("Failed to extract text from " + fileName, e);
        }
    }
}
