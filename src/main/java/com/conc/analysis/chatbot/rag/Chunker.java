package com.conc.analysis.chatbot.rag;

import java.util.ArrayList;
import java.util.List;

public class Chunker {
    public static List<String> chunk(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null) return chunks;
        text = text.replaceAll("\\s+"," ").trim();
        int n = text.length();
        int i = 0;
        while (i < n) {
            int end = Math.min(n, i + chunkSize);
            String piece = text.substring(i, end);
            chunks.add(piece);
            if (end == n) break;
            i = end - overlap;
            if (i < 0) i = 0;
        }
        return chunks;
    }
}

