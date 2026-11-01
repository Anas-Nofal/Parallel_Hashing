
package com.parallelhashing.hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public class HashUtils {

    private static final String ALGORITHM = "SHA-256";

    // Convert a plain text into its SHA-256 hash
    public static String sha256(String input) {

        Objects.requireNonNull(input, "Input cannot be null");

        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

            byte[] hashBytes = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }

    // Check whether the supplied hash has a valid SHA-256 format
    public static boolean isValidSha256Hash(String hash) {

        return hash != null && hash.matches("(?i)[0-9a-f]{64}");
    }

    // Compare a candidate text with an expected SHA-256 hash
    public static boolean matches(String candidate, String expectedHash) {

        if (!isValidSha256Hash(expectedHash)) {
            return false;
        }

        byte[] expectedBytes = HexFormat.of().parseHex(expectedHash);
        byte[] actualBytes = HexFormat.of().parseHex(sha256(candidate));

        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }
}
