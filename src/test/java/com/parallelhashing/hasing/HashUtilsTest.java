
package com.parallelhashing.hasing;

import com.parallelhashing.hashing.HashUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilsTest {

    @Test
    void shouldGenerateCorrectEmptyStringHash() {

        String expected =
                "e3b0c44298fc1c149afbf4c8996fb924"
                        + "27ae41e4649b934ca495991b7852b855";

        assertEquals(expected, HashUtils.sha256(""));
    }

    @Test
    void shouldAcceptValidSha256Hash() {

        String validHash =
                "e3b0c44298fc1c149afbf4c8996fb924"
                        + "27ae41e4649b934ca495991b7852b855";

        assertTrue(HashUtils.isValidSha256Hash(validHash));
    }

    @Test
    void shouldRejectInvalidHash() {

        assertFalse(HashUtils.isValidSha256Hash("12345"));
    }

    @Test
    void shouldMatchCorrectCandidate() {

        String emptyStringHash =
                "e3b0c44298fc1c149afbf4c8996fb924"
                        + "27ae41e4649b934ca495991b7852b855";

        assertTrue(HashUtils.matches("", emptyStringHash));
    }

    @Test
    void shouldRejectIncorrectCandidate() {

        String emptyStringHash =
                "e3b0c44298fc1c149afbf4c8996fb924"
                        + "27ae41e4649b934ca495991b7852b855";

        assertFalse(HashUtils.matches("hello", emptyStringHash));
    }
}
