package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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

    private final IndexSearcher indexSearcher;
    private final QueryParser queryParser;

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
        this.queryParser = new QueryParser("content", new StandardAnalyzer());
    }

    public List<Document> search(String queryString, int maxResults) throws ParseException, IOException {
        logger.info("Executing search for query: '{}' with max results: {}", queryString, maxResults);
        Query query = queryParser.parse(queryString);
        Analyzer analyzer = new StandardAnalyzer();
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
                    String text = doc.get("content");
                    TokenStream stream = analyzer.tokenStream(text, new StringReader(text));
                    String highlightedText = null;
                    try {
                        highlightedText = highlighter.getBestFragment(stream, text);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidTokenOffsetsException e) {
                        throw new RuntimeException(e);
                    }
                    doc.removeField("content");
                    doc.add(new StringField("snippet", highlightedText, Field.Store.YES));
                    return doc;
                })
                .toList();

        logger.info("Search completed. Found {} results.", results.size());
        return results;
    }
}
