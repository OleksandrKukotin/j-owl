package com.github.oleksandrkukotin.jowl.indexing;

import com.github.oleksandrkukotin.jowl.crawler.CrawledPage;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DocumentIndexer {

    private final IndexWriter indexWriter;

    public DocumentIndexer(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    public void index(CrawledPage page) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("url", page.url(), Field.Store.YES));
        doc.add(new StringField("title", page.title(), Field.Store.YES));
        doc.add(new StringField("content", page.content(), Field.Store.YES));

        indexWriter.addDocument(doc);
        indexWriter.commit();
    }
}
