package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import java.util.Set;

public record JavadocCrawledPage(
        String url,
        String title,
        String content,
        Set<String> links
) {
}
