package com.github.oleksandrkukotin.jowl.indexing.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextSplitterTest {

    private static final int MAX_TERM_LENGTH = 10;

    private final TextSplitter splitter = new TextSplitter(MAX_TERM_LENGTH);

    @Test
    void keepsShortTextAsSinglePart() {
        assertThat(splitter.splitText("a b c")).containsExactly("a b c");
    }

    @Test
    void keepsTextOfExactlyMaxLengthAsSinglePart() {
        assertThat(splitter.splitText("aaaa bbbbb")).containsExactly("aaaa bbbbb");
    }

    @Test
    void packsWordsIntoPartsUpToMaxLength() {
        assertThat(splitter.splitText("aaaa bbbb cccc dd"))
                .containsExactly("aaaa bbbb", "cccc dd");
    }

    @Test
    void splitsWordLongerThanMaxLengthIntoChunks() {
        assertThat(splitter.splitText("abcdefghijklmnopqrstuvwxy"))
                .containsExactly("abcdefghij", "klmnopqrst", "uvwxy");
    }

    @Test
    void keepsAllContentWhenLongWordIsInTheMiddle() {
        assertThat(splitter.splitText("hi abcdefghijkl there"))
                .containsExactlyInAnyOrder("abcdefghij", "kl", "hi there");
    }

    @Test
    void collapsesWhitespaceBetweenWords() {
        assertThat(splitter.splitText("a\n\n b\t c")).containsExactly("a b c");
    }

    @Test
    void returnsNoPartsForEmptyText() {
        assertThat(splitter.splitText("")).isEmpty();
    }

    @Test
    void neverProducesPartLongerThanMaxLength() {
        String text = "Lucene rejects terms longer than the configured maximum, so every part "
                + "must fit, including supercalifragilisticexpialidocious words.";

        List<String> parts = splitter.splitText(text);

        assertThat(parts).isNotEmpty().allSatisfy(part -> assertThat(part).hasSizeLessThanOrEqualTo(MAX_TERM_LENGTH));
    }
}
