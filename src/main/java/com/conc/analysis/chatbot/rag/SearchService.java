package com.conc.analysis.chatbot.rag;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.conc.analysis.chatbot.model.ChatDtos.Source;

@Service
public class SearchService {

    private final IndexService indexService;
    private final Analyzer analyzer;

    @Value("${rag.topK}")
    private int topK;

    @Value("${rag.maxContextChars}")
    private int maxContextChars;

    public SearchService(IndexService indexService, Analyzer analyzer) {
        this.indexService = indexService;
        this.analyzer = analyzer;
    }

    public static class RetrievalResult {
        public final String context;
        public final List<Source> sources;
        public RetrievalResult(String context, List<Source> sources) {
            this.context = context; this.sources = sources;
        }
    }

    public RetrievalResult retrieve(String query) throws Exception {
        Directory dir = indexService.getDirectory();
        if (dir == null) {
            throw new IllegalStateException("Index not available. Did you add files to ./data?");
        }
        try (var reader = DirectoryReader.open(dir)) {
            var searcher = new IndexSearcher(reader);

            QueryParser parser = new QueryParser("content", analyzer);
            Query q = parser.parse(QueryParser.escape(query));
            TopDocs topDocs = searcher.search(q, topK);
            StringBuilder ctx = new StringBuilder();
            List<Source> sources = new ArrayList<>();

            FastVectorHighlighter highlighter = new FastVectorHighlighter();
            FieldQuery fq = highlighter.getFieldQuery(q);

            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document d = searcher.doc(sd.doc);
                String content = d.get("content");
                String path = d.get("path");
                int chunk = Integer.parseInt(d.get("chunk"));
                // add with separators and source tags
                if (ctx.length() + content.length() > maxContextChars) break;
                ctx.append("\n----- SOURCE ").append(sources.size()+1).append(" -----\n");
                ctx.append("FILE: ").append(path).append("\n");
                ctx.append("CHUNK: ").append(chunk).append("\n");
                ctx.append(content).append("\n");

                // simple snippet
                String snippet = content.length() > 300 ? content.substring(0, 300) + "..." : content;
                sources.add(new Source(path, chunk, snippet, sd.score));
            }
            return new RetrievalResult(ctx.toString(), sources);
        }
    }
}

