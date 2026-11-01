
package com.parallelhashing.services;

import com.parallelhashing.hashing.HashUtils;
import com.parallelhashing.model.BenchmarkResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BenchmarkService {

    private static final int REPETITIONS = 3;

    // Measure sequential hashing performance
    public BenchmarkResult benchmarkSequential(
            long operations,
            String targetHash
    ) {

        validateInput(operations, targetHash);

        double totalTime = 0;

        for (int round = 0; round < REPETITIONS; round++) {

            long start = System.nanoTime();

            for (long i = 0; i < operations; i++) {

                String candidate = "benchmark-" + i;

                HashUtils.matches(candidate, targetHash);
            }

            long elapsed = System.nanoTime() - start;

            totalTime += elapsed / 1_000_000.0;
        }

        return new BenchmarkResult(
                1,
                operations,
                totalTime / REPETITIONS
        );
    }

    // Measure parallel hashing performance
    public BenchmarkResult benchmarkParallel(
            long operations,
            int threadCount,
            String targetHash
    ) {

        validateInput(operations, targetHash);

        if (threadCount < 1 || threadCount > 64) {
            throw new IllegalArgumentException(
                    "Thread count must be between 1 and 64."
            );
        }

        double totalTime = 0;

        for (int round = 0; round < REPETITIONS; round++) {

            ExecutorService executor =
                    Executors.newFixedThreadPool(threadCount);

            List<Future<Long>> futures = new ArrayList<>();

            // Start timing after creating the thread pool
            long start = System.nanoTime();

            try {

                for (int i = 0; i < threadCount; i++) {

                    final long from =
                            (long) i * operations / threadCount;

                    final long to =
                            (long) (i + 1) * operations / threadCount;

                    Future<Long> future = executor.submit(() -> {

                        long localCount = 0;

                        for (long j = from; j < to; j++) {

                            String candidate = "benchmark-" + j;

                            HashUtils.matches(candidate, targetHash);

                            localCount++;
                        }

                        return localCount;
                    });

                    futures.add(future);
                }

                long completedOperations = 0;

                for (Future<Long> future : futures) {
                    completedOperations += future.get();
                }

                long elapsed = System.nanoTime() - start;

                if (completedOperations != operations) {
                    throw new IllegalStateException(
                            "Not all benchmark operations were completed."
                    );
                }

                totalTime += elapsed / 1_000_000.0;

            } catch (InterruptedException exception) {

                for (Future<?> future : futures) {
                    future.cancel(true);
                }

                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Benchmark was interrupted.",
                        exception
                );

            } catch (ExecutionException exception) {

                throw new IllegalStateException(
                        "A benchmark worker failed.",
                        exception.getCause()
                );

            } finally {
                executor.shutdownNow();
            }
        }

        return new BenchmarkResult(
                threadCount,
                operations,
                totalTime / REPETITIONS
        );
    }

    private void validateInput(
            long operations,
            String targetHash
    ) {

        if (operations < 1) {
            throw new IllegalArgumentException(
                    "Operations must be greater than zero."
            );
        }

        if (!HashUtils.isValidSha256Hash(targetHash)) {
            throw new IllegalArgumentException(
                    "Invalid SHA-256 hash."
            );
        }
    }
}
