package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebCrawler {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";

    public Document fetchPage(String url) {
        try {
            Document page = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10 * 1000)
                    .get();
            String log = "Page fetched: " + page.title();
            logger.info(log);
            return page;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
