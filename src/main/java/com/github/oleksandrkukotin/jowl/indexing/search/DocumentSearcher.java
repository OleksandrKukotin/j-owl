package com.github.oleksandrkukotin.jowl.indexing.search;

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

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

@Component
public class DocumentSearcher {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSearcher.class);
    public static final int SNIPPET_SIZE = 100;

    private final Analyzer analyzer = new StandardAnalyzer();
    private final IndexSearcher indexSearcher;

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    public List<SearchResult> search(String queryString, int maxResults) throws ParseException, IOException {
        logger.info("Executing search for query: '{}' with max results: {}", queryString, maxResults);
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{CLASS_DESCRIPTION, METHOD_SIGNATURE},
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
                    String text = doc.get(CLASS_DESCRIPTION);
                    if (text != null && !text.isBlank()) {
                        String highlightedText = createHighlightedSnippet(CLASS_DESCRIPTION, text, highlighter);
                        if (highlightedText != null) {
                            doc.add(new TextField(SNIPPET, highlightedText, Field.Store.YES));
                        }
                    }
                    return doc;
                })
                .map(SearchResultFactory::fromDocument)
                .toList();
        logger.info("Search completed. Found {} results.", results.size());
        return results;
    }

    private String createHighlightedSnippet(String fieldName, String text, Highlighter highlighter) {
        try (TokenStream stream = analyzer.tokenStream(fieldName, new StringReader(text))) {
            return highlighter.getBestFragment(stream, text);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "";
        } catch (InvalidTokenOffsetsException e) {
            logger.error("Highlighting failed for text {}", text, e);
            return "";
        }
    }
}
