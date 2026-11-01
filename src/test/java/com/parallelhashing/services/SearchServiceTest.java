
package com.parallelhashing.services;

import com.parallelhashing.generators.ListCandidateSource;
import com.parallelhashing.model.ExecutionMode;
import com.parallelhashing.model.SearchResult;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private final SearchService searchService =
            new SearchService();

    private final String emptyStringHash =
            "e3b0c44298fc1c149afbf4c8996fb924"
                    + "27ae41e4649b934ca495991b7852b855";

    @Test
    void shouldExecuteSequentialSearch() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                ""
        );

        SearchResult result = searchService.search(
                new ListCandidateSource(candidates),
                emptyStringHash,
                ExecutionMode.SEQUENTIAL,
                1
        );

        assertTrue(result.found());
        assertEquals("", result.matchedCandidate());
        assertEquals(3, result.hashesChecked());
    }

    @Test
    void shouldExecuteParallelSearch() {

        List<String> candidates = List.of(
                "apple",
                "computer",
                ""
        );

        SearchResult result = searchService.search(
                new ListCandidateSource(candidates),
                emptyStringHash,
                ExecutionMode.PARALLEL,
                2
        );

        assertTrue(result.found());
        assertEquals("", result.matchedCandidate());
        assertTrue(result.hashesChecked() >= 1);
        assertTrue(result.hashesChecked() <= candidates.size());
    }
}
