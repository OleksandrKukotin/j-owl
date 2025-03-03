package com.github.oleksandrkukotin.jowl.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class WebCrawler {

    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
    private static final int TIMEOUT = 10 * 1000; //10 sec

    public Optional<Document> fetchPage(String url) {
        try {
            Document page = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();
            String title = page.title();
            logger.info("Page fetched: {}", title);
            return Optional.of(page);
        } catch (IOException e) {
            logger.error("Error fetching page: ", e);
            return Optional.empty();
        }
    }

}
