package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.exception.IndexSearchException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
    public static final int SNIPPET_SIZE = 100;
    // TODO: sync fields naming with DocumentIndexer
    private static final String MAIN_SEARCH_FIELD = "classDescription";
    private static final String SECOND_SEARCH_FIELD = "methodSignature";

    private final Analyzer analyzer = new StandardAnalyzer();
    private final IndexSearcher indexSearcher;

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    public List<SearchResult> search(String queryString, int maxResults) throws ParseException, IOException {
        logger.info("Executing search for query: '{}' with max results: {}", queryString, maxResults);
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{MAIN_SEARCH_FIELD, SECOND_SEARCH_FIELD},
                analyzer);
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        StoredFields storedFields = indexSearcher.storedFields();

        SimpleHTMLFormatter boldFormatter = new SimpleHTMLFormatter("<b>", "</b>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(boldFormatter, scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(SNIPPET_SIZE));

        List<SearchResult> results = Arrays.stream(topDocs.scoreDocs)
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
                    String text = doc.get(MAIN_SEARCH_FIELD);
                    if (text != null && !text.isBlank()) {
                        try (TokenStream stream = analyzer.tokenStream(MAIN_SEARCH_FIELD, new StringReader(text))) {
                            String highlightedText = highlighter.getBestFragment(stream, text);
                            if (highlightedText != null) {
                                doc.add(new TextField("snippet", highlightedText, Field.Store.YES));
                            }
                            doc.removeField(MAIN_SEARCH_FIELD);
                            doc.add(new TextField(MAIN_SEARCH_FIELD, text, Field.Store.YES));
                        } catch (IOException e) {
                            throw new IndexSearchException(e.getMessage(), e);
                        } catch (InvalidTokenOffsetsException e) {
                            logger.error("Highlighting failed for text {}", text, e);
                            throw new IndexSearchException(e.getMessage(), e);
                        }
                    }
                    return doc;
                })
                .map(doc -> {
                    if (doc.get("methodName") != null) {
                        return new MethodSearchResult(doc);
                    } else {
                        return new ClassSearchResult(doc);
                    }
                })
                .toList();
        logger.info("Search completed. Found {} results.", results.size());
        return results;
    }
}
