package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.document.Document;

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

public class MethodSearchResult implements SearchResult {

    private final String url;
    private final String snippet;
    private final String methodName;
    private final String methodSignature;
    private final String returnType;
    private final String modifiers;
    private final String className;
    private final String packageName;
    private final String shortDescription;

    public MethodSearchResult(Document document) {
        this.url = document.get(CLASS_URL) + "#" + document.get(METHOD_NAME);
        this.methodName = document.get(METHOD_NAME);
        this.methodSignature = document.get(METHOD_SIGNATURE);
        this.returnType = document.get(METHOD_RETURN_TYPE);
        this.modifiers = document.get(METHOD_MODIFIERS);
        this.snippet = document.get(SNIPPET);
        this.className = extractClassName(document.get(CLASS_URL));
        this.packageName = extractPackageName(this.className);
        this.shortDescription = extractShortDescription(document.get(METHOD_DESCRIPTION));
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getModifiers() {
        return modifiers;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getShortDescription() {
        return shortDescription;
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
