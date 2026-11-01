package com.parallelhashing.generators;

import java.util.Objects;

// Generates candidates without storing the entire search space
public class BruteForceGenerator implements CandidateSource {

    // Lowercase English letters and digits
    public static final String DEFAULT_CHARACTER_SET =
            "abcdefghijklmnopqrstuvwxyz0123456789";

    private final String characterSet;
    private final int minLength;
    private final int maxLength;

    // Constructor using the default character set
    public BruteForceGenerator(int minLength, int maxLength) {
        this(DEFAULT_CHARACTER_SET, minLength, maxLength);
    }

    // Constructor allowing a custom character set
    public BruteForceGenerator(
            String characterSet,
            int minLength,
            int maxLength
    ) {
        Objects.requireNonNull(characterSet);

        if (characterSet.isEmpty()) {
            throw new IllegalArgumentException(
                    "Character set cannot be empty."
            );
        }

        if (minLength < 1 || maxLength < minLength) {
            throw new IllegalArgumentException(
                    "Invalid candidate length range."
            );
        }

        this.characterSet = characterSet;
        this.minLength = minLength;
        this.maxLength = maxLength;

        calculateTotalSize();
    }

    @Override
    public long size() {
        return calculateTotalSize();
    }

    private long calculateTotalSize() {
        long total = 0;
        long power = 1;

        for (int length = 1; length <= maxLength; length++) {
            power = Math.multiplyExact(power, characterSet.length());

            if (length >= minLength) {
                total = Math.addExact(total, power);
            }
        }

        return total;
    }

    @Override
    public String candidateAt(long index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(
                    "Candidate index is out of range."
            );
        }

        long remaining = index;
        int base = characterSet.length();

        for (int length = minLength; length <= maxLength; length++) {
            long count = 1;

            for (int i = 0; i < length; i++) {
                count = Math.multiplyExact(count, base);
            }

            if (remaining < count) {
                return decode(remaining, length);
            }

            remaining -= count;
        }

        throw new IllegalStateException(
                "Candidate generation failed."
        );
    }

    private String decode(long index, int length) {
        int base = characterSet.length();
        char[] result = new char[length];

        for (int i = length - 1; i >= 0; i--) {
            int digit = (int) (index % base);
            result[i] = characterSet.charAt(digit);
            index /= base;
        }

        return new String(result);
    }
}