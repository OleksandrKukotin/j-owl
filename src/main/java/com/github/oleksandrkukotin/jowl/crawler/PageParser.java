package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

public class PageParser {

    public PageParser() {}

    public CrawledPage parsePage(String url, Document document) {
        if (document == null) return null;

        String title = document.title();
        String content = document.body().text();
        List<String> links= document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty())
                .toList();

        return new CrawledPage(url, title, content, links);
    }
}
