package com.conc.analysis.chatbot.rag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
@Service
public class IndexService {

    private final Analyzer analyzer;
    private final DocumentLoader loader;

    @Value("${data.folder}")
    private String dataFolder;

    @Value("${index.folder}")
    private String indexFolder;

    @Value("${rag.chunkSize}")
    private int chunkSize;

    @Value("${rag.chunkOverlap}")
    private int chunkOverlap;

    private Directory directory;

    
    public IndexService(Analyzer analyzer) {
        this.analyzer = analyzer;
        this.loader = new DocumentLoader();
    }

    @PostConstruct
    public void buildIndex() throws IOException {
        Path data = Paths.get(dataFolder);
        Path idx = Paths.get(indexFolder);
        // Files.createDirectories(idx);
        directory = FSDirectory.open(idx);
        var config = new IndexWriterConfig(analyzer);
        System.out.println();
        System.out.println("1st Check passed");
        System.out.println();
        try (var writer = new IndexWriter(directory, config)) {
            // writer.deleteAll(); // rebuild
            var files = DocumentLoader.listFiles(data);
            for (Path file : files) {
                String text;
                try {
                    System.out.println();
                    System.out.println("2nd Check passed");
                    System.out.println();
                    text = loader.extractText(file);
                    System.out.println();
                    System.out.println("3rd Check passed");
                    System.out.println();
                } catch (Exception e) {
                    continue; // skip unreadable file
                }
                System.out.println();
                System.out.println("4th Check passed");
                System.out.println(text);
                System.out.println();
                var chunks = Chunker.chunk(text, chunkSize, chunkOverlap);
                int c = 0;
                for (String ch : chunks) {
                    Document doc = new Document();
                    doc.add(new StringField("path", file.toAbsolutePath().toString(), Field.Store.YES));
                    doc.add(new IntPoint("chunk", c));
                    doc.add(new StoredField("chunk", c));
                    doc.add(new TextField("content", ch, Field.Store.YES));
                    writer.addDocument(doc);
                    c++;
                }
            }
            writer.commit();
        }
    }

    public Directory getDirectory() {
        return directory;
    }
}