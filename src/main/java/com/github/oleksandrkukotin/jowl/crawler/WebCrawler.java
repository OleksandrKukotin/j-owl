package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.nodes.Document;

import java.util.Optional;

public interface WebCrawler {

    Optional<Document> fetchPageWithRetries(String url, int maxRetries);

    Optional<Document> fetchPage(String url);
}
