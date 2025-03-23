package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;

public class ClassSearchResult implements SearchResult {

    private final String title;
    private final String url;
    private final String snippet;

    public ClassSearchResult(Document document) {
        this.title = document.get("title");
        this.url = document.get("url");
        this.snippet = document.get("snippet");
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
}
