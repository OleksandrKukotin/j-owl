package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.nodes.Document;

import java.util.Optional;

public interface PageParser {

    Optional<CrawledPage> parsePage(String url, Document document);
}
