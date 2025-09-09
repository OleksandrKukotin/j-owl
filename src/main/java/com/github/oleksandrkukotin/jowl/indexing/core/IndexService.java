package com.github.oleksandrkukotin.jowl.indexing.core;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.exception.IndexingException;
import com.github.oleksandrkukotin.jowl.indexing.search.DocumentSearcher;
import com.github.oleksandrkukotin.jowl.indexing.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {

    private static final Logger logger = LoggerFactory.getLogger(IndexService.class);

    private final DocumentIndexer indexer;
    private final DocumentSearcher searcher;

    public IndexService(DocumentIndexer indexer, DocumentSearcher searcher) {
        this.indexer = indexer;
        this.searcher = searcher;
    }

    public void indexDocument(JavadocCrawledPage page) {
        try {
            logger.info("Starting to index document: {} with {} methods", 
                       page.className(), page.methods().size());
            long startTime = System.currentTimeMillis();
            
            indexer.index(page);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully indexed document: {} in {}ms", page.className(), duration);
        } catch (IOException e) {
            logger.error("Failed to index document: {} - {}", page.className(), e.getMessage(), e);
            throw new IndexingException("Failed to index document: " + page.className(), e);
        }
    }

    public List<SearchResult> search(String query, int maxResults) {
        try {
            logger.info("Executing search query: '{}' with max results: {}", query, maxResults);
            long startTime = System.currentTimeMillis();
            
            List<SearchResult> results = searcher.search(query, maxResults);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Search completed in {}ms. Found {} results for query: '{}'", 
                       duration, results.size(), query);
            return results;
        } catch (Exception e) {
            logger.error("Error during search with query '{}': {}", query, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void commitIndex() {
        try {
            logger.info("Starting index commit operation");
            long startTime = System.currentTimeMillis();
            
            indexer.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Index commit completed successfully in {}ms", duration);
        } catch (IOException e) {
            logger.error("Failed to commit index: {}", e.getMessage(), e);
            throw new IndexingException("Failed to commit index", e);
        }
    }

    public void resetIndex() {
        try {
            logger.info("Starting index reset operation");
            long startTime = System.currentTimeMillis();
            
            indexer.clearIndex();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Index reset completed successfully in {}ms", duration);
        } catch (IOException e) {
            logger.error("Failed to reset index: {}", e.getMessage(), e);
            throw new IndexingException("Failed to reset index", e);
        }
    }
}
