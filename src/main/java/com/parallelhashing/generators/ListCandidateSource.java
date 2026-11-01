
package com.parallelhashing.generators;

import java.util.List;
import java.util.Objects;

// Provides indexed access to dictionary candidates
public class ListCandidateSource implements CandidateSource {

    private final List<String> candidates;

    public ListCandidateSource(List<String> candidates) {
        this.candidates = List.copyOf(
                Objects.requireNonNull(candidates)
        );
    }

    @Override
    public long size() {
        return candidates.size();
    }

    @Override
    public String candidateAt(long index) {

        if (index < 0 || index >= candidates.size()) {
            throw new IndexOutOfBoundsException(
                    "Candidate index is out of range."
            );
        }

        return candidates.get((int) index);
    }
}
