package com.github.oleksandrkukotin.jowl.crawler;

import com.github.oleksandrkukotin.jowl.indexing.IndexService;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private final WebCrawler webCrawler;
    private final PageParser pageParser;
    private final IndexService indexService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(12);

    private final AtomicInteger crawlCounter = new AtomicInteger(0);
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    public CrawlService(WebCrawler webCrawler, PageParser pageParser, IndexService indexService) {
        this.webCrawler = webCrawler;
        this.pageParser = pageParser;
        this.indexService = indexService;
    }

    public void submitCrawlTask(String url, int depth) {
        executorService.submit(() -> crawlRecursively(url, depth));
    }

    private void crawlRecursively(String url, int depth) {
        if (depth <= 0 || visitedUrls.contains(url)) return;

        visitedUrls.add(url);
        crawlCounter.incrementAndGet();
        Document doc = webCrawler.fetchPage(url);
        CrawledPage page = pageParser.parsePage(url, doc);

        if (page != null) {
            indexService.indexDocument(page);
            page.links().forEach(link -> executorService.submit(() -> crawlRecursively(link, depth - 1)));
            logger.info("Crawling & indexing complete at {}th depth for {}", depth, url);
        }
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    public void resetCrawlCounter() {
        crawlCounter.set(0);
    }
}
