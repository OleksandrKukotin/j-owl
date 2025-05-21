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
            indexer.index(page);
        } catch (IOException e) {
            throw new IndexingException(e.getMessage(), e);
        }
    }

    public List<SearchResult> search(String query, int maxResults) {
        try {
            return searcher.search(query, maxResults);
        } catch (Exception e) {
            logger.error("Error during search with query {}", query, e);
            return new ArrayList<>();
        }
    }

    public void commitIndex() {
        try {
            indexer.commit();
            logger.info("Index committed");
        } catch (IOException e) {
            throw new IndexingException(e.getMessage(), e);
        }
    }

    public void resetIndex() {
        try {
            indexer.clearIndex();
            logger.info("Index reset");
        } catch (IOException e) {
            throw new IndexingException(e.getMessage(), e);
        }
    }
}
