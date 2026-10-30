package com.github.oleksandrkukotin.jowl.indexing.search;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocMethod;
import com.github.oleksandrkukotin.jowl.indexing.core.DocumentIndexer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentSearcherTest {

    private static final String LIST_URL = "https://docs.example.com/api/java/util/List.html";
    private static final String MAP_URL = "https://docs.example.com/api/java/util/Map.html";

    private IndexWriter indexWriter;
    private SearcherManager searcherManager;
    private DocumentSearcher searcher;

    @BeforeEach
    void indexSampleDocuments() throws IOException {
        indexWriter = new IndexWriter(new ByteBuffersDirectory(), new IndexWriterConfig(new StandardAnalyzer()));

        // Index through the real DocumentIndexer so the test catches field drift between indexing and search.
        // maxTermLength is normally injected by Spring after construction.
        DocumentIndexer indexer = new DocumentIndexer(indexWriter);
        ReflectionTestUtils.setField(indexer, "maxTermLength", 32766);

        indexer.index(new JavadocCrawledPage(LIST_URL, "java.util.List",
                "An ordered collection, also known as a sequence.", Set.of(),
                Set.of(new JavadocMethod("size", "public", "int", "int size()",
                        "Returns the number of elements in this list."))));
        indexer.index(new JavadocCrawledPage(MAP_URL, "java.util.Map",
                "An object that maps keys to values.", Set.of(), Set.of()));
        indexer.commit();

        searcherManager = new SearcherManager(indexWriter, null);
        searcher = new DocumentSearcher(searcherManager);
    }

    @AfterEach
    void closeIndex() throws IOException {
        searcherManager.close();
        indexWriter.close();
    }

    @Test
    void findsClassByDescription() throws Exception {
        List<SearchResult> results = searcher.search("sequence", 10);

        assertThat(results).singleElement()
                .isInstanceOfSatisfying(ClassSearchResult.class, result -> {
                    assertThat(result.getName()).isEqualTo("java.util.List");
                    assertThat(result.getUrl()).isEqualTo(LIST_URL);
                    assertThat(result.getPackageName()).isEqualTo("java.util");
                    assertThat(result.getSnippet()).contains("<b>sequence</b>");
                });
    }

    @Test
    void findsMethodByDescription() throws Exception {
        List<SearchResult> results = searcher.search("elements", 10);

        assertThat(results).singleElement()
                .isInstanceOfSatisfying(MethodSearchResult.class, result -> {
                    assertThat(result.getName()).isEqualTo("size");
                    assertThat(result.getUrl()).isEqualTo(LIST_URL + "#size");
                    assertThat(result.getReturnType()).isEqualTo("int");
                    assertThat(result.getSnippet()).contains("<b>elements</b>");
                });
    }

    @Test
    void findsMethodBySignature() throws Exception {
        assertThat(searcher.search("size", 10))
                .singleElement()
                .isInstanceOf(MethodSearchResult.class);
    }

    @Test
    void returnsEmptyListWhenNothingMatches() throws Exception {
        assertThat(searcher.search("nonexistent", 10)).isEmpty();
    }

    @Test
    void limitsNumberOfResults() throws Exception {
        assertThat(searcher.search("an OR returns", 10)).hasSize(3);
        assertThat(searcher.search("an OR returns", 2)).hasSize(2);
    }

    @Test
    void throwsParseExceptionForInvalidQuery() {
        assertThatThrownBy(() -> searcher.search("size AND", 10)).isInstanceOf(ParseException.class);
    }
}
