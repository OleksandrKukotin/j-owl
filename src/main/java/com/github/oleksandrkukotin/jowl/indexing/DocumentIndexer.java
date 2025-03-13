package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentIndexer {

    private static final int MAX_TERM_LENGTH = 32766;

    private final IndexWriter indexWriter;

    public DocumentIndexer(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    public void index(JavadocCrawledPage page) throws IOException {
        Document doc = createLuceneDocument(page);
        indexWriter.addDocument(doc);
    }

    private Document createLuceneDocument(JavadocCrawledPage page) {
        Document doc = new Document();
        doc.add(new StringField("url", page.url(), Field.Store.YES));
        doc.add(new TextField("title", page.title(), Field.Store.YES));

        String content = page.content();
        if (content.length() > MAX_TERM_LENGTH) {
            List<String> chunks = splitText(content);
            for (String chunk : chunks) {
                doc.add(new TextField("content", chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField("content", content, Field.Store.YES));
        }
        return doc;
    }

    private List<String> splitText(String text) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < text.length(); i += MAX_TERM_LENGTH) {
            parts.add(text.substring(i, Math.min(i + MAX_TERM_LENGTH, text.length())));
        }
        return parts;
    }

    public void commit() throws IOException {
        indexWriter.commit();
    }

    public void clearIndex() throws IOException {
        indexWriter.deleteAll();
    }
}
