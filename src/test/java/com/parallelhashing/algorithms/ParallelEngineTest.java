
package com.parallelhashing.algorithms;

import com.parallelhashing.model.SearchResult;
import org.junit.jupiter.api.Test;
import com.parallelhashing.generators.ListCandidateSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParallelEngineTest {

    private final ParallelEngine engine = new ParallelEngine();

    private final String emptyStringHash =
            "e3b0c44298fc1c149afbf4c8996fb924"
                    + "27ae41e4649b934ca495991b7852b855";

    @Test
    void shouldFindMatchingCandidate() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                "",
                "student"
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash,
                4
        );

        assertTrue(result.found());
        assertEquals("", result.matchedCandidate());
        assertTrue(result.hashesChecked() >= 1);
        assertTrue(result.hashesChecked() <= candidates.size());
    }

    @Test
    void shouldReturnNotFoundWhenNoCandidateMatches() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                "student",
                "network"
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash,
                4
        );

        assertFalse(result.found());
        assertNull(result.matchedCandidate());
        assertEquals(candidates.size(), result.hashesChecked());
    }

    @Test
    void shouldRejectInvalidTargetHash() {

        List<String> candidates = List.of("apple");

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.search(new ListCandidateSource(candidates), "12345", 2)
        );
    }

    @Test
    void shouldRejectInvalidThreadCount() {

        List<String> candidates = List.of("apple");

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.search(new ListCandidateSource(candidates), emptyStringHash, 0)
        );
    }

    @Test
    void shouldWorkWhenThreadCountExceedsCandidateCount() {

        List<String> candidates = List.of(
                "apple",
                "computer"
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash,
                8
        );

        assertFalse(result.found());
        assertEquals(2, result.hashesChecked());
    }
}
