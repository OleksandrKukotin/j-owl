package com.github.oleksandrkukotin.jowl.crawler;

import java.util.List;

public record CrawledPage(
        String url,
        String title,
        String content,
        List<String> links
) {
}
