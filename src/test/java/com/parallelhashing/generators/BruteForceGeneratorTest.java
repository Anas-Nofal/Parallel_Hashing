
package com.parallelhashing.generators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BruteForceGeneratorTest {

    @Test
    void shouldGenerateAllCandidatesInCorrectOrder() {

        BruteForceGenerator generator =
                new BruteForceGenerator("ab", 1, 2);

        assertEquals(6, generator.size());

        assertEquals("a", generator.candidateAt(0));
        assertEquals("b", generator.candidateAt(1));
        assertEquals("aa", generator.candidateAt(2));
        assertEquals("ab", generator.candidateAt(3));
        assertEquals("ba", generator.candidateAt(4));
        assertEquals("bb", generator.candidateAt(5));
    }

    @Test
    void shouldRejectInvalidIndex() {

        BruteForceGenerator generator =
                new BruteForceGenerator("ab", 1, 2);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> generator.candidateAt(6)
        );
    }

    @Test
    void shouldRejectInvalidCharacterSet() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new BruteForceGenerator("", 1, 2)
        );
    }
}
