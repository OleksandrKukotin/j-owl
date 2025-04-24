package com.github.oleksandrkukotin.jowl.crawler.javadoc;

import com.github.oleksandrkukotin.jowl.crawler.PageParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JavadocPageParser implements PageParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavadocPageParser.class);

    // TO TEST
    @Override
    public Optional<JavadocCrawledPage> parsePage(String url, Document document) {
        if (document == null) return Optional.empty();

        Optional<String> optionalTitle = Optional.of(document.select("h1").text());
        String className = optionalTitle.orElse("");
        String classDescription = document.select("div.block").text(); // Class description
        Set<String> urls = extractLinks(document, url);
        Set<JavadocMethod> methods = extractMethods(document);

        return Optional.of(new JavadocCrawledPage(url, className, classDescription, urls, methods));
    }

    private Set<String> extractLinks(Document document, String baseUrl) {
        return document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty())
                .filter(link -> !link.contains("#"))
                .filter(link -> isTheSameDomain(baseUrl, link))
                .collect(Collectors.toSet());
    }

    private boolean isTheSameDomain(String baseUrl, String targetUrl) {
        try {
            URI baseUri = new URI(baseUrl);
            URL url = baseUri.toURL();
            URI targetUri = new URI(targetUrl);
            URL target = targetUri.toURL();
            return url.getHost().equalsIgnoreCase(target.getHost());
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.debug("Error parsing URLs: baseUrl={}, targetUrl={}", baseUrl, targetUrl, e);
            return false;
        }
    }

    private Set<JavadocMethod> extractMethods(Document document) {
        final String PRIMARY_SELECTOR = "section#method-detail";
        final String SECONDARY_SELECTOR = "li.detail";

        Elements methodElements = document.select(PRIMARY_SELECTOR);
        if (methodElements.isEmpty()) {
            LOGGER.warn("No primary methods found");
            methodElements = document.select(SECONDARY_SELECTOR);
        }

        return methodElements.stream()
                .map(JavadocPageParser::parseMethod)
                .collect(Collectors.toSet());
    }

    private static JavadocMethod parseMethod(Element methodElement) {
        String methodName = methodElement.select("h3").text();
        String modifiers = methodElement.select("span.modifiers").text();
        String returnType = methodElement.select("span.return-type").text();
        String fullSignature = methodElement.select("div.member-signature").text();
        String methodDescription = methodElement.select("div.block").text();

        return new JavadocMethod(methodName, modifiers, returnType, fullSignature, methodDescription);
    }
}
