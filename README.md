# JOwl - Developer Docs & API Search Engine 🦉

**Tired of digging through SEO spam just to find the official docs?**  
JOwl is a lightweight, Lucene-powered search engine built to index and retrieve developer documentation faster and cleaner than traditional search engines.

It’s a personal project — but polished enough to showcase full-stack skills: backend, frontend, crawling, indexing, and deployment. 🚀

---

## ✨ Features (MVP)
✔ **Web Crawler** – Automatically fetches developer docs & API references  
✔ **Lucene-Powered Search** – Fast, relevance-based querying  
✔ **REST API** – Simple JSON interface for search results  
✔ **Angular UI** – Clean and functional web interface  
✔ **Dockerized** – Easy to run in one command

---

## 🛠 Tech Stack
- **Java 21** – Core language
- **Spring Boot** – REST backend
- **Lucene** – Full-text search & indexing
- **jsoup** – HTML parsing & crawling
- **Gradle** – Build automation
- **Docker** – Containerization
- **Angular** – Frontend

---

## 🎯 Roadmap
### ✅ Phase 1: Core MVP (Done!)
- [x] Crawl & index developer docs (title + content)
- [x] Expose REST API for search queries
- [x] Basic Angular UI with results display
- [ ] Docker setup for easy deployment

### 🔜 Phase 2: Nice-to-Haves (Future Work)
- 🔹 Autocomplete & fuzzy search
- 🔹 Code snippet extraction
- 🔹 Smarter ranking (ML/AI relevance scoring)
- 🔹 Dark-mode UI 🌙

### 🚀 Phase 3: Expansion Ideas
- Index GitHub Wiki & README files
- Automatic monitoring & updating of indexed sources
- Support for Swagger / OpenAPI / Postman collections

---

## 🔧 Setup & Usage
```bash
# Clone the repo
git clone git@github.com:OleksandrKukotin/j-owl.git
cd j-owl

# Build & run with Docker
docker-compose up --build

# App will be available at:
# Backend: http://localhost:8080
# Frontend: http://localhost:4200
```