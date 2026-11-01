
package com.parallelhashing.algorithms;

import com.parallelhashing.generators.CandidateSource;
import com.parallelhashing.hashing.HashUtils;
import com.parallelhashing.model.SearchResult;

import java.util.Objects;

public class SequentialEngine {

    // Search candidates one by one using a single thread
    public SearchResult search(
            CandidateSource source,
            String targetHash
    ) {

        Objects.requireNonNull(source, "Candidate source cannot be null");

        if (!HashUtils.isValidSha256Hash(targetHash)) {
            throw new IllegalArgumentException(
                    "The target must be a valid SHA-256 hash."
            );
        }

        long startTime = System.nanoTime();
        long hashesChecked = 0;

        for (long i = 0; i < source.size(); i++) {

            String candidate = Objects.requireNonNull(
                    source.candidateAt(i),
                    "Candidate cannot be null"
            );

            hashesChecked++;

            if (HashUtils.matches(candidate, targetHash)) {

                long elapsedTime = System.nanoTime() - startTime;

                return new SearchResult(
                        true,
                        candidate,
                        hashesChecked,
                        elapsedTime
                );
            }
        }

        long elapsedTime = System.nanoTime() - startTime;

        return new SearchResult(
                false,
                null,
                hashesChecked,
                elapsedTime
        );
    }
}
