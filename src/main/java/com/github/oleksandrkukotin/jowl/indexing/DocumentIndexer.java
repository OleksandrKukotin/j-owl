package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocMethod;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentIndexer {

    private final IndexWriter indexWriter;

    @Value("${lucene.index.max.term.length}")
    private int maxTermLength;

    @PostConstruct
    private void validateMaxTermLength() {
        if (maxTermLength <= 0) {
            throw new IllegalArgumentException("The lucene.index.max.term.length property must be positive. " +
                    "Current value:" + maxTermLength);
        }
    }

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

    private Document createClassDocument(JavadocCrawledPage page) {
        Document doc = new Document();
        doc.add(new StringField("classUrl", page.url(), Field.Store.YES));
        doc.add(new TextField("className", page.className(), Field.Store.YES));

        String content = page.classDescription();
        if (content.length() > maxTermLength) {
            List<String> chunks = splitText(content);
            for (String chunk : chunks) {
                doc.add(new TextField("classDescription", chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField("classDescription", content, Field.Store.YES));
        }
        return doc;
    }

    private Document createMethodDocument(String url, JavadocMethod method) {
        Document doc = new Document();
        doc.add(new StringField("parentClassUrl", url, Field.Store.YES));
        doc.add(new StringField("methodName", method.name(), Field.Store.YES));
        doc.add(new StringField("modifiers", method.modifiers(), Field.Store.YES));
        doc.add(new StringField("returnType", method.returnType(), Field.Store.YES));
        doc.add(new TextField("methodSignature", method.signature(), Field.Store.YES));

        String methodDescription = method.description();
        if (methodDescription.length() > maxTermLength) {
            for (String chunk : splitText(methodDescription)) {
                doc.add(new TextField("description", chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField("description", methodDescription, Field.Store.YES));
        }
        return doc;
    }

    private List<String> splitText(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (!currentPart.isEmpty()) {
                if (currentPart.length() + 1 + word.length() > maxTermLength) {
                    parts.add(currentPart.toString());
                    currentPart = new StringBuilder(word);
                } else {
                    currentPart.append(" ").append(word);
                }
            } else {
                if (word.length() > maxTermLength) {
                    int index = 0;
                    while (index < word.length()) {
                        int end = Math.min(index + maxTermLength, word.length());
                        parts.add(word.substring(index, end));
                        index += maxTermLength;
                    }
                } else {
                    currentPart.append(word);
                }
            }
        }
        if (!currentPart.isEmpty()) {
            parts.add(currentPart.toString());
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
