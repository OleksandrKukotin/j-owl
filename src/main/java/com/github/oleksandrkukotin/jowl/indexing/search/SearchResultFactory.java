package com.github.oleksandrkukotin.jowl.indexing.search;

import org.apache.lucene.document.Document;

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

public class SearchResultFactory {

    public static SearchResult fromDocument(Document doc) {
        if (doc == null) return null;
        if (doc.get(METHOD_NAME) != null) {
            return new MethodSearchResult(doc);
        } else {
            return new ClassSearchResult(doc);
        }
    }
}
