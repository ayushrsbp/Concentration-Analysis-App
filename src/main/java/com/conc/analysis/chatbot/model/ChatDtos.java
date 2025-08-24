package com.conc.analysis.chatbot.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class ChatDtos {

    public static class ChatRequest {
        @NotBlank
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class Source {
        private String file;
        private int chunk;
        private String snippet;
        private float score;

        public Source() {}
        public Source(String file, int chunk, String snippet, float score) {
            this.file = file; this.chunk = chunk; this.snippet = snippet; this.score = score;
        }
        public String getFile() { return file; }
        public int getChunk() { return chunk; }
        public String getSnippet() { return snippet; }
        public float getScore() { return score; }
        public void setFile(String file) { this.file = file; }
        public void setChunk(int chunk) { this.chunk = chunk; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        public void setScore(float score) { this.score = score; }
    }

    public static class ChatResponse {
        private String answer;
        private List<Source> sources;
        private long tookMs;

        public ChatResponse() {}
        public ChatResponse(String answer, List<Source> sources, long tookMs) {
            this.answer = answer; this.sources = sources; this.tookMs = tookMs;
        }

        public String getAnswer() { return answer; }
        public List<Source> getSources() { return sources; }
        public long getTookMs() { return tookMs; }
        public void setAnswer(String answer) { this.answer = answer; }
        public void setSources(List<Source> sources) { this.sources = sources; }
        public void setTookMs(long tookMs) { this.tookMs = tookMs; }
    }
}

