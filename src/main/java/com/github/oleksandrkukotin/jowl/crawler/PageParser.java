package com.github.oleksandrkukotin.jowl.crawler;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import org.jsoup.nodes.Document;

import java.util.Optional;

public interface PageParser {

    Optional<JavadocCrawledPage> parsePage(String url, Document document);
}
