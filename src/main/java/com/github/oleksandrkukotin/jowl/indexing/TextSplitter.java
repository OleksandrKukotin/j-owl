package com.github.oleksandrkukotin.jowl.indexing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextSplitter {

    private final int maxTermLength;

    public TextSplitter(@Value("${lucene.index.max.term.length}") int maxTermLength) {
        this.maxTermLength = maxTermLength;
    }

    public List<String> splitText(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();

        for (String word : text.split("\\s+")) {
            if (word.length() > maxTermLength) {
                splitLongWord(parts, word);
                continue;
            }
            addWordToCurrentPart(parts, currentPart, word);
        }

        flushRemainingPart(parts, currentPart);
        return parts;
    }

    private void splitLongWord(List<String> parts, String word) {
        int index = 0;
        while (index < word.length()) {
            int end = Math.min(index + maxTermLength, word.length());
            parts.add(word.substring(index, end));
            index += maxTermLength;
        }
    }

    private void addWordToCurrentPart(List<String> parts, StringBuilder currentPart, String word) {
        if (!currentPart.isEmpty() && currentPart.length() + 1 + word.length() > maxTermLength) {
            parts.add(currentPart.toString());
            currentPart.setLength(0);
        }
        if (!currentPart.isEmpty()) {
            currentPart.append(" ");
        }
        currentPart.append(word);
    }

    private void flushRemainingPart(List<String> parts, StringBuilder currentPart) {
        if (!currentPart.isEmpty()) {
            parts.add(currentPart.toString());
        }
    }
}
