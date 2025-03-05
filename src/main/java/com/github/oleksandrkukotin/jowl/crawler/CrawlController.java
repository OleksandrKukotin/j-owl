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
    public ResponseEntity<String> crawl(@RequestParam("url") String url, @PathVariable("depth") int depth) {
        crawlService.submitCrawlTask(url, depth);
        return ResponseEntity.ok("Crawling started, you can check number of crawled pages by /counter endpoint" +
                "or stop the process by passing /crawl/stop endpoint");
    }

    @PostMapping("/crawl/stop")
    public ResponseEntity<String> stop() {
        crawlService.stopCrawl();
        return ResponseEntity.ok("Crawling process is stopped");
    }

    @GetMapping("/crawl/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(crawlService.getCrawlerStatus());
    }

    @GetMapping("/counter")
    public ResponseEntity<String> getCrawlCounter() {
        String output = String.format("%d pages crawled, need to commit index after stop", crawlService.getCrawlCount());
        return ResponseEntity.ok(output);
    }
}
