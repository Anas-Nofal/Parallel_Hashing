
package com.parallelhashing.generators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryGeneratorTest {

    private final DictionaryGenerator generator =
            new DictionaryGenerator();

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldLoadDictionaryCorrectly() throws IOException {

        Path dictionaryFile =
                temporaryDirectory.resolve("dictionary.txt");

        Files.writeString(
                dictionaryFile,
                "apple\nhello\ncomputer\n"
        );

        List<String> candidates =
                generator.loadDictionary(dictionaryFile);

        assertEquals(
                List.of("apple", "hello", "computer"),
                candidates
        );
    }

    @Test
    void shouldRemoveDuplicateCandidates() throws IOException {

        Path dictionaryFile =
                temporaryDirectory.resolve("duplicates.txt");

        Files.writeString(
                dictionaryFile,
                "apple\nhello\napple\n"
        );

        List<String> candidates =
                generator.loadDictionary(dictionaryFile);

        assertEquals(
                List.of("apple", "hello"),
                candidates
        );
    }

    @Test
    void shouldIgnoreEmptyLinesAndComments() throws IOException {

        Path dictionaryFile =
                temporaryDirectory.resolve("comments.txt");

        Files.writeString(
                dictionaryFile,
                "# Test dictionary\n"
                        + "apple\n"
                        + "\n"
                        + "hello\n"
                        + "   \n"
        );

        List<String> candidates =
                generator.loadDictionary(dictionaryFile);

        assertEquals(
                List.of("apple", "hello"),
                candidates
        );
    }

    @Test
    void shouldRejectMissingFile() {

        Path missingFile =
                temporaryDirectory.resolve("missing.txt");

        assertThrows(
                IOException.class,
                () -> generator.loadDictionary(missingFile)
        );
    }

    @Test
    void shouldReturnAnUnmodifiableList() throws IOException {

        Path dictionaryFile =
                temporaryDirectory.resolve("immutable.txt");

        Files.writeString(dictionaryFile, "apple\nhello\n");

        List<String> candidates =
                generator.loadDictionary(dictionaryFile);

        assertThrows(
                UnsupportedOperationException.class,
                () -> candidates.add("computer")
        );
    }
}
