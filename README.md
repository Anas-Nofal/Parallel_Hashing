# Parallel Hashing Lab

**Parallel Hashing Lab** is a JavaFX desktop application developed to
demonstrate and compare sequential and parallel approaches to SHA-256
hash searching.

The application provides two candidate-search methods---Dictionary
Search and Brute Force Search---and allows users to run them in
sequential or multithreaded mode. It also measures execution time and
the number of hashes checked to help explore the performance of parallel
computing.

## Features

-   **SHA-256 hashing:** Calculates SHA-256 digests for candidate
    strings.
-   **Dictionary Search:** Checks candidates loaded from a dictionary
    file.
-   **Brute Force Search:** Generates candidates using lowercase English
    letters and digits.
-   **Configurable candidate length:** Sets the minimum and maximum
    lengths for Brute Force Search.
-   **Sequential execution:** Processes candidates using a single
    execution thread.
-   **Parallel execution:** Distributes candidate-search work across a
    configurable number of threads.
-   **Performance measurements:** Reports execution time and the number
    of hashes checked.
-   **Benchmarking:** Compares execution performance using different
    thread counts.
-   **JavaFX graphical interface:** Provides controls for configuring
    and running searches.

## Technologies

-   Java
-   JavaFX
-   Apache Maven
-   SHA-256
-   Java concurrency and multithreading
-   JUnit 5

## How It Works

The user provides a target SHA-256 hash and selects a search method and
execution mode. The application generates or loads candidate strings,
calculates each candidate's SHA-256 hash, and compares it with the
target hash.

### Search methods

**Dictionary Search**

Loads candidate words from the dictionary resource and checks their
SHA-256 hashes until a match is found or the available candidates have
been checked.

**Brute Force Search**

Generates candidate strings from the configured character set of
lowercase English letters and digits. The user can specify the minimum
and maximum candidate lengths.

### Execution modes

**Sequential**

Processes candidates one after another using a single thread.

**Parallel**

Uses multiple threads to process portions of the candidate search
concurrently. The number of threads can be configured by the user.

## Performance Metrics

The application measures execution time and the number of hashes
checked. It can be used to compare sequential and parallel execution.

Two common metrics for interpreting the results are:

**Speedup**

``` text
Speedup = Sequential Execution Time / Parallel Execution Time
```

Speedup compares the time taken by the sequential and parallel
implementations.

**Efficiency**

``` text
Efficiency = Speedup / Number of Threads
```

Efficiency indicates how effectively the available threads are being
used.

Actual performance depends on factors such as workload size,
thread-management overhead, hardware, and JVM behavior. Parallel
execution is not guaranteed to be faster for every workload.

## Requirements

-   JDK 27 (the version used during development)
-   Apache Maven
-   A supported desktop environment for JavaFX

## Run the Application from Source

Clone the repository:

``` bash
git clone https://github.com/Anas-Nofal/Parallel_Hashing.git
```

Enter the project directory:

``` bash
cd Parallel_Hashing
```

Run the application using Maven:

``` bash
mvn clean javafx:run
```

Maven downloads the dependencies declared in `pom.xml` and launches the
JavaFX application.

## Run the Tests

Execute the automated tests with:

``` bash
mvn test
```

## Project Structure

``` text
Parallel_Hashing/
├── src/
│   ├── main/
│   │   ├── java/com/parallelhashing/
│   │   │   ├── algorithms/
│   │   │   ├── controller/
│   │   │   ├── generators/
│   │   │   ├── hashing/
│   │   │   ├── model/
│   │   │   ├── services/
│   │   │   ├── HashDemo.java
│   │   │   └── Main.java
│   │   └── resources/
│   │       ├── dictionaries/
│   │       ├── fxml/
│   │       └── styles/
│   └── test/
│       └── java/com/parallelhashing/
├── .gitignore
├── pom.xml
└── README.md
```

## Learning Objectives

This project demonstrates practical concepts in parallel computing,
including:

-   Thread creation and management.
-   Dividing work into smaller tasks.
-   Concurrent execution.
-   Comparing sequential and parallel implementations.
-   Measuring speedup and efficiency.
-   Understanding thread-management overhead and workload size.

## Disclaimer

This project is intended for educational purposes to demonstrate SHA-256
hashing and parallel processing. It is not intended to be a production
password-recovery or security-auditing tool. Brute Force searches can
require a very large amount of computation, particularly as candidate
length increases.

## Author

**Anas Nofal**

------------------------------------------------------------------------

*Parallel Hashing Lab --- Exploring sequential and parallel computing
through SHA-256 hash searching.*
