# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

J-Owl is a Lucene-powered developer documentation search engine that indexes and retrieves Javadoc. The pipeline is: crawl Javadoc HTML → parse → index into Lucene → serve via REST → display in Angular UI.

## Commands

### Backend (Java / Gradle)

```bash
./gradlew clean build    # Full build with tests
./gradlew bootRun        # Run Spring Boot app (port 8080)
./gradlew test           # Run tests
./gradlew bootJar        # Build executable JAR
```

### Frontend (Angular — run from `j-owl-ui/`)

```bash
npm install              # Install dependencies
npm start                # Dev server on port 4200
npm run build            # Production build to dist/j-owl-ui
npm test                 # Run Karma tests
```

### Docker (full stack)

```bash
docker-compose up --build    # Build and start backend (:8080) + frontend (:4200)
```

## Architecture

Three-layer stack:

```
Angular UI (port 4200, Nginx)
       ↓  HTTP  (Nginx proxies /index/* to backend)
Spring Boot REST API (port 8080)
       ↓
Lucene index (file system: lucene_index/)
```

### Backend modules (`src/main/java/com/github/oleksandrkukotin/jowl/`)

| Package | Responsibility |
|---|---|
| `crawler/` | Web crawling — `CrawlService` runs a fixed thread pool (default 6), uses `ConcurrentHashMap` for visited-URL dedup, depth-limited recursion |
| `crawler/javadoc/` | jsoup-based Javadoc-specific crawler and parser; produces `JavadocCrawledPage` with class + method metadata |
| `indexing/core/` | `IndexService` → `DocumentIndexer` creates Lucene documents; `TextSplitter` guards the 32,766-char term limit |
| `indexing/search/` | `DocumentSearcher` uses `MultiFieldQueryParser` over `classDescription`, `methodSignature`, `methodDescription`; returns highlighted HTML snippets via `QueryHighlighter`; `SearchResultFactory` produces `ClassSearchResult` or `MethodSearchResult` |
| `indexing/config/` | Spring beans for Lucene (`IndexWriter`, `DirectoryReader`); field name constants in `LuceneFields` |
| `exception/` | `GlobalExceptionHandler` + typed exceptions for indexing/search errors |

### Crawl → Index → Search data flow

1. `POST /crawler/crawl/{depth}?url=X` triggers `CrawlService` (async)
2. `JavadocWebCrawler` fetches pages; `JavadocPageParser` extracts class/method data
3. `IndexService.indexDocument()` → `DocumentIndexer` writes one Lucene document per class and per method
4. `POST /index/commit` flushes changes to disk
5. `GET /index/search?query=X&maxResults=N` → `DocumentSearcher` → `SearchResponse` JSON
6. Angular `SearchComponent` calls `SearchService` (HTTP), renders results

### Lucene document fields (`LuceneFields.java`)

- **Class docs**: `classUrl`, `className`, `classDescription`, `packageName`, `shortDescription`, `snippet`
- **Method docs**: adds `methodName`, `methodModifiers`, `returnType`, `methodSignature`, `methodDescription`

### Frontend (`j-owl-ui/src/app/`)

Single `SearchComponent` with `SearchService` for HTTP. API base URL is configured in `src/environments/environment.ts`. Result type is detected at runtime by checking for method-specific fields (`isMethodResult()`).

## Key Configuration

`src/main/resources/application.properties`:
- `crawler.concurrent.thread.pool.size` — crawler parallelism
- `crawler.retries.max` — HTTP retry count
- `lucene.index.dir` — where the Lucene index is stored on disk
- `lucene.index.max.term.length` — must stay ≤ 32766 (Lucene hard limit)

## REST API Reference

Import `api/J-Owl-Postman.json` or `api/J-Owl-Bruno.json` for a full collection. Key endpoints:

| Method | Path | Purpose |
|---|---|---|
| GET | `/index/search?query=X&maxResults=N` | Search |
| POST | `/index/commit` | Flush index to disk |
| DELETE | `/index/reset` | Clear all indexed documents |
| POST | `/crawler/crawl/{depth}?url=X` | Start crawl |
| POST | `/crawler/crawl/stop` | Stop crawl |
| GET | `/crawler/crawl/status` | Crawl running? |
| GET | `/crawler/counter` | Pages crawled count |