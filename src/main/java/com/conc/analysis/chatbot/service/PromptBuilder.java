package com.conc.analysis.chatbot.service;

import java.util.List;
import com.conc.analysis.chatbot.model.ChatDtos.Source;

public class PromptBuilder {

    public static String systemPrompt() {
        return """
        You are Vent Baba, a domain assistant on ventilation (HVAC, airflow, ducts, IAQ, codes, fans, AHUs, etc.).
        Use ONLY the provided CONTEXT below. If the answer is not in the context, say you don't have enough information.
        Be precise, concise, safety-first, and show any formulas or steps if calculations are involved.
        Cite sources by listing file names you used at the end under "Sources".
        """;
    }

    public static String userPrompt(String userMessage, String retrievalContext, List<Source> sources, Double math) {
        StringBuilder sb = new StringBuilder();
        if (math != null) {
            sb.append("MATH_COMPUTATION:\n");
            sb.append("The user's expression evaluates to: ").append(math).append("\n\n");
        }
        sb.append("CONTEXT (from local documents):\n");
        if (retrievalContext == null || retrievalContext.isBlank()) {
            sb.append("(No matching documents found.)\n");
        } else {
            sb.append(retrievalContext).append("\n");
        }
        sb.append("\nUSER QUESTION:\n").append(userMessage).append("\n\n");
        sb.append("INSTRUCTIONS:\n");
        sb.append("- Answer based strictly on CONTEXT. If missing, say so and suggest which document might be needed.\n");
        sb.append("- Keep the answer clear and structured.\n");
        sb.append("- Provide calculations and units if applicable.\n");
        sb.append("- End with a 'Sources' section listing the files used.\n");
        if (sources != null && !sources.isEmpty()) {
            sb.append("- Available source files:\n");
            for (int i=0;i<sources.size();i++) {
                var s = sources.get(i);
                sb.append("  ").append(i+1).append(". ").append(s.getFile()).append(" (chunk ").append(s.getChunk()).append(")\n");
            }
        }
        return sb.toString();
    }
}

