package com.github.oleksandrkukotin.jowl.crawler;

import com.github.oleksandrkukotin.jowl.indexing.IndexService;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private final WebCrawler webCrawler;
    private final PageParser pageParser;
    private final IndexService indexService;
    private final Set<String> visitedUrls = new HashSet<>();

    private final AtomicInteger crawlCounter = new AtomicInteger(0);

    public CrawlService(WebCrawler webCrawler, PageParser pageParser, IndexService indexService) {
        this.webCrawler = webCrawler;
        this.pageParser = pageParser;
        this.indexService = indexService;
    }

    public void crawlRecursively(String url, int depth) {
        if (depth <= 0 || visitedUrls.contains(url)) return;

        visitedUrls.add(url);
        crawlCounter.incrementAndGet();
        Document doc = webCrawler.fetchPage(url);
        CrawledPage page = pageParser.parsePage(url, doc);

        if (page != null) {
            indexService.indexDocument(page);
            page.links().forEach(link -> crawlRecursively(link, depth - 1));
            logger.info("Crawling & indexing complete for {}", url);
        }
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    public void resetCrawlCounter() {
        crawlCounter.set(0);
    }
}
