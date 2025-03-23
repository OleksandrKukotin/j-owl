package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;

public class MethodSearchResult implements SearchResult {

    private final String title;
    private final String url;
    private final String snippet;
    private final String methodName;
    private final String methodSignature;

    public MethodSearchResult(Document document) {
        this.url = document.get("url") + "#" + document.get("methodName");
        this.methodName = document.get("methodName");
        this.methodSignature = document.get("methodSignature");
        this.snippet = document.get("snippet");
        this.title = document.get("className") + "." + methodName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }
}
