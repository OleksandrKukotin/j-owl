package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlService {

    private final WebCrawler webCrawler;
    private final PageParser pageParser;
    private final Set<String> visitedUrls = new HashSet<>();

    private final AtomicInteger crawlCounter = new AtomicInteger(0);

    public CrawlService(WebCrawler webCrawler, PageParser pageParser) {
        this.webCrawler = webCrawler;
        this.pageParser = pageParser;
    }

    public void crawlRecursively(String url, int depth) {
        if (depth <= 0 || visitedUrls.contains(url)) return;

        visitedUrls.add(url);
        crawlCounter.incrementAndGet();
        Document doc = webCrawler.fetchPage(url);
        CrawledPage page = pageParser.parsePage(url, doc);

        if (page != null) {
            page.links().forEach(link -> crawlRecursively(link, depth - 1));
        }
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    public void resetCrawlCounter() {
        crawlCounter.set(0);
    }
}
