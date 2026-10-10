package com.examly.springapp.model;

import lombok.Getter;
import lombok.Setter;

// Answer of POST /api/chat.
@Getter
@Setter
public class ChatResponse {

    private String reply;
    private boolean matched;
    private String matchedQuestion;
    private String category;
    private double confidence;
    // "semantic" (Gemini embeddings) or "lexical" (word-overlap fallback)
    private String source;
    private String sessionId;
    private String resolvedQuestion;

    public ChatResponse() {
    }
}
