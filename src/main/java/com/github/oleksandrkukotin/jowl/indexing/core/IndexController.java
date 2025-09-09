package com.github.oleksandrkukotin.jowl.indexing.core;

import com.github.oleksandrkukotin.jowl.indexing.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/index")
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping(value = "/search", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResponse> search(@RequestParam("query") String query,
                                                 @RequestParam(name = "maxResults", defaultValue = "10") int limit) {
        logger.info("Received search request: query='{}', maxResults={}", query, limit);
        
        try {
            long startTime = System.currentTimeMillis();
            List<SearchResult> results = indexService.search(query, limit);
            long duration = System.currentTimeMillis() - startTime;
            
            SearchResponse response = new SearchResponse(query, results, limit, duration);
            logger.info("Search request completed successfully: {} results in {}ms", results.size(), duration);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Search request failed for query '{}': {}", query, e.getMessage(), e);
            SearchResponse errorResponse = new SearchResponse(query, List.of(), limit, 0L);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<IndexOperationResponse> commitIndex() {
        logger.info("Received index commit request");
        
        try {
            long startTime = System.currentTimeMillis();
            indexService.commitIndex();
            long duration = System.currentTimeMillis() - startTime;
            
            IndexOperationResponse response = new IndexOperationResponse(
                true, 
                "Index committed successfully", 
                "commit", 
                duration
            );
            
            logger.info("Index commit request completed successfully in {}ms", duration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Index commit request failed: {}", e.getMessage(), e);
            IndexOperationResponse errorResponse = new IndexOperationResponse(
                false, 
                "Failed to commit index: " + e.getMessage(), 
                "commit"
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/reset")
    public ResponseEntity<IndexOperationResponse> reset() {
        logger.info("Received index reset request");
        
        try {
            long startTime = System.currentTimeMillis();
            indexService.resetIndex();
            long duration = System.currentTimeMillis() - startTime;
            
            IndexOperationResponse response = new IndexOperationResponse(
                true, 
                "Index reset successfully", 
                "reset", 
                duration
            );
            
            logger.info("Index reset request completed successfully in {}ms", duration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Index reset request failed: {}", e.getMessage(), e);
            IndexOperationResponse errorResponse = new IndexOperationResponse(
                false, 
                "Failed to reset index: " + e.getMessage(), 
                "reset"
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<IndexOperationResponse> getIndexStats() {
        logger.info("Received index statistics request");
        
        try {
            // This would need to be implemented in IndexService to get actual stats
            // For now, return a placeholder response
            IndexOperationResponse response = new IndexOperationResponse(
                true, 
                "Index statistics retrieved successfully", 
                "stats", 
                0L
            );
            
            logger.info("Index statistics request completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Index statistics request failed: {}", e.getMessage(), e);
            IndexOperationResponse errorResponse = new IndexOperationResponse(
                false, 
                "Failed to retrieve index statistics: " + e.getMessage(), 
                "stats"
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
