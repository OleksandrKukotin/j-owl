package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.document.Document;

// TODO: to implement
public class SearchResultFactory {

    public SearchResult createMethodSearchResult() {
        return new MethodSearchResult(new Document());
    }

    public SearchResult createClassSearchResult() {
        return new ClassSearchResult(new Document());
    }
}
