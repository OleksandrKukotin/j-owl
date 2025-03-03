package com.github.oleksandrkukotin.jowl.crawler;

import com.github.oleksandrkukotin.jowl.indexing.IndexService;
import jakarta.annotation.PreDestroy;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private final WebCrawler webCrawler;
    private final PageParser pageParser;
    private final IndexService indexService;
    private final ExecutorService executorService;

    private final AtomicInteger crawlCounter = new AtomicInteger(0);
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    public CrawlService(WebCrawler webCrawler, PageParser pageParser, IndexService indexService,
                        @Value("${crawler.thread.pool.size}") int threadPoolSize) {
        this.webCrawler = webCrawler;
        this.pageParser = pageParser;
        this.indexService = indexService;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void submitCrawlTask(String url, int depth) {
        executorService.submit(() -> crawlRecursively(url, depth));
    }

    private void crawlRecursively(String url, int depth) {
        if (depth <= 0 || !visitedUrls.add(url)) return;

        crawlCounter.incrementAndGet();
        Optional<Document> doc = webCrawler.fetchPage(url);
        if (doc.isEmpty()) {
            logger.info("No page found for {}", url);
            return;
        }
        Optional<CrawledPage> page = pageParser.parsePage(url, doc.get());

        if (page.isEmpty()) {
            logger.info("No page found for {}", url);
            return;
        }
        indexService.indexDocument(page.get());
        page.get().links().forEach(link -> executorService.submit(() -> crawlRecursively(link, depth - 1)));
        logger.info("Crawling & indexing complete at {}th depth for {}", depth, url);
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    public void resetCrawlCounter() {
        crawlCounter.set(0);
    }

    @PreDestroy
    public void shutdownExecutorService() {
        logger.info("Shutting down executor");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate");
                var droppedTasks = executorService.shutdownNow();
                logger.warn("ExecutorService was abruptly dropped. {} task(s) will not be executed",
                        droppedTasks.size());
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted while shutting down executor");
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
