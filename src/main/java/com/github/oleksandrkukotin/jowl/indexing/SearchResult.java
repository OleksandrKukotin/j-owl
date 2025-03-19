package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;

import java.util.List;

public class SearchResult {

    private String title;
    private String url;
    private String snippet;
    private String methodName;
    private String methodSignature;

    public SearchResult(Document document) {
        this.url = document.get("url");
        this.title = generateTitle(document);
        this.snippet = document.get("snippet");
        this.methodName = document.get("methodName");
        this.methodSignature = document.get("methodSignature");
    }

    private String generateTitle(Document document) {
        String rawTitle = document.get("className");

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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    //    public String getContent() {
//        return classDescription;
//    }
//
//    public void setContent(String classDescription) {
//        this.classDescription = classDescription;
//    }
}
