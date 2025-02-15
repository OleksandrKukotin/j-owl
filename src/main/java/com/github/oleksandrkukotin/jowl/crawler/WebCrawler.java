package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private static final String TARGET_URL = "http://docs.oracle.com/javase/17/docs/api/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";

    public WebCrawler() {
    }

    public void crawl() {
        try {
            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent(USER_AGENT)
                    .timeout(10 * 1000)
                    .get();

            logger.info(doc.title());
            logger.info(String.valueOf(doc.body()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new WebCrawler().crawl();
    }
}
