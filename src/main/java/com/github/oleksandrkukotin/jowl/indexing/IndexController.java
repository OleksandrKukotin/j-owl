package com.github.oleksandrkukotin.jowl.indexing;

import org.apache.lucene.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndexController {

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam("query") String query,
                                 @RequestParam(name = "maxResults", defaultValue = "10") int limit) {
        return indexService.search(query, limit);
    }

    @PostMapping("/commit")
    public ResponseEntity<String> commitIndex() {
        indexService.commitIndex();
        return ResponseEntity.ok("Index committed");
    }

    @DeleteMapping("/reset")
    public void reset() {
        indexService.resetIndex();
    }
}
