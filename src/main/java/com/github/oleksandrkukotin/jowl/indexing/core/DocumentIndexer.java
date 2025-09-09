package com.github.oleksandrkukotin.jowl.indexing.core;

import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocCrawledPage;
import com.github.oleksandrkukotin.jowl.crawler.javadoc.JavadocMethod;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.github.oleksandrkukotin.jowl.indexing.config.LuceneFields.*;

@Component
public class DocumentIndexer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentIndexer.class);

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
        logger.debug("Creating index documents for class: {} with {} methods", 
                    page.className(), page.methods().size());
        
        // Index the class document
        Document doc = createClassDocument(page);
        indexWriter.addDocument(doc);
        logger.debug("Added class document for: {}", page.className());

        // Index method documents
        int methodCount = 0;
        for (JavadocMethod method : page.methods()) {
            Document methodDoc = createMethodDocument(page.url(), method);
            indexWriter.addDocument(methodDoc);
            methodCount++;
            
            if (methodCount % 10 == 0) {
                logger.debug("Indexed {} methods for class: {}", methodCount, page.className());
            }
        }
        
        logger.debug("Completed indexing for class: {} - {} methods indexed", 
                    page.className(), methodCount);
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
        logger.debug("Committing index changes to disk");
        indexWriter.commit();
        logger.debug("Index commit completed");
    }

    public void clearIndex() throws IOException {
        logger.debug("Clearing all documents from index");
        indexWriter.deleteAll();
        logger.debug("Index cleared successfully");
    }
}
