
package com.parallelhashing.controller;

import com.parallelhashing.generators.BruteForceGenerator;
import com.parallelhashing.generators.CandidateSource;
import com.parallelhashing.generators.DictionaryGenerator;
import com.parallelhashing.generators.ListCandidateSource;

import com.parallelhashing.model.BenchmarkResult;
import com.parallelhashing.model.ExecutionMode;
import com.parallelhashing.model.SearchResult;

import com.parallelhashing.services.BenchmarkService;
import com.parallelhashing.services.SearchService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    // Search input fields
    @FXML private TextField hashField;
    @FXML private TextField threadsField;


    // Search configuration
    @FXML private ComboBox<String> searchModeComboBox;
    @FXML private ComboBox<String> executionModeComboBox;

    // Brute Force configuration
    @FXML private TextField minLengthField;
    @FXML private TextField maxLengthField;

    // Search result labels
    @FXML private Label statusLabel;
    @FXML private Label resultLabel;
    @FXML private Label timeLabel;

    // Benchmark input fields
    @FXML private TextField operationsField;
    @FXML private ComboBox<Integer> benchmarkThreadsComboBox;

    // Benchmark result labels
    @FXML private Label benchmarkStatusLabel;
    @FXML private Label sequentialBenchmarkTimeLabel;
    @FXML private Label parallelBenchmarkTimeLabel;
    @FXML private Label speedupLabel;
    @FXML private Label efficiencyLabel;

    // Activity log
    @FXML private TextArea activityLogArea;

    // Application services
    private final SearchService searchService = new SearchService();
    private final BenchmarkService benchmarkService = new BenchmarkService();

    // Background executor to keep the JavaFX interface responsive
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    // Initialize the interface
    @FXML
    public void initialize() {
        minLengthField.setText("1");
        maxLengthField.setText("5");

        searchModeComboBox.getItems().setAll(
                "Dictionary",
                "Brute Force"
        );

        executionModeComboBox.getItems().setAll(
                "Sequential",
                "Parallel"
        );

        searchModeComboBox.setValue("Dictionary");
        executionModeComboBox.setValue("Parallel");

        threadsField.setText("4");
        minLengthField.setText("1");
        maxLengthField.setText("4");

        benchmarkThreadsComboBox.getItems().setAll(
                1, 2, 4, 8, 16
        );

        benchmarkThreadsComboBox.setValue(4);
        operationsField.setText("20000");

        statusLabel.setText("Ready");
        resultLabel.setText("-");
        timeLabel.setText("-");

        benchmarkStatusLabel.setText("Ready to benchmark.");
        sequentialBenchmarkTimeLabel.setText("-");
        parallelBenchmarkTimeLabel.setText("-");
        speedupLabel.setText("-");
        efficiencyLabel.setText("-");

        addLog("Application initialized successfully.");
    }

    // Handle the password hash search
    @FXML
    private void handleStart() {

        String targetHash = hashField.getText().trim();

        if (!targetHash.matches("(?i)[0-9a-f]{64}")) {
            statusLabel.setText("Please enter a valid SHA-256 hash.");
            return;
        }

        int threadCount;

        try {
            threadCount = Integer.parseInt(threadsField.getText().trim());

            if (threadCount < 1 || threadCount > 64) {
                statusLabel.setText("Threads must be between 1 and 64.");
                return;
            }

        } catch (NumberFormatException exception) {
            statusLabel.setText("Invalid thread count.");
            return;
        }

        String searchMode = searchModeComboBox.getValue();
        String executionMode = executionModeComboBox.getValue();

        if (searchMode == null || executionMode == null) {
            statusLabel.setText("Please select both search options.");
            return;
        }

        CandidateSource source;

        try {

            if (searchMode.equals("Dictionary")) {

                var resource = getClass().getResource(
                        "/dictionaries/test-dictionary.txt"
                );

                if (resource == null) {
                    statusLabel.setText("Dictionary file not found.");
                    return;
                }

                var candidates = new DictionaryGenerator()
                        .loadDictionary(Path.of(resource.toURI()));

                source = new ListCandidateSource(candidates);

            } else {
                int minLength;
                int maxLength;

                try {
                    minLength = Integer.parseInt(minLengthField.getText().trim());
                    maxLength = Integer.parseInt(maxLengthField.getText().trim());

                    if (minLength < 1 || maxLength < minLength || maxLength > 12) {
                        statusLabel.setText(
                                "Length must be between 1 and 12, and minimum must not exceed maximum."
                        );
                        return;
                    }

                    source = new BruteForceGenerator(
                            minLength,
                            maxLength
                    );

                } catch (NumberFormatException exception) {
                    statusLabel.setText("Please enter valid candidate lengths.");
                    return;
                } catch (ArithmeticException exception) {
                    statusLabel.setText("The candidate search space is too large.");
                    return;
                }
            }

        } catch (Exception exception) {

            statusLabel.setText(
                    "Could not load candidates: " + exception.getMessage()
            );

            return;
        }

        ExecutionMode mode = executionMode.equals("Sequential")
                ? ExecutionMode.SEQUENTIAL
                : ExecutionMode.PARALLEL;

        statusLabel.setText("Searching...");
        resultLabel.setText("Processing...");
        timeLabel.setText("-");

        addLog("Started " + executionMode + " search.");

        Task<SearchResult> task = new Task<>() {

            @Override
            protected SearchResult call() {

                return searchService.search(
                        source,
                        targetHash,
                        mode,
                        threadCount
                );
            }
        };

        task.setOnSucceeded(event -> {

            SearchResult result = task.getValue();

            resultLabel.setText(
                    result.found()
                            ? "Found: " + result.matchedCandidate()
                            : "No matching candidate found."
            );

            timeLabel.setText(
                    String.format(
                            Locale.US,
                            "%.3f ms",
                            result.executionTimeMillis()
                    )
            );

            statusLabel.setText(
                    "Completed. Hashes checked: " + result.hashesChecked()
            );

            addLog(
                    "Search completed. Hashes checked: "
                            + result.hashesChecked()
            );
        });

        task.setOnFailed(event -> {

            statusLabel.setText(
                    "Search failed: " + task.getException().getMessage()
            );

            resultLabel.setText("-");

            addLog("Search failed: " + task.getException().getMessage());
        });

        executor.submit(task);
    }

    // Run the performance benchmark

    @FXML
    private void handleBenchmark() {

        long operations;

        try {
            operations = Long.parseLong(operationsField.getText().trim());

            if (operations < 1 || operations > 5_000_000) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            benchmarkStatusLabel.setText(
                    "Please enter a number between 1 and 5,000,000."
            );
            return;
        }

        benchmarkStatusLabel.setText("Running benchmark...");
        sequentialBenchmarkTimeLabel.setText("Running...");
        parallelBenchmarkTimeLabel.setText("Running...");
        speedupLabel.setText("--");
        efficiencyLabel.setText("--");

        addLog("\nStarting performance comparison...");

        String targetHash =
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        Task<List<BenchmarkResult>> task = new Task<>() {

            @Override
            protected List<BenchmarkResult> call() {

                int[] threadCounts = {1, 2, 4, 8, 16};

                // Warm-up phase
                long warmupOperations = Math.min(operations, 5000);

                benchmarkService.benchmarkSequential(
                        warmupOperations, targetHash
                );

                for (int threads : threadCounts) {
                    benchmarkService.benchmarkParallel(
                            warmupOperations, threads, targetHash
                    );
                }

                // Measure sequential execution
                BenchmarkResult sequentialResult =
                        benchmarkService.benchmarkSequential(
                                operations, targetHash
                        );

                List<BenchmarkResult> results = new ArrayList<>();

                results.add(sequentialResult);

                // Measure parallel execution with different thread counts
                for (int threads : threadCounts) {

                    BenchmarkResult result =
                            benchmarkService.benchmarkParallel(
                                    operations, threads, targetHash
                            );

                    results.add(result);
                }

                return results;
            }
        };

        task.setOnSucceeded(event -> {

            List<BenchmarkResult> results = task.getValue();

            BenchmarkResult sequential = results.get(0);

            double sequentialTime = sequential.averageTimeMillis();

            sequentialBenchmarkTimeLabel.setText(
                    String.format("%.3f ms", sequentialTime)
            );

            StringBuilder comparison = new StringBuilder();

            comparison.append("\n--- BENCHMARK COMPARISON ---\n");
            comparison.append("Operations: ").append(operations).append("\n\n");

            comparison.append(String.format(
                    "%-10s %-15s %-15s %-15s%n",
                    "Threads", "Time (ms)", "Speedup", "Efficiency"
            ));

            comparison.append(
                    "----------------------------------------------------------\n"
            );

            BenchmarkResult bestResult = null;
            double bestSpeedup = 0;

            for (int i = 1; i < results.size(); i++) {

                BenchmarkResult parallel = results.get(i);

                double parallelTime = parallel.averageTimeMillis();

                double speedup = sequentialTime / parallelTime;

                double efficiency =
                        (speedup / parallel.threadCount()) * 100;

                comparison.append(String.format(
                        "%-10d %-15.3f %-15.2f %-14.2f%%%n",
                        parallel.threadCount(),
                        parallelTime,
                        speedup,
                        efficiency
                ));

                if (speedup > bestSpeedup) {
                    bestSpeedup = speedup;
                    bestResult = parallel;
                }
            }

            // Display the best result in the main result labels
            if (bestResult != null) {

                double bestTime = bestResult.averageTimeMillis();

                double bestEfficiency =
                        (bestSpeedup / bestResult.threadCount()) * 100;

                parallelBenchmarkTimeLabel.setText(
                        String.format("%.3f ms", bestTime)
                );

                speedupLabel.setText(
                        String.format("%.2fx", bestSpeedup)
                );

                efficiencyLabel.setText(
                        String.format("%.2f%%", bestEfficiency)
                );
            }

            comparison.append("\nBest thread count: ")
                    .append(bestResult == null ? "--" : bestResult.threadCount());

            addLog(comparison.toString());

            benchmarkStatusLabel.setText(
                    "Benchmark completed successfully."
            );
        });

        task.setOnFailed(event -> {

            Throwable error = task.getException();

            benchmarkStatusLabel.setText("Benchmark failed.");

            addLog("Benchmark error: " + error.getMessage());

            error.printStackTrace();
        });

        executor.submit(task);
    }

    // Reset the search and benchmark interface
    @FXML
    private void handleReset() {

        hashField.clear();
        threadsField.setText("4");

        searchModeComboBox.setValue("Dictionary");
        executionModeComboBox.setValue("Parallel");

        statusLabel.setText("Ready");
        resultLabel.setText("-");
        timeLabel.setText("-");

        operationsField.setText("20000");
        benchmarkThreadsComboBox.setValue(4);

        benchmarkStatusLabel.setText("Ready to benchmark.");
        sequentialBenchmarkTimeLabel.setText("-");
        parallelBenchmarkTimeLabel.setText("-");
        speedupLabel.setText("-");
        efficiencyLabel.setText("-");

        addLog("Interface reset.");
    }

    // Add messages to the activity log
    private void addLog(String message) {

        if (activityLogArea != null) {
            activityLogArea.appendText(
                    System.lineSeparator() + message
            );
        }
    }
}
