package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;

import static com.github.oleksandrkukotin.jowl.indexing.LuceneFields.*;

public class ClassSearchResult implements SearchResult {

    private final String name;
    private final String url;
    private final String snippet;

    public ClassSearchResult(Document document) {
        this.name = document.get(CLASS_NAME);
        this.url = document.get(CLASS_URL);
        this.snippet = document.get(SNIPPET);
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
}
