
package com.parallelhashing.model;

// Stores the results of a benchmark run
public record BenchmarkResult(
        int threadCount,
        long operations,
        double averageTimeMillis
) {
}
