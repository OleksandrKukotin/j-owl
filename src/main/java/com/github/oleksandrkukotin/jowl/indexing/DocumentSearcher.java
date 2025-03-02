package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.exception.IndexSearchException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class DocumentSearcher {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSearcher.class);
    public static final String CONTENT_FIELD_NAME = "content";

    private final IndexSearcher indexSearcher;
    private final QueryParser queryParser;
    private final Analyzer analyzer = new StandardAnalyzer();

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
        this.queryParser = new QueryParser(CONTENT_FIELD_NAME, new StandardAnalyzer());
    }

    public List<Document> search(String queryString, int maxResults) throws ParseException, IOException {
        logger.info("Executing search for query: '{}' with max results: {}", queryString, maxResults);
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        StoredFields storedFields = indexSearcher.storedFields();

        SimpleHTMLFormatter boldFormatter = new SimpleHTMLFormatter("<b>", "</b>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(boldFormatter, scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(50));

        List<Document> results = Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return storedFields.document(scoreDoc.doc);
                    } catch (IOException e) {
                        logger.error("Failed to retrieve document ID {}: {}", scoreDoc.doc, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(doc -> {
                    String text = doc.get(CONTENT_FIELD_NAME);
                    if (text != null && !text.isBlank()) {
                        try (TokenStream stream = analyzer.tokenStream(CONTENT_FIELD_NAME, new StringReader(text))) {
                            String highlightedText = highlighter.getBestFragment(stream, text);
                            doc.add(new TextField("snippet", highlightedText, Field.Store.YES));
                            doc.add(new TextField(CONTENT_FIELD_NAME, text, Field.Store.YES));
                        } catch (IOException e) {
                            throw new IndexSearchException(e.getMessage());
                        } catch (InvalidTokenOffsetsException e) {
                            logger.error("Highlighting failed for text {}", text, e);
                        }
                    }
                    return doc;
                })
                .toList();

        logger.info("Search completed. Found {} results.", results.size());
        return results;
    }
}
