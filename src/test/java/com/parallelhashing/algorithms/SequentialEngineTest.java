
package com.parallelhashing.algorithms;

import com.parallelhashing.model.SearchResult;
import org.junit.jupiter.api.Test;
import com.parallelhashing.generators.ListCandidateSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequentialEngineTest {

    private final SequentialEngine engine = new SequentialEngine();

    private final String emptyStringHash =
            "e3b0c44298fc1c149afbf4c8996fb924"
                    + "27ae41e4649b934ca495991b7852b855";

    @Test
    void shouldFindMatchingCandidate() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                ""
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash
        );

        assertTrue(result.found());
        assertEquals("", result.matchedCandidate());
        assertEquals(3, result.hashesChecked());
    }

    @Test
    void shouldReturnNotFoundWhenNoCandidateMatches() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                "student"
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash
        );

        assertFalse(result.found());
        assertNull(result.matchedCandidate());
        assertEquals(3, result.hashesChecked());
    }

    @Test
    void shouldRejectInvalidTargetHash() {

        List<String> candidates = List.of("apple");

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.search(new ListCandidateSource(candidates), "12345")
        );
    }

    @Test
    void shouldStopAfterFindingTheFirstMatch() {

        List<String> candidates = List.of(
                "",
                "apple",
                "computer"
        );

        SearchResult result = engine.search(
                new ListCandidateSource(candidates),
                emptyStringHash
        );

        assertTrue(result.found());
        assertEquals(1, result.hashesChecked());
    }
}
