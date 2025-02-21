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
        return "Crawl finished :]";
    }
}
