package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class LuceneConfig {

    @Value("${lucene.index.dir}")
    private String indexDir;

    @Bean
    public FSDirectory luceneDirectory() throws IOException {
        return FSDirectory.open(Paths.get(indexDir));
    }

    @Bean
    public IndexWriter indexWriter(FSDirectory directory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(directory, config);
    }

    @Bean
    public IndexSearcher indexSearcher(FSDirectory directory) throws IOException {
        return new IndexSearcher(DirectoryReader.open(directory));
    }

    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }
}
