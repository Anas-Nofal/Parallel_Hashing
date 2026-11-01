
package com.parallelhashing.services;

import com.parallelhashing.algorithms.ParallelEngine;
import com.parallelhashing.algorithms.SequentialEngine;
import com.parallelhashing.generators.CandidateSource;
import com.parallelhashing.model.ExecutionMode;
import com.parallelhashing.model.SearchResult;

import java.util.Objects;

public class SearchService {

    private final SequentialEngine sequentialEngine;
    private final ParallelEngine parallelEngine;

    public SearchService() {
        this.sequentialEngine = new SequentialEngine();
        this.parallelEngine = new ParallelEngine();
    }

    // Execute the search using the selected mode
    public SearchResult search(
            CandidateSource source,
            String targetHash,
            ExecutionMode executionMode,
            int threadCount
    ) {

        Objects.requireNonNull(source);
        Objects.requireNonNull(executionMode);

        if (executionMode == ExecutionMode.SEQUENTIAL) {
            return sequentialEngine.search(source, targetHash);
        }

        return parallelEngine.search(source, targetHash, threadCount);
    }
}
