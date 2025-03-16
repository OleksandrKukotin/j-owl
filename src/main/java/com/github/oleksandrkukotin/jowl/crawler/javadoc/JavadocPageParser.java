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

        Optional<String> optionalTitle = Optional.of(document.select("h1").text());
        String title = optionalTitle.orElse("");
        String packageName = document.select("div.subTitle").next("package").text(); // Package name
        String description = document.select("div.block").text(); // Class description

        // TODO: Extract methods correctly
        Set<String> methods = document.select("table.memberSummary tbody tr")
                .stream()
                .map(row -> row.select("td.colFirst a").text()) // Method name
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        return Optional.of(new JavadocCrawledPage(url, title, packageName + "\n" + description, methods));
    }
}
