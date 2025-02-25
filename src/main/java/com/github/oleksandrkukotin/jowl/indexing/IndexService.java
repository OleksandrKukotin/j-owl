package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.crawler.CrawledPage;
import com.github.oleksandrkukotin.jowl.exception.IndexingException;
import org.apache.lucene.document.Document;
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

    public void indexDocument(CrawledPage page) {
        try {
            indexer.index(page);
        } catch (IOException e) {
            throw new IndexingException(e.getMessage());
        }
    }

    public List<Document> search(String query, int maxResults) {
        try {
            return searcher.search(query, maxResults);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }
}
