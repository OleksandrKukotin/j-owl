package com.github.oleksandrkukotin.jowl.crawler;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crawler")
public class CrawlController {

    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping("/crawl/{depth}")
    public String crawl(@RequestParam("url") String url, @PathVariable("depth") int depth) {
        crawlService.crawlRecursively(url, depth);
        return String.format("Crawl finished :]%n%d pages crawled with depth %d", crawlService.getCrawlCount(), depth);
    }

    @GetMapping("/counter")
    public int getCrawlCounter() {
        return crawlService.getCrawlCount();
    }

    @GetMapping("/counter/reset")
    public void resetCrawlCounter() {
        crawlService.resetCrawlCounter();
    }
}
