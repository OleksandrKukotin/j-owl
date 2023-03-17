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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
<<<<<<< HEAD
import java.util.Set;
=======
import java.util.Map;
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)

@Service
public class IndexService {

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
        } catch (IOException e) {
            throw new IndexReaderException("Error creating IndexReader", e);
        }
    }

    private List<Document> createDocuments(Map<String, HtmlPage> data) {
        return data.entrySet().stream()
            .map(entry -> {
                Document document = new Document();
<<<<<<< HEAD
                document.add(new TextField("key", entry.getKey(), Field.Store.YES));
                document.add(new TextField("value", entry.getValue(), Field.Store.YES));
=======
                document.add(new TextField("LINK", entry.getKey(), Field.Store.YES));
                document.add(new TextField("TITLE", entry.getValue().getTitle(), Field.Store.YES));
                document.add(new TextField("BODY", entry.getValue().getBody().text(), Field.Store.YES));
>>>>>>> a14c866 (Added logger and made multiply changes according to TODOs)
                return document;
            })
            .toList();
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
