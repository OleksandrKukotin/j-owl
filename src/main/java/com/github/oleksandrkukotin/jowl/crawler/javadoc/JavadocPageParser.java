package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import com.github.oleksandrkukotin.jowl.crawler.CrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.PageParser;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JavadocPageParser implements PageParser {

    @Override
    public Optional<CrawledPage> parsePage(String url, Document document) {
        if (document == null) return Optional.empty();

        String title = document.title();
        String content = document.body().text();
        List<String> links = document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty())
                .toList();

        return Optional.of(new CrawledPage(url, title, content, links));
    }
}
