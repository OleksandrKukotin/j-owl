package com.github.oleksandrkukotin.jowl.crawler;

import java.util.Set;

public record CrawledPage(
        String url,
        String title,
        String content,
        Set<String> links
) {
}
