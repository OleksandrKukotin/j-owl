<<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/WebCrawler.java
package com.example.jowl;
========
package com.github.pawawudaf.jowl.parse;
>>>>>>>> 8531e30 (Renamed core package):src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
<<<<<<< HEAD
import java.io.PrintWriter;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
<<<<<<< HEAD
import java.util.Queue;
import java.util.Set;
=======
import java.util.*;
<<<<<<< HEAD
import java.util.concurrent.ConcurrentHashMap;
>>>>>>> 841f8d3 (Implemented using of a recursion and Stream API in WebsiteParser class)
=======
import java.util.concurrent.ConcurrentMap;
>>>>>>> 8fb2ea2 (Added ParsedData class)
=======
import java.util.*;
>>>>>>> 3c98ae6 (Implemented IndexService, WebsiteController moved to IndexingController, small upgrades of ParsedData and WebsiteParser)
=======
import java.util.Map;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)

public class WebCrawler {

<<<<<<< HEAD
    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:(\\d)+)?(/($|[a-zA-Z0-9\\-\\.\\?\\,\\'\\\\/+=&amp;%\\$#_\\*!]+))*$";
    private static final int MAX_CRAWLING_DEPTH = 2;

    private final Set<String> visitedUrls;
    private final Queue<String> urlsToVisit;

    public WebCrawler() {
        this.visitedUrls = new HashSet<>();
        this.urlsToVisit = new LinkedList<>();
    }

    public void crawl(String seedUrl) {
        int currentDepth = 0;
        urlsToVisit.add(seedUrl);
        while (!urlsToVisit.isEmpty() && currentDepth <= MAX_CRAWLING_DEPTH) {
            String url = urlsToVisit.remove();
            visitedUrls.add(url);
            String html = fetchHtml(url);
            List<String> links = parseLinks(html);
            for (String link : links) {
                if (!visitedUrls.contains(link)) {
                    urlsToVisit.add(link);
                }
            }
            currentDepth++;
            storeResults(html);
=======
    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)";
<<<<<<< HEAD

    private static final int MAX_CRAWLING_DEPTH = 2;

    public ParsedData parse(String seedUrl, ParsedData parsedData, int currentDepth) {
        if (currentDepth > MAX_CRAWLING_DEPTH) {
<<<<<<< HEAD
            return map;
>>>>>>> 841f8d3 (Implemented using of a recursion and Stream API in WebsiteParser class)
=======
            return parsedData;
>>>>>>> 8fb2ea2 (Added ParsedData class)
        }

        String html = fetchHtml(seedUrl);
        parsedData.putObject(seedUrl, html);
        List<String> parsedLinks = parseLinks(html);

        parsedLinks.stream()
            .filter(link -> !parsedData.isUrlContained(link))
            .forEach(link -> {
                parse(link, parsedData, currentDepth + 1);
            });

        return parsedData;
=======

    public Map<String, HtmlPage> parse(String seedUrl, Map<String, HtmlPage> dataMap) {
        HtmlPage htmlPage = fetchHtml(seedUrl);
        for (String link : parseLinks(htmlPage.getLinks())) {
            if (dataMap.containsKey(link)) {
                continue;
            }
            dataMap.put(link, htmlPage);
            parse(link, dataMap);
        }
        return dataMap;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
    }

    private HtmlPage fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();
<<<<<<< HEAD
            return html.toString();
=======
            HtmlPage htmlPage = new HtmlPage();
            htmlPage.setTitle(html.title());
            htmlPage.setBody(html.body());
            htmlPage.setLinks(html.select("a[href]"));

            return htmlPage;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
        } catch (IOException e) {
            throw new FetchHtmlException("Error fetching HTML from URL: " + url, e);
        }
    }

<<<<<<< HEAD
    private List<String> parseLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

<<<<<<< HEAD
        for (Element linkElement : linkElements) {
            String link = linkElement.attr("href");
            if(isLinkValid(link)) {
                links.add(link);
            }
        }
        return links;
=======
        return linkElements.stream()
=======
    private List<String> parseLinks(Elements links) {
        return links.stream()
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .toList();
>>>>>>> 841f8d3 (Implemented using of a recursion and Stream API in WebsiteParser class)
    }

    private boolean isLinkValid(String url) {
        return url.contains(LINK_VALIDATION_REGEX);
    }

<<<<<<< HEAD
    private void storeResults(String html) {
        try (PrintWriter out = new PrintWriter(new FileWriter("results.txt", true))) {
            out.println(html);
        } catch (IOException e) {
            e.printStackTrace();
=======
    private static final class FetchHtmlException extends RuntimeException {

<<<<<<< HEAD
        public FetchHtmlException(String message, Throwable cause) {
            super(message, cause);
>>>>>>> 3c98ae6 (Implemented IndexService, WebsiteController moved to IndexingController, small upgrades of ParsedData and WebsiteParser)
=======
        public FetchHtmlException(String message, Exception exception) {
            super(message, exception);
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
        }
    }
}
