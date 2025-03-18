package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocMethod;
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
        Document doc = createClassDocument(page);
        indexWriter.addDocument(doc);

        for (JavadocMethod method : page.methods()) {
            Document methodDoc = createMethodDocument(page.url(), method);
            indexWriter.addDocument(methodDoc);
        }
    }

    private Document createMethodDocument(String url, JavadocMethod method) {
        Document doc = new Document();
        doc.add(new StringField("classUrl", url, Field.Store.YES));
        doc.add(new StringField("methodName", method.name(), Field.Store.YES));
        doc.add(new StringField("modifiers", method.modifiers(), Field.Store.YES));
        doc.add(new StringField("returnType", method.returnType(), Field.Store.YES));
        doc.add(new TextField("fullSignature", method.signature(), Field.Store.YES));

        String methodDescription = method.description();
        if (methodDescription.length() > MAX_TERM_LENGTH) {
            for (String chunk : splitText(methodDescription)) {
                doc.add(new TextField("chunk", chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField("description", methodDescription, Field.Store.YES));
        }
        return doc;
    }

    private Document createClassDocument(JavadocCrawledPage page) {
        Document doc = new Document();
        doc.add(new StringField("url", page.url(), Field.Store.YES));
        doc.add(new TextField("className", page.className(), Field.Store.YES));

        String content = page.classDescription();
        if (content.length() > MAX_TERM_LENGTH) {
            List<String> chunks = splitText(content);
            for (String chunk : chunks) {
                doc.add(new TextField("classDescription", chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField("classDescription", content, Field.Store.YES));
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
