package com.github.oleksandrkukotin.jowl.crawler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crawler")
public class CrawlController {

    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @PostMapping("/crawl/{depth}")
    public String crawl(@RequestParam("url") String url, @PathVariable("depth") int depth) {
        crawlService.submitCrawlTask(url, depth);
        return String.format("Crawl finished :]%n%d pages crawled with depth %d", crawlService.getCrawlCount(), depth);
    }

    @PostMapping("/crawl/stop")
    public ResponseEntity<String> stop() {
        crawlService.stopCrawl();
        return ResponseEntity.ok("Crawling process is stopped");
    }

    @GetMapping("/counter")
    public int getCrawlCounter() {
        return crawlService.getCrawlCount();
    }

    @DeleteMapping("/counter/reset")
    public void resetCrawlCounter() {
        crawlService.resetCrawlCounter();
    }
}
