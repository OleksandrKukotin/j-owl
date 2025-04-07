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
import java.util.List;

import static com.github.oleksandrkukotin.jowl.indexing.LuceneFields.*;

@Component
public class DocumentIndexer {

    private final IndexWriter indexWriter;
    private final TextSplitter textSplitter;

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
        this.textSplitter = new TextSplitter(maxTermLength);
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
        doc.add(new StringField(CLASS_URL, page.url(), Field.Store.YES));
        doc.add(new TextField(CLASS_NAME, page.className(), Field.Store.YES));

        String content = page.classDescription();
        if (content.length() > maxTermLength) {
            List<String> chunks = textSplitter.splitText(content);
            for (String chunk : chunks) {
                doc.add(new TextField(CLASS_DESCRIPTION, chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField(CLASS_DESCRIPTION, content, Field.Store.YES));
        }
        return doc;
    }

    private Document createMethodDocument(String url, JavadocMethod method) {
        Document doc = new Document();
        doc.add(new StringField(CLASS_URL, url, Field.Store.YES));
        doc.add(new StringField(METHOD_NAME, method.name(), Field.Store.YES));
        doc.add(new StringField(METHOD_MODIFIERS, method.modifiers(), Field.Store.YES));
        doc.add(new StringField(METHOD_RETURN_TYPE, method.returnType(), Field.Store.YES));
        doc.add(new TextField(METHOD_SIGNATURE, method.signature(), Field.Store.YES));

        String methodDescription = method.description();
        if (methodDescription.length() > maxTermLength) {
            for (String chunk : textSplitter.splitText(methodDescription)) {
                doc.add(new TextField(METHOD_DESCRIPTION, chunk, Field.Store.YES));
            }
        } else {
            doc.add(new TextField(METHOD_DESCRIPTION, methodDescription, Field.Store.YES));
        }
        return doc;
    }

    public void commit() throws IOException {
        indexWriter.commit();
    }

    public void clearIndex() throws IOException {
        indexWriter.deleteAll();
    }
}
