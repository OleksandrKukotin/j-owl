package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import com.github.oleksandrkukotin.jowl.crawler.CrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.PageParser;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JavadocPageParser implements PageParser {

    @Override
    public Optional<CrawledPage> parsePage(String url, Document document) {
        if (document == null) return Optional.empty();

        String title = document.title();
        // TODO: make the parser parse only useful content
        String content = document.body().text();
        // TODO: make the parser stay only on one domain
        Set<String> links = document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty() && !link.contains("#"))
                .collect(Collectors.toSet());

        return Optional.of(new CrawledPage(url, title, content, links));
    }
}
