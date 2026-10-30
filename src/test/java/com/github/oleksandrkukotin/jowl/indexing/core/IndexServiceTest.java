package com.github.oleksandrkukotin.jowl.indexing.core;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.indexing.search.DocumentSearcher;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IndexServiceTest {

    private static final JavadocCrawledPage LIST_PAGE = new JavadocCrawledPage(
            "https://docs.example.com/api/java/util/List.html", "java.util.List",
            "An ordered collection, also known as a sequence.", Set.of(), Set.of());

    private IndexWriter indexWriter;
    private SearcherManager searcherManager;
    private IndexService indexService;

    // The searcher is created before anything is indexed, as it is at application startup
    @BeforeEach
    void createService() throws IOException {
        indexWriter = new IndexWriter(new ByteBuffersDirectory(), new IndexWriterConfig(new StandardAnalyzer()));
        searcherManager = new SearcherManager(indexWriter, null);

        DocumentIndexer indexer = new DocumentIndexer(indexWriter);
        ReflectionTestUtils.setField(indexer, "maxTermLength", 32766);
        indexService = new IndexService(indexer, new DocumentSearcher(searcherManager));
    }

    @AfterEach
    void closeIndex() throws IOException {
        searcherManager.close();
        indexWriter.close();
    }

    @Test
    void searchFindsDocumentsCommittedAfterStartup() {
        assertThat(indexService.search("sequence", 10)).isEmpty();

        indexService.indexDocument(LIST_PAGE);
        indexService.commitIndex();

        assertThat(indexService.search("sequence", 10)).hasSize(1);
    }

    @Test
    void searchFindsNothingAfterReset() {
        indexService.indexDocument(LIST_PAGE);
        indexService.commitIndex();

        indexService.resetIndex();

        assertThat(indexService.search("sequence", 10)).isEmpty();
    }
}
