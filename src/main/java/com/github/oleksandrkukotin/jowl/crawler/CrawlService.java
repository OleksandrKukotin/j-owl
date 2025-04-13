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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private final JavadocWebCrawler javadocWebCrawler;
    private final JavadocPageParser javadocPageParser;
    private final IndexService indexService;
    private final ExecutorService executorService;

    private final AtomicBoolean isStopped = new AtomicBoolean(true);
    private final AtomicInteger crawlCounter = new AtomicInteger(0);
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    public CrawlService(JavadocWebCrawler javadocWebCrawler, JavadocPageParser javadocPageParser, IndexService indexService,
                        @Value("${crawler.concurrent.thread.pool.size}") int threadPoolSize) {
        this.javadocWebCrawler = javadocWebCrawler;
        this.javadocPageParser = javadocPageParser;
        this.indexService = indexService;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void submitCrawlTask(String url, int depth) {
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            isStopped.set(false);
            executorService.execute(() -> crawlRecursively(url, depth));
        }
    }

    private void crawlRecursively(String url, int depth) {
        if (isCrawlConditionsViolated(url, depth)) return;

        crawlCounter.incrementAndGet();
        Optional<Document> doc = javadocWebCrawler.fetchPage(url);
        if (doc.isEmpty()) {
            logger.info("No page fetched for {}", url);
            return;
        }
        Optional<JavadocCrawledPage> page = javadocPageParser.parsePage(url, doc.get());
        page.ifPresentOrElse(
                javadocCrawledPage -> {
                    indexService.indexDocument(javadocCrawledPage);
                    javadocCrawledPage.links().forEach(link -> {
                        executorService.execute(() -> crawlRecursively(link, depth - 1));
                        logger.info("Crawling & indexing complete for {} at {}th depth", link, depth - 1);
                    });
                },
                () -> logger.info("No page parsed for {}", url)
        );
    }

    private boolean isCrawlConditionsViolated(String url, int depth) {
        if (isStopped.get()) {
            logger.info("Crawling was explicitly stopped. {} pages crawled and indexed, please commit the index",
                    crawlCounter.get());
            return true;
        }
        if (depth <= 0) {
            logger.info("Stopping crawl recursion at URL {} because the depth limit has been reached. {} pages crawled and indexed",
                    url, crawlCounter.get());
            return true;
        }
        if (!visitedUrls.add(url)) {
            logger.info("Skipping crawl for URL {} since it has already been visited. {} pages crawled and indexed", url, crawlCounter.get());
            return true;
        }
        return false;
    }

    public void stopCrawl() {
        logger.info("Stopping crawl...");
        isStopped.set(true);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warn("Forcefully shutting down executor...");
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
        return isStopped.get() ? "Crawler is stopped" : "Crawler is running";
    }

    public int getCrawlCount() {
        return crawlCounter.get();
    }

    @PreDestroy
    public void shutdownExecutorService() {
        stopCrawl();
    }
}
