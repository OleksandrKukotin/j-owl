package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.document.Document;

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

public class ClassSearchResult implements SearchResult {

    private final String name;
    private final String url;
    private final String snippet;
    private final String packageName;
    private final String shortDescription;

    public ClassSearchResult(Document document) {
        this.name = document.get(CLASS_NAME);
        this.url = document.get(CLASS_URL);
        this.snippet = document.get(SNIPPET);
        this.packageName = extractPackageName(this.name);
        this.shortDescription = extractShortDescription(document.get(CLASS_DESCRIPTION));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getShortDescription() {
        return shortDescription;
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

    private String extractShortDescription(String description) {
        if (description == null || description.isBlank()) {
            return "";
        }
        
        // Extract the first sentence or first 100 characters
        String firstSentence = description.split("[.!?]")[0];
        if (firstSentence.length() > 100) {
            return firstSentence.substring(0, 100) + "...";
        }
        return firstSentence;
    }
}
