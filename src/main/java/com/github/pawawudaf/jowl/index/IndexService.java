package com.github.pawawudaf.jowl.index;

<<<<<<< HEAD
import com.github.pawawudaf.jowl.parse.ParsedData;
import org.apache.lucene.analysis.Analyzer;
=======
import com.github.pawawudaf.jowl.parse.HtmlPage;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
<<<<<<< HEAD
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
=======
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
<<<<<<< HEAD
import org.apache.lucene.search.Query;
=======
import org.apache.lucene.search.ScoreDoc;
>>>>>>> b35e441 (Upgraded classes of index package)
import org.apache.lucene.search.TopDocs;
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
<<<<<<< HEAD
import java.nio.file.Path;
=======
import java.util.Arrays;
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)
import java.util.List;
<<<<<<< HEAD
import java.util.Set;
=======
import java.util.Map;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)

@Service
public class IndexService {

    static final String TITLE = "TITLE";
    static final String LINK = "LINK";
    static final String BODY = "BODY";
    static final int NUMBER_OF_DOCS = 10;
    private final IndexWriter indexWriter;

    public IndexService() {
        final Analyzer analyzer = new StandardAnalyzer();
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            Path indexPath = Files.createTempDirectory("tempIndex");
            Directory directory = FSDirectory.open(indexPath);
            indexWriter = new IndexWriter(directory, config);
        } catch (IOException e) {
            throw new TempDirectoryCreationException("Error during creating temp directory", e);
        }
    }

    public void indexDocuments(Map<String, HtmlPage> data) {
        try {
            indexWriter.addDocuments(createDocuments(data));
            indexWriter.commit();
        } catch (IOException e) {
            throw new IndexingException("Error indexing documents", e);
        }
    }

<<<<<<< HEAD
<<<<<<< HEAD
    public String getStringOfIndexedDocuments() {
        StringBuilder stringBuilder = new StringBuilder();
        try (IndexReader indexReader = DirectoryReader.open(indexWriter)) {
            for (int docId = 0; docId < indexReader.maxDoc(); docId++) {
                Document doc = indexReader.storedFields().document(docId);
                stringBuilder.append("Document ID: ").append(docId).append("\n");
                for (IndexableField field : doc.getFields()) {
                    stringBuilder.append("Field: ").append(field.name()).append(", Value: ").append(field.stringValue()).append("\n");
                }
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
=======
    public List<IndexDto> getAllIndexedDocuments() {
        try (IndexReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new MatchAllDocsQuery();
            TopDocs topDocs = searcher.search(query, 10);

            return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        Document doc = reader.document(scoreDoc.doc);
                        return new IndexDto(
                            doc.get("TITLE"),
                            doc.get("LINK"),
                            topDocs
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
=======
    private IndexDto getDocumentFromScoreDoc(ScoreDoc scoreDoc, IndexReader reader) {
        try {
            Document doc = reader.storedFields().document(scoreDoc.doc);
            return new IndexDto(
                doc.get(TITLE),
                doc.get(LINK),
                doc.get(BODY)
            );
        } catch (IOException e) {
            throw new GettingIndexException("", e);
        }
    }

    public List<IndexDto> getAllIndexedDocuments() {
        try (IndexReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), NUMBER_OF_DOCS);

            return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> getDocumentFromScoreDoc(scoreDoc, reader))
>>>>>>> b35e441 (Upgraded classes of index package)
                .toList();
>>>>>>> 10abe91 (Added validator and changed parse() method in WebsiteParser, created IndexDto and according method in IndexService, IndexController moved from Controller to RestController)
        } catch (IOException e) {
            throw new IndexReaderException("Error during reading index documents", e);
        }
    }

    private List<Document> createDocuments(Map<String, HtmlPage> data) {
        return data.entrySet().stream()
            .map(entry -> {
                Document document = new Document();
<<<<<<< HEAD
<<<<<<< HEAD
                document.add(new TextField("key", entry.getKey(), Field.Store.YES));
                document.add(new TextField("value", entry.getValue(), Field.Store.YES));
=======
                document.add(new TextField("LINK", entry.getKey(), Field.Store.YES));
                document.add(new TextField("TITLE", entry.getValue().getTitle(), Field.Store.YES));
                document.add(new TextField("BODY", entry.getValue().getBody().text(), Field.Store.YES));
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
=======
                document.add(new TextField(LINK, entry.getKey(), Field.Store.YES));
                document.add(new TextField(TITLE, entry.getValue().getTitle(), Field.Store.YES));
                document.add(new TextField(BODY, entry.getValue().getBody().text(), Field.Store.YES));
>>>>>>> b35e441 (Upgraded classes of index package)
                return document;
            })
            .toList();
    }

    private static final class GettingIndexException extends RuntimeException {

        public GettingIndexException(String message, Exception exception) {
            super(message, exception);
        }
    }

    private static final class TempDirectoryCreationException extends RuntimeException {

        public TempDirectoryCreationException(String message, Exception exception) {
            super(message, exception);
        }
    }

    private static final class IndexingException extends RuntimeException {

        public IndexingException(String message, Exception exception) {
            super(message, exception);
        }
    }

    public class IndexReaderException extends RuntimeException {

        public IndexReaderException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
