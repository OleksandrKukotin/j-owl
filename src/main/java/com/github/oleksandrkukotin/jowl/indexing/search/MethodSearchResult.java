package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.document.Document;

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

public class MethodSearchResult implements SearchResult {

    private final String url;
    private final String snippet;
    private final String methodName;
    private final String methodSignature;

    public MethodSearchResult(Document document) {
        this.url = document.get(CLASS_URL) + "#" + document.get(METHOD_NAME);
        this.methodName = document.get(METHOD_NAME);
        this.methodSignature = document.get(METHOD_SIGNATURE);
        this.snippet = document.get(SNIPPET);
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
}
