package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.nodes.Document;

import java.util.Optional;

public interface WebCrawler {

    Optional<Document> fetchPage(String url);
}
