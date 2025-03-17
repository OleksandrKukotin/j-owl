package com.github.oleksandrkukotin.jowl.crawler.javadoc;

public record JavadocMethod(
        String name,
        String modifiers,
        String returnType,
        String signature,
        String description
) {
}
