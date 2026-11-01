package com.parallelhashing.generators;

// Provides indexed access to search candidates
public interface CandidateSource {

    long size();

    String candidateAt(long index);
}