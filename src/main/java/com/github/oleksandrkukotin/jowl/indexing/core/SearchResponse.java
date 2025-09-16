package com.github.oleksandrkukotin.jowl.indexing.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.oleksandrkukotin.jowl.indexing.search.SearchResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {
    
    private final String query;
    private final List<SearchResult> results;
    private final int totalResults;
    private final int maxResults;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    
    private final Long durationMs;
    private final Map<String, Object> metadata;

    public SearchResponse(String query, List<SearchResult> results, int maxResults, Long durationMs) {
        this.query = query;
        this.results = results;
        this.totalResults = results.size();
        this.maxResults = maxResults;
        this.timestamp = LocalDateTime.now();
        this.durationMs = durationMs;
        this.metadata = null;
    }

    public SearchResponse(String query, List<SearchResult> results, int maxResults, Long durationMs, Map<String, Object> metadata) {
        this.query = query;
        this.results = results;
        this.totalResults = results.size();
        this.maxResults = maxResults;
        this.timestamp = LocalDateTime.now();
        this.durationMs = durationMs;
        this.metadata = metadata;
    }

    public String getQuery() {
        return query;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}

