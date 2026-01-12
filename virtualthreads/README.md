# Virtual Threads vs. Traditional Threads in Java

This project provides a comparative analysis of Java's virtual threads and traditional (platform) threads, demonstrating the performance benefits of using virtual threads for I/O-bound tasks.

## Introduction

- **Traditional Threads:** Each thread is mapped directly to an OS thread, which can be a limited resource. Creating and managing a large number of traditional threads can lead to high memory consumption and context-switching overhead.

- **Virtual Threads:** Introduced in Java 21, virtual threads are lightweight threads managed by the Java Virtual Machine (JVM). They are not tied to OS threads, allowing for a much larger number of concurrent tasks without the associated overhead.

## Architecture Diagram

### Traditional Threads

```
+----------------+       +----------------+
| Java Thread    | ----> | OS Thread      |
+----------------+       +----------------+
+----------------+       +----------------+
| Java Thread    | ----> | OS Thread      |
+----------------+       +----------------+
       ...                    ...
(Limited by OS thread count)
```

### Virtual Threads

```
+----------------+       +----------------+
| Virtual Thread | -\    | OS Thread      |
+----------------+   \   +----------------+
+----------------+    -> |                |
| Virtual Thread | --/   | Carrier Thread |
+----------------+       +----------------+
+----------------+       +----------------+
| Virtual Thread | -\    | OS Thread      |
+----------------+   \   +----------------+
+----------------+    -> |                |
| Virtual Thread | --/   | Carrier Thread |
+----------------+       +----------------+
       ...                    ...
(Managed by JVM, not limited by OS threads)
```

## How to Run the Examples

1. **Compile the code:**
   ```bash
   mvn compile
   ```

2. **Run the traditional thread example:**
   ```bash
   mvn exec:java -Dexec.mainClass="virtualthreads.TraditionalThreadExample"
   ```

3. **Run the virtual thread example:**
   ```bash
   mvn exec:java -Dexec.mainClass="virtualthreads.VirtualThreadExample"
   ```

## Performance Metrics

The examples demonstrate how virtual threads can handle a large number of concurrent tasks more efficiently than traditional threads. When running the examples, you will observe that the virtual thread implementation completes the tasks in a significantly shorter amount of time, showcasing the performance improvements.
