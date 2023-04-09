package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.HtmlPage;
import com.github.pawawudaf.jowl.parse.WebsiteParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
<<<<<<< HEAD
    private static final int MAX_DEPTH = 2;
=======
    private static final StopWatch stopWatch = new StopWatch();
<<<<<<< HEAD
    private static final int MAX_DEPTH = 7;
>>>>>>> 5651a41 (Completed ont of TODOs, for review another one)
=======
>>>>>>> b06d99c (Added PathVariable to /index endpoint and changed logging implementation)

    private final WebsiteParser websiteParser;
    private final IndexService indexService;

    @Autowired
    public IndexController(WebsiteParser websiteParser, IndexService indexService) {
        this.websiteParser = websiteParser;
        this.indexService = indexService;
    }

<<<<<<< HEAD
    @GetMapping("/index")
    @ResponseStatus(HttpStatus.CREATED)
    public void index(@RequestBody IndexCommand indexCommand) {
        logger.info("Indexing process started... Seed URL:" + indexCommand.getLink());
<<<<<<< HEAD
        StopWatch stopWatch = new StopWatch();
=======
>>>>>>> 5651a41 (Completed ont of TODOs, for review another one)
        try {
            stopWatch.start("Parsing");
            Map<String, HtmlPage> parsedPages = websiteParser.parse(indexCommand.getLink(), new HashMap<String, HtmlPage>(), MAX_DEPTH);
            stopWatch.stop();
            logger.info("Time of parsing: " + stopWatch.getLastTaskInfo().getTimeSeconds() + "sec");
            stopWatch.start("Indexig");
            indexService.indexDocuments(parsedPages);
            stopWatch.stop();
            logger.info("Time of indexing: " + stopWatch.getLastTaskInfo().getTimeSeconds() + " sec");
            logger.info("The indexing process successfully ended. The number of indexed documents: " + parsedPages.size());
        } catch (Exception e) {
            logger.error("An error occurred during the indexing process: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to index documents", e);
        }
=======
    @GetMapping("/index/{depth}")
    @ResponseStatus(HttpStatus.CREATED)
    public void index(@PathVariable int depth, @RequestBody IndexWriteCommand indexWriteCommand) {
        if (!stopWatch.isRunning()) stopWatch.start("Indexing");
        logger.info("Indexing process started... Seed URL: {}. Depth parameter: {}", indexWriteCommand.getLink(), depth);
        Set<String> seedUrl = Collections.singleton(indexWriteCommand.getLink());
        Map<String, ParsedHtmlPage> parsedPages = websiteParser.parse(seedUrl, new HashMap<>(), depth, new HashSet<>());
        indexService.indexDocuments(parsedPages);
        stopWatch.stop();
        logger.info("The indexing process is done. Elapsed time: {} sec.", stopWatch.getLastTaskInfo().getTimeSeconds());
>>>>>>> b06d99c (Added PathVariable to /index endpoint and changed logging implementation)
    }

    @GetMapping("/show")
    @ResponseStatus(HttpStatus.OK)
    public List<IndexDto> showIndex() {
        return indexService.getAllIndexedDocuments();
    }

    public static final class IndexingErrorException extends RuntimeException {

        public IndexingErrorException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
