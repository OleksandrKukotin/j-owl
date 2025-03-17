package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import java.util.Set;

public record JavadocCrawledPage(
        String url,
        String className,
        String classDescription,
        Set<String> links,
        Set<JavadocMethod> methods
) {
}
