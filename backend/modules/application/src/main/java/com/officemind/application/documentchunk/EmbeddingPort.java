package com.officemind.application.documentchunk;

import java.util.List;

public interface EmbeddingPort {

    /** Embeds a single piece of text into a fixed-size float vector. */
    float[] embed(String text);

    /** Batch variant — implementations may call the model once per item
     *  or use a native batch API if available; callers should not assume
     *  either. Order of results matches order of input. */
    List<float[]> embedAll(List<String> texts);
}
