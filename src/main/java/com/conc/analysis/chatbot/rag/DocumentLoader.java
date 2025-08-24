package com.conc.analysis.chatbot.rag;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

public class DocumentLoader {

    private final Tika tika = new Tika();

    public static List<Path> listFiles(Path folder) throws IOException {
        if (!Files.exists(folder)) return List.of();
        try (var s = Files.walk(folder)) {
            return s.filter(Files::isRegularFile).toList();
        }
    }

    public String extractText(Path file) throws IOException {
        try (InputStream is = new FileInputStream(file.toFile())) {
            var parser = new AutoDetectParser();
            var handler = new BodyContentHandler(-1);
            var metadata = new Metadata();
            try {
                parser.parse(is, handler, metadata);
            } catch (Exception e) {
                // Fallback: quick-detect
                try {
                    return tika.parseToString(file);
                } catch (IOException | TikaException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            return handler.toString();
        }
    }
}
