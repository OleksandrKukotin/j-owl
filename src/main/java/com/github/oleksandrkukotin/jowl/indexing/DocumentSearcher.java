package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentSearcher {

    private final IndexSearcher indexSearcher;
    private final QueryParser queryParser;

    public DocumentSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
        this.queryParser = new QueryParser("content", new StandardAnalyzer());
    }

    public List<String> search(String queryString, int maxResults) throws ParseException, IOException {
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = indexSearcher.search(query, maxResults);

        List<String> results = new ArrayList<String>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = ;
            results.add(doc.);
            //TODO: finish the method
        }
    }
}
