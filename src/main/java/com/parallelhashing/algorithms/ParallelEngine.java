
package com.parallelhashing.algorithms;

import com.parallelhashing.generators.CandidateSource;
import com.parallelhashing.hashing.HashUtils;
import com.parallelhashing.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ParallelEngine {

    // Search candidates using multiple worker threads
    public SearchResult search(
            CandidateSource source,
            String targetHash,
            int threadCount
    ) {

        Objects.requireNonNull(source, "Candidate source cannot be null");

        if (!HashUtils.isValidSha256Hash(targetHash)) {
            throw new IllegalArgumentException(
                    "The target must be a valid SHA-256 hash."
            );
        }

        if (threadCount < 1 || threadCount > 64) {
            throw new IllegalArgumentException(
                    "Thread count must be between 1 and 64."
            );
        }

        long startTime = System.nanoTime();
        long totalCandidates = source.size();

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference<String> matchedCandidate = new AtomicReference<>(null);
        AtomicLong hashesChecked = new AtomicLong(0);

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        List<Future<?>> futures = new ArrayList<>();

        try {

            // Divide the search space among worker threads
            for (int i = 0; i < threadCount; i++) {

                final long start =
                        (long) i * totalCandidates / threadCount;

                final long end =
                        (long) (i + 1) * totalCandidates / threadCount;

                Future<?> future = executor.submit(() -> {

                    for (long j = start; j < end; j++) {

                        if (found.get() || Thread.currentThread().isInterrupted()) {
                            break;
                        }

                        String candidate = Objects.requireNonNull(
                                source.candidateAt(j),
                                "Candidate cannot be null"
                        );

                        hashesChecked.incrementAndGet();

                        if (HashUtils.matches(candidate, targetHash)) {

                            if (matchedCandidate.compareAndSet(null, candidate)) {
                                found.set(true);
                            }

                            break;
                        }
                    }
                });

                futures.add(future);
            }

            // Wait for all worker tasks
            for (Future<?> future : futures) {
                future.get();
            }

        } catch (InterruptedException exception) {

            for (Future<?> future : futures) {
                future.cancel(true);
            }

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Parallel search was interrupted.",
                    exception
            );

        } catch (ExecutionException exception) {

            throw new IllegalStateException(
                    "A worker thread failed during the search.",
                    exception.getCause()
            );

        } finally {
            executor.shutdownNow();
        }

        long elapsedTime = System.nanoTime() - startTime;

        String result = matchedCandidate.get();

        return new SearchResult(
                result != null,
                result,
                hashesChecked.get(),
                elapsedTime
        );
    }
}
