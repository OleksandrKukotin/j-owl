package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
    public static final int SNIPPET_SIZE = 200;
    public static final int MAX_FRAGMENTS = 3;
    public static final String DESCRIPTION_FIELD = "<b>Description:</b> ";
    public static final String NEWLINE = "<br/>";

    private final Analyzer analyzer = new StandardAnalyzer();
    private final IndexSearcher indexSearcher;

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    public List<SearchResult> search(String queryString, int maxResults) throws ParseException, IOException {
        logger.info("Executing search for query: '{}' with max results: {}", queryString, maxResults);
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{CLASS_DESCRIPTION, METHOD_SIGNATURE, METHOD_DESCRIPTION},
                analyzer);
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        StoredFields storedFields = indexSearcher.storedFields();

        SimpleHTMLFormatter boldFormatter = new SimpleHTMLFormatter("<b>", "</b>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(boldFormatter, scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(SNIPPET_SIZE));
        highlighter.setMaxDocCharsToAnalyze(10000);

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
                    String snippet = createInformativeSnippet(doc, highlighter);
                    if (snippet != null && !snippet.isBlank()) {
                        doc.add(new TextField(SNIPPET, snippet, Field.Store.YES));
                    }
                    return doc;
                })
                .map(SearchResultFactory::fromDocument)
                .toList();
        logger.info("Search completed. Found {} results.", results.size());
        return results;
    }

    private String createInformativeSnippet(Document doc, Highlighter highlighter) {
        StringBuilder snippetBuilder = new StringBuilder();
        
        // Determine if this is a method or class document
        boolean isMethod = doc.get(METHOD_NAME) != null;
        
        if (isMethod) {
            // For method documents, create a rich snippet
            snippetBuilder.append(createMethodSnippet(doc, highlighter));
        } else {
            // For class documents, create a rich snippet
            snippetBuilder.append(createClassSnippet(doc, highlighter));
        }
        
        return snippetBuilder.toString();
    }
    
    private String createMethodSnippet(Document doc, Highlighter highlighter) {
        StringBuilder snippet = new StringBuilder();
        
        // Add class context
        String className = extractClassName(doc.get(CLASS_URL));
        String packageName = extractPackageName(className);
        if (packageName != null && !packageName.isBlank()) {
            snippet.append("<b>Package:</b> ").append(packageName).append(NEWLINE);
        }
        if (className != null && !className.isBlank()) {
            snippet.append("<b>Class:</b> ").append(className).append(NEWLINE);
        }
        
        // Add method signature with highlighting
        String methodSignature = doc.get(METHOD_SIGNATURE);
        if (methodSignature != null && !methodSignature.isBlank()) {
            String highlightedSignature = createHighlightedSnippet(METHOD_SIGNATURE, methodSignature, highlighter);
            if (highlightedSignature != null) {
                snippet.append("<b>Method:</b> ").append(highlightedSignature).append(NEWLINE);
            } else {
                snippet.append("<b>Method:</b> ").append(truncateText(methodSignature, 150)).append(NEWLINE);
            }
        }
        
        // Add return type and modifiers if available
        String returnType = doc.get(METHOD_RETURN_TYPE);
        String modifiers = doc.get(METHOD_MODIFIERS);
        if (returnType != null || modifiers != null) {
            snippet.append("<b>Type:</b> ");
            if (modifiers != null && !modifiers.isBlank()) {
                snippet.append(modifiers).append(" ");
            }
            if (returnType != null && !returnType.isBlank()) {
                snippet.append(returnType);
            }
            snippet.append(NEWLINE);
        }
        
        // Add method description with highlighting
        String methodDescription = doc.get(METHOD_DESCRIPTION);
        if (methodDescription != null && !methodDescription.isBlank()) {
            String highlightedDescription = createHighlightedSnippet(METHOD_DESCRIPTION, methodDescription, highlighter);
            if (highlightedDescription != null) {
                snippet.append(DESCRIPTION_FIELD).append(highlightedDescription);
            } else {
                snippet.append(DESCRIPTION_FIELD).append(truncateText(methodDescription, 200));
            }
        }
        
        return snippet.toString();
    }
    
    private String createClassSnippet(Document doc, Highlighter highlighter) {
        StringBuilder snippet = new StringBuilder();
        
        // Add package information
        String className = doc.get(CLASS_NAME);
        String packageName = extractPackageName(className);
        if (packageName != null && !packageName.isBlank()) {
            snippet.append("<b>Package:</b> ").append(packageName).append(NEWLINE);
        }
        
        // Add class name
        if (className != null && !className.isBlank()) {
            snippet.append("<b>Class:</b> ").append(className).append(NEWLINE);
        }
        
        // Add class description with highlighting
        String classDescription = doc.get(CLASS_DESCRIPTION);
        if (classDescription != null && !classDescription.isBlank()) {
            String highlightedDescription = createHighlightedSnippet(CLASS_DESCRIPTION, classDescription, highlighter);
            if (highlightedDescription != null) {
                snippet.append(DESCRIPTION_FIELD).append(highlightedDescription);
            } else {
                snippet.append(DESCRIPTION_FIELD).append(truncateText(classDescription, 300));
            }
        }
        
        return snippet.toString();
    }
    
    private String createHighlightedSnippet(String fieldName, String text, Highlighter highlighter) {
        try (TokenStream stream = analyzer.tokenStream(fieldName, new StringReader(text))) {
            return highlighter.getBestFragment(stream, text);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvalidTokenOffsetsException e) {
            logger.error("Highlighting failed for text {}", text, e);
            return null;
        }
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        // Try to truncate at a word boundary
        String truncated = text.substring(0, maxLength);
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > maxLength * 0.8) { // Only use word boundary if it's not too far back
            truncated = truncated.substring(0, lastSpace);
        }
        
        return truncated + "...";
    }
    
    private String extractClassName(String classUrl) {
        if (classUrl == null || classUrl.isBlank()) {
            return "";
        }
        
        // Extract class name from URL (assuming URL ends with class name)
        String[] parts = classUrl.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            // Remove .html extension if present
            if (lastPart.endsWith(".html")) {
                lastPart = lastPart.substring(0, lastPart.length() - 5);
            }
            return lastPart;
        }
        return "";
    }
    
    private String extractPackageName(String className) {
        if (className == null || className.isBlank()) {
            return "";
        }
        
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            return className.substring(0, lastDot);
        }
        return "";
    }
}
