package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;

public class SearchResult {

    private String title;
    private String url;
    private String snippet;
//    private String content;

    public SearchResult(Document document) {
        this.url = document.get("url");
        this.title = generateTitle(document);
        this.snippet = document.get("snippet");
//        this.content = document.get("content");
    }

    private String generateTitle(Document document) {
        String rawTitle = document.get("title");

        if (this.url.contains("#")) {
            String methodName = this.url.substring(url.indexOf("#") + 1);
            return rawTitle + " " + methodName;
        }
        return rawTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
}
