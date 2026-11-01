
package com.parallelhashing.model;

// Stores the result and performance information of a search operation
public record SearchResult(
        boolean found,
        String matchedCandidate,
        long hashesChecked,
        long elapsedNanos
) {

    // Convert elapsed time from nanoseconds to milliseconds
    public double executionTimeMillis() {
        return elapsedNanos / 1_000_000.0;
    }
}
