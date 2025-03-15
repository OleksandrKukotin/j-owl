package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import com.github.oleksandrkukotin.jowl.crawler.PageParser;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JavadocPageParser implements PageParser {

    @Override
    public Optional<JavadocCrawledPage> parsePage(String url, Document document) {
        if (document == null) return Optional.empty();

        String title = document.title();
        // TODO: analyze and understand Javadoc page structure
        // TODO: make the parser parse only useful content
        String content = document.body().text();
        // TODO: make the parser stay only on one domain
        Set<String> links = document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty() && !link.contains("#"))
                .collect(Collectors.toSet());

        return Optional.of(new JavadocCrawledPage(url, title, content, links));
    }

//    @Override
//    public Optional<JavadocCrawledPage> parsePage(String url, Document document) {
//        if (document == null) return Optional.empty();
//
//        String title = document.select("h1, h2").first().text(); // Class name
//        String packageName = document.select("div.subTitle").text(); // Package name
//        String description = document.select("div.block").text(); // Class description
//
//        // Extract methods
//        Set<String> methods = document.select("table.memberSummary tbody tr")
//                .stream()
//                .map(row -> row.select("td.colFirst a").text()) // Method name
//                .filter(name -> !name.isEmpty())
//                .collect(Collectors.toSet());
//
//        return Optional.of(new JavadocCrawledPage(url, title, packageName + "\n" + description, methods));
//    }
}
