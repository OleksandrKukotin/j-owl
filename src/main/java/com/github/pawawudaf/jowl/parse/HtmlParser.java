<<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/WebCrawler.java
package com.example.jowl;
========
package com.github.pawawudaf.jowl.parse;
>>>>>>>> 8531e30 (Renamed core package):src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
<<<<<<< HEAD
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)

<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java
<<<<<<< HEAD
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
=======
=======
import java.io.IOException;
import java.net.SocketTimeoutException;
>>>>>>> 053647a (Small refactor of HtmlParser and minor updates of IndexController according to the parser class changes):src/main/java/com/github/pawawudaf/jowl/parse/HtmlParser.java
import java.util.HashSet;
import java.util.Map;
>>>>>>> 9f27975 (Reworked WebsiteParser's parse() method and minor logging change)
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
<<<<<<< HEAD
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
=======
import java.util.regex.Pattern;
>>>>>>> 78dddf2 (Added StopWatch logging for /index endpoint, changed link validation mechanism in WebsiteParser and logging info about efficiency, added method isEmpty() for HtmlPage)

<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java
public class WebCrawler {

<<<<<<< HEAD
<<<<<<< HEAD
    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:(\\d)+)?(/($|[a-zA-Z0-9\\-\\.\\?\\,\\'\\\\/+=&amp;%\\$#_\\*!]+))*$";
    private static final int MAX_CRAWLING_DEPTH = 2;
=======
@Component
public class HtmlParser {

    // TODO: add Concurrency
    private static final Logger logger = LoggerFactory.getLogger(HtmlParser.class);
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final String CSS_QUERY = "a[href]";
    private static final int STOP_DEPTH = 1;
>>>>>>> 053647a (Small refactor of HtmlParser and minor updates of IndexController according to the parser class changes):src/main/java/com/github/pawawudaf/jowl/parse/HtmlParser.java

    private final Set<String> visitedUrls;
    private final Queue<String> urlsToVisit;

<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java
    public WebCrawler() {
        this.visitedUrls = new HashSet<>();
        this.urlsToVisit = new LinkedList<>();
=======
    public HtmlParser(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
>>>>>>> 053647a (Small refactor of HtmlParser and minor updates of IndexController according to the parser class changes):src/main/java/com/github/pawawudaf/jowl/parse/HtmlParser.java
    }

<<<<<<< HEAD
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
=======
    private static final UrlValidator urlValidator = new UrlValidator();
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final Logger logger = LoggerFactory.getLogger(WebsiteParser.class);
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)

    public Map<String, HtmlPage> parse(String seedUrl, Map<String, HtmlPage> dataMap, int maxDepth) {
        HtmlPage htmlPage = fetchHtml(seedUrl);
        if (!htmlPage.isEmpty()) {
            dataMap.put(seedUrl, htmlPage);
        }
        if (maxDepth > 0) {
            int linksProcessed = 0;
            for (String link : parseLinks(htmlPage.getLinks())) {
                if (!dataMap.containsKey(link)) {
                    dataMap = parse(link, dataMap, maxDepth - 1);
                    linksProcessed++;
                }
            }
            logger.info("Processed " + linksProcessed + " links for URL: " + seedUrl);
        }
        return dataMap;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
    }

<<<<<<< HEAD
    private HtmlPage fetchHtml(String url) {
=======
    public Map<String, ParsedHtmlPage> parse(Set<String> urls, Map<String, ParsedHtmlPage> pages, int depth) {
=======
    public Map<String, ParsedHtmlPage> parse(Set<String> urls, Map<String, ParsedHtmlPage> pages, int depth, Set<String> visited) {
<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java
>>>>>>> 9f27975 (Reworked WebsiteParser's parse() method and minor logging change)
        if (depth < MIN_DEPTH || urls.isEmpty()) {
=======
        if (depth < STOP_DEPTH || urls.isEmpty()) {
>>>>>>> 053647a (Small refactor of HtmlParser and minor updates of IndexController according to the parser class changes):src/main/java/com/github/pawawudaf/jowl/parse/HtmlParser.java
            return pages;
        }

        Set<String> newUrls = new HashSet<>();
        for (String url : urls) {
<<<<<<< HEAD
            ParsedHtmlPage parsedHtmlPage = fetchHtml(url);
            pages.put(url, parsedHtmlPage);
            return parse(parsedHtmlPage.getLinks(), pages, depth - 1);
        }

        return pages;   // TODO: handle case when urls = 0
=======
            if (!visited.contains(url)) {
                logger.info("Current URL: {}", url);
                visited.add(url);
                ParsedHtmlPage parsedHtmlPage = fetchHtml(url);

                if (parsedHtmlPage.getLinks().isEmpty()) {
                    pages.put(url, parsedHtmlPage);
                    continue;
                }

                newUrls.addAll(parsedHtmlPage.getLinks());
                pages.put(url, parsedHtmlPage);
            }
        }

        return parse(newUrls, pages, depth - 1, visited);
>>>>>>> 9f27975 (Reworked WebsiteParser's parse() method and minor logging change)
    }


    private ParsedHtmlPage fetchHtml(String url) {
>>>>>>> 5651a41 (Completed ont of TODOs, for review another one)
        try {
            Document html = Jsoup.connect(url).get();
<<<<<<< HEAD
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
            logger.error("Error fetching HTML from URL: " + url);
            return new HtmlPage();
=======

            ParsedHtmlPage parsedHtmlPage = new ParsedHtmlPage();
            parsedHtmlPage.setTitle(html.title());
            parsedHtmlPage.setBody(html.body());
            parsedHtmlPage.setLinks(parseLinks(html.select(CSS_QUERY)));
            return parsedHtmlPage;
<<<<<<< HEAD:src/main/java/com/github/pawawudaf/jowl/parse/WebsiteParser.java
        } catch (Exception e) {
            LOGGER.error("Error fetching HTML from URL: {}", url);
            return new ParsedHtmlPage();
>>>>>>> 9f27975 (Reworked WebsiteParser's parse() method and minor logging change)
=======
        } catch (SocketTimeoutException e) {
            logger.error("A connection timed out while processing the following URL: {}", url);
        } catch (HttpStatusException e) {
            logger.error("A resource accessed by URL {} gave response with status code {}", url, e.getStatusCode());
        } catch (IOException e) {
            logger.error("IOException caused by error during fetching HTML from the following URL: {}", url);
>>>>>>> 053647a (Small refactor of HtmlParser and minor updates of IndexController according to the parser class changes):src/main/java/com/github/pawawudaf/jowl/parse/HtmlParser.java
        }
        return new ParsedHtmlPage();
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
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
        return urlValidator.isValid(url);
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)
=======
        return !MEDIA_PATTERN.matcher(url.toLowerCase()).find() && urlValidator.isValid(url);
>>>>>>> 78dddf2 (Added StopWatch logging for /index endpoint, changed link validation mechanism in WebsiteParser and logging info about efficiency, added method isEmpty() for HtmlPage)
    }
}
