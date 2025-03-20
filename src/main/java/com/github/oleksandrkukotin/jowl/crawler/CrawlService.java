package com.github.oleksandrkukotin.jowl.crawler;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocPageParser;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocWebCrawler;
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

    private final JavadocWebCrawler javadocWebCrawler;
    private final JavadocPageParser javadocPageParser;
    private final IndexService indexService;
    private final ExecutorService executorService;

    private final AtomicInteger crawlCounter = new AtomicInteger(0);
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    private volatile boolean isStopped = false;

    public CrawlService(JavadocWebCrawler javadocWebCrawler, JavadocPageParser javadocPageParser, IndexService indexService,
                        @Value("${crawler.thread.pool.size}") int threadPoolSize) {
        this.javadocWebCrawler = javadocWebCrawler;
        this.javadocPageParser = javadocPageParser;
        this.indexService = indexService;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void submitCrawlTask(String url, int depth) {
        isStopped = false;
        executorService.submit(() -> crawlRecursively(url, depth));
    }

    private void crawlRecursively(String url, int depth) {
        if (isCrawlConditionsViolated(url, depth)) return;

        crawlCounter.incrementAndGet();
        Optional<Document> doc = javadocWebCrawler.fetchPage(url);
        if (doc.isEmpty()) {
            logger.info("No page found for {}", url);
            return;
        }
        Optional<JavadocCrawledPage> page = javadocPageParser.parsePage(url, doc.get());

        if (page.isEmpty()) {
            logger.info("No page found for {}", url);
            return;
        }
        indexService.indexDocument(page.get());
        page.get().links().forEach(link -> executorService.submit(() -> crawlRecursively(link, depth - 1)));
        logger.info("Crawling & indexing complete at {}th depth for {}", depth, url);
    }

    private boolean isCrawlConditionsViolated(String url, int depth) {
        if (depth <= 0) {
            logger.info("Stopping crawl recursion at URL {} because the depth limit has been reached. {} pages crawled and indexed",
                    url, crawlCounter.get());
            return true;
        }
        if (!visitedUrls.add(url)) {
            logger.info("Skipping crawl for URL {} since it has already been visited. {} pages crawled and indexed", url, crawlCounter.get());
            return true;
        }
        if (isStopped) {
            logger.info("Crawling was explicitly stopped. {} pages crawled and indexed", crawlCounter.get());
            return true;
        }
        return false;
    }

    public void stopCrawl() {
        logger.info("Stopping crawl...");
        isStopped = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate");
                var droppedTasks = executorService.shutdownNow();
                logger.warn("ExecutorService was abruptly dropped. {} task(s) will not be executed",
                        droppedTasks.size());
                crawlCounter.set(0);
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted while shutting down executor");
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public String getCrawlerStatus() {
        return isStopped ? "Crawler is stopped" : "Crawler is running";
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    @PreDestroy
    public void shutdownExecutorService() {
        stopCrawl();
    }
}
