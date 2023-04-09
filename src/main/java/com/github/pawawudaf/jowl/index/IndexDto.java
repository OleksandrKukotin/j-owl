package com.github.pawawudaf.jowl.index;

public class IndexDto {

    private String title;
    private String link;
<<<<<<< HEAD
    private TopDocs searchResults;
=======
    private String body;
>>>>>>> b35e441 (Upgraded classes of index package)

    public IndexDto(String title, String link, String body) {
        this.title = title;
        this.link = link;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
