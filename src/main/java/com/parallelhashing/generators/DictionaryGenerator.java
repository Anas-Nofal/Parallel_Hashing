
package com.parallelhashing.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class DictionaryGenerator {

    // Load candidate words from a local dictionary file
    public List<String> loadDictionary(Path filePath) throws IOException {

        Objects.requireNonNull(filePath, "File path cannot be null");

        if (!Files.exists(filePath)) {
            throw new IOException(
                    "Dictionary file does not exist: " + filePath
            );
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException(
                    "The provided path is not a regular file."
            );
        }

        // Store unique candidates while preserving their original order
        LinkedHashSet<String> candidates = new LinkedHashSet<>();

        // Read the dictionary line by line using UTF-8
        try (BufferedReader reader = Files.newBufferedReader(
                filePath,
                StandardCharsets.UTF_8
        )) {

            String line;

            while ((line = reader.readLine()) != null) {

                String candidate = line.trim();

                // Ignore empty lines and comment lines
                if (candidate.isEmpty() || candidate.startsWith("#")) {
                    continue;
                }

                candidates.add(candidate);
            }
        }

        // Return an immutable list
        return List.copyOf(candidates);
    }
}
