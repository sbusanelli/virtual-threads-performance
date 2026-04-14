package virtualthreads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Benchmark tests for Virtual Threads and Traditional Threads
 * Performance comparison with detailed metrics and analysis
 */
@DisplayName("Benchmark Tests")
class BenchmarkTest {

    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1000;
    private static final int TASK_COUNT = 1000;
    private static final long TASK_DURATION_MS = 1;

    @Test
    @DisplayName("Virtual Threads - Basic Benchmark")
    void testVirtualThreadsBasicBenchmark() {
        System.out.println("\n=== Virtual Threads Basic Benchmark ===");
        
        // Warmup
        warmupVirtualThreads();
        
        // Benchmark
        long[] durations = new long[BENCHMARK_ITERATIONS];
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            durations[i] = measureVirtualThreadsPerformance();
        }
        
        // Calculate statistics
        BenchmarkStats stats = calculateBenchmarkStats(durations);
        
        System.out.printf("Virtual Threads Benchmark Results:%n");
        System.out.printf("  Average: %.2f ms%n", stats.average);
        System.out.printf("  Min: %.2f ms%n", stats.min);
        System.out.printf("  Max: %.2f ms%n", stats.max);
        System.out.printf("  P50: %.2f ms%n", stats.p50);
        System.out.printf("  P95: %.2f ms%n", stats.p95);
        System.out.printf("  P99: %.2f ms%n", stats.p99);
        System.out.printf("  Standard Deviation: %.2f ms%n", stats.standardDeviation);
        
        assertTrue(stats.average > 0, "Average duration should be positive");
        assertTrue(stats.standardDeviation > 0, "Standard deviation should be positive");
    }

    @Test
    @DisplayName("Traditional Threads - Basic Benchmark")
    void testTraditionalThreadsBasicBenchmark() {
        System.out.println("\n=== Traditional Threads Basic Benchmark ===");
        
        // Warmup
        warmupTraditionalThreads();
        
        // Benchmark
        long[] durations = new long[BENCHMARK_ITERATIONS];
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            durations[i] = measureTraditionalThreadsPerformance();
        }
        
        // Calculate statistics
        BenchmarkStats stats = calculateBenchmarkStats(durations);
        
        System.out.printf("Traditional Threads Benchmark Results:%n");
        System.out.printf("  Average: %.2f ms%n", stats.average);
        System.out.printf("  Min: %.2f ms%n", stats.min);
        System.out.printf("  Max: %.2f ms%n", stats.max);
        System.out.printf("  P50: %.2f ms%n", stats.p50);
        System.out.printf("  P95: %.2f ms%n", stats.p95);
        System.out.printf("  P99: %.2f ms%n", stats.p99);
        System.out.printf("  Standard Deviation: %.2f ms%n", stats.standardDeviation);
        
        assertTrue(stats.average > 0, "Average duration should be positive");
        assertTrue(stats.standardDeviation > 0, "Standard deviation should be positive");
    }

    @Test
    @DisplayName("Virtual Threads - Throughput Benchmark")
    void testVirtualThreadsThroughputBenchmark() {
        System.out.println("\n=== Virtual Threads Throughput Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000, 5000};
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            // Warmup
            warmupVirtualThreads();
            
            // Benchmark
            long[] durations = new long[100];
            
            for (int i = 0; i < 100; i++) {
                durations[i] = measureVirtualThreadsThroughput(taskCount);
            }
            
            BenchmarkStats stats = calculateBenchmarkStats(durations);
            double throughput = (double) taskCount / (stats.average / 1000.0);
            
            System.out.printf("  Duration: %.2f ms%n", stats.average);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            System.out.printf("  P95: %.2f ms%n", stats.p95);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Throughput Benchmark")
    void testTraditionalThreadsThroughputBenchmark() {
        System.out.println("\n=== Traditional Threads Throughput Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            // Warmup
            warmupTraditionalThreads();
            
            // Benchmark
            long[] durations = new long[100];
            
            for (int i = 0; i < 100; i++) {
                durations[i] = measureTraditionalThreadsThroughput(taskCount);
            }
            
            BenchmarkStats stats = calculateBenchmarkStats(durations);
            double throughput = (double) taskCount / (stats.average / 1000.0);
            
            System.out.printf("  Duration: %.2f ms%n", stats.average);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            System.out.printf("  P95: %.2f ms%n", stats.p95);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Memory Benchmark")
    void testVirtualThreadsMemoryBenchmark() {
        System.out.println("\n=== Virtual Threads Memory Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000, 5000};
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            // Measure memory before
            System.gc();
            long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            // Run tasks
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                for (int i = 0; i < taskCount; i++) {
                    futures.add(executor.submit(() -> {
                        // Allocate memory
                        byte[] memory = new byte[1024];
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }));
                }
                
                // Wait for completion
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for memory benchmark
                    }
                }
            }
            
            // Measure memory after
            System.gc();
            long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            long memoryUsed = afterMemory - beforeMemory;
            double memoryPerTask = (double) memoryUsed / taskCount;
            
            System.out.printf("  Memory Used: %d KB%n", memoryUsed / 1024);
            System.out.printf("  Memory Per Task: %.2f bytes%n", memoryPerTask);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Memory Benchmark")
    void testTraditionalThreadsMemoryBenchmark() {
        System.out.println("\n=== Traditional Threads Memory Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            // Measure memory before
            System.gc();
            long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            // Run tasks
            try (ExecutorService executor = Executors.newFixedThreadPool(taskCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                for (int i = 0; i < taskCount; i++) {
                    futures.add(executor.submit(() -> {
                        // Allocate memory
                        byte[] memory = new byte[1024];
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }));
                }
                
                // Wait for completion
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for memory benchmark
                    }
                }
            }
            
            // Measure memory after
            System.gc();
            long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            long memoryUsed = afterMemory - beforeMemory;
            double memoryPerTask = (double) memoryUsed / taskCount;
            
            System.out.printf("  Memory Used: %d KB%n", memoryUsed / 1024);
            System.out.printf("  Memory Per Task: %.2f bytes%n", memoryPerTask);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Latency Benchmark")
    void testVirtualThreadsLatencyBenchmark() {
        System.out.println("\n=== Virtual Threads Latency Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000, 5000};
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            List<Long> latencies = new ArrayList<>();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<Long>> futures = new ArrayList<>();
                
                for (int i = 0; i < taskCount; i++) {
                    futures.add(executor.submit(() -> {
                        long startTime = System.nanoTime();
                        
                        // Simulate work
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        long endTime = System.nanoTime();
                        return endTime - startTime;
                    }));
                }
                
                // Wait for completion and collect latencies
                for (Future<Long> future : futures) {
                    try {
                        latencies.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for latency benchmark
                    }
                }
            }
            
            // Calculate latency statistics
            LatencyStats latencyStats = calculateLatencyStats(latencies);
            
            System.out.printf("  Average Latency: %.2f ms%n", latencyStats.average);
            System.out.printf("  Min Latency: %.2f ms%n", latencyStats.min);
            System.out.printf("  Max Latency: %.2f ms%n", latencyStats.max);
            System.out.printf("  P50 Latency: %.2f ms%n", latencyStats.p50);
            System.out.printf("  P95 Latency: %.2f ms%n", latencyStats.p95);
            System.out.printf("  P99 Latency: %.2f ms%n", latencyStats.p99);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Latency Benchmark")
    void testTraditionalThreadsLatencyBenchmark() {
        System.out.println("\n=== Traditional Threads Latency Benchmark ===");
        
        int[] taskCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
        
        for (int taskCount : taskCounts) {
            System.out.printf("Testing %d tasks:%n", taskCount);
            
            List<Long> latencies = new ArrayList<>();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(taskCount)) {
                List<Future<Long>> futures = new ArrayList<>();
                
                for (int i = 0; i < taskCount; i++) {
                    futures.add(executor.submit(() -> {
                        long startTime = System.nanoTime();
                        
                        // Simulate work
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        long endTime = System.nanoTime();
                        return endTime - startTime;
                    }));
                }
                
                // Wait for completion and collect latencies
                for (Future<Long> future : futures) {
                    try {
                        latencies.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for latency benchmark
                    }
                }
            }
            
            // Calculate latency statistics
            LatencyStats latencyStats = calculateLatencyStats(latencies);
            
            System.out.printf("  Average Latency: %.2f ms%n", latencyStats.average);
            System.out.printf("  Min Latency: %.2f ms%n", latencyStats.min);
            System.out.printf("  Max Latency: %.2f ms%n", latencyStats.max);
            System.out.printf("  P50 Latency: %.2f ms%n", latencyStats.p50);
            System.out.printf("  P95 Latency: %.2f ms%n", latencyStats.p95);
            System.out.printf("  P99 Latency: %.2f ms%n", latencyStats.p99);
        }
    }

    @Nested
    @DisplayName("Advanced Benchmarks")
    class AdvancedBenchmarks {

        @Test
        @DisplayName("Virtual Threads - CPU Intensive Benchmark")
        void testVirtualThreadsCPUIntensiveBenchmark() {
            System.out.println("\n=== Virtual Threads CPU Intensive Benchmark ===");
            
            int[] taskCounts = {100, 500, 1000, 2000};
            int cpuWorkload = 10000; // Number of operations per task
            
            for (int taskCount : taskCounts) {
                System.out.printf("Testing %d tasks with %d operations each:%n", taskCount, cpuWorkload);
                
                // Warmup
                warmupVirtualThreads();
                
                // Benchmark
                long[] durations = new long[50];
                
                for (int i = 0; i < 50; i++) {
                    durations[i] = measureVirtualThreadsCPUIntensive(taskCount, cpuWorkload);
                }
                
                BenchmarkStats stats = calculateBenchmarkStats(durations);
                double throughput = (double) taskCount * cpuWorkload / (stats.average / 1000.0);
                
                System.out.printf("  Duration: %.2f ms%n", stats.average);
                System.out.printf("  Throughput: %.2f ops/sec%n", throughput);
                System.out.printf("  P95: %.2f ms%n", stats.p95);
            }
        }

        @Test
        @DisplayName("Traditional Threads - CPU Intensive Benchmark")
        void testTraditionalThreadsCPUIntensiveBenchmark() {
            System.out.println("\n=== Traditional Threads CPU Intensive Benchmark ===");
            
            int[] taskCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
            int cpuWorkload = 10000; // Number of operations per task
            
            for (int taskCount : taskCounts) {
                System.out.printf("Testing %d tasks with %d operations each:%n", taskCount, cpuWorkload);
                
                // Warmup
                warmupTraditionalThreads();
                
                // Benchmark
                long[] durations = new long[50];
                
                for (int i = 0; i < 50; i++) {
                    durations[i] = measureTraditionalThreadsCPUIntensive(taskCount, cpuWorkload);
                }
                
                BenchmarkStats stats = calculateBenchmarkStats(durations);
                double throughput = (double) taskCount * cpuWorkload / (stats.average / 1000.0);
                
                System.out.printf("  Duration: %.2f ms%n", stats.average);
                System.out.printf("  Throughput: %.2f ops/sec%n", throughput);
                System.out.printf("  P95: %.2f ms%n", stats.p95);
            }
        }

        @Test
        @DisplayName("Virtual Threads - I/O Intensive Benchmark")
        void testVirtualThreadsIOIntensiveBenchmark() {
            System.out.println("\n=== Virtual Threads I/O Intensive Benchmark ===");
            
            int[] taskCounts = {100, 500, 1000, 2000};
            int ioWorkload = 10; // Number of I/O operations per task
            
            for (int taskCount : taskCounts) {
                System.out.printf("Testing %d tasks with %d I/O operations each:%n", taskCount, ioWorkload);
                
                // Warmup
                warmupVirtualThreads();
                
                // Benchmark
                long[] durations = new long[50];
                
                for (int i = 0; i < 50; i++) {
                    durations[i] = measureVirtualThreadsIOIntensive(taskCount, ioWorkload);
                }
                
                BenchmarkStats stats = calculateBenchmarkStats(durations);
                double throughput = (double) taskCount * ioWorkload / (stats.average / 1000.0);
                
                System.out.printf("  Duration: %.2f ms%n", stats.average);
                System.out.printf("  Throughput: %.2f I/O ops/sec%n", throughput);
                System.out.printf("  P95: %.2f ms%n", stats.p95);
            }
        }

        @Test
        @DisplayName("Traditional Threads - I/O Intensive Benchmark")
        void testTraditionalThreadsIOIntensiveBenchmark() {
            System.out.println("\n=== Traditional Threads I/O Intensive Benchmark ===");
            
            int[] taskCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
            int ioWorkload = 10; // Number of I/O operations per task
            
            for (int taskCount : taskCounts) {
                System.out.printf("Testing %d tasks with %d I/O operations each:%n", taskCount, ioWorkload);
                
                // Warmup
                warmupTraditionalThreads();
                
                // Benchmark
                long[] durations = new long[50];
                
                for (int i = 0; i < 50; i++) {
                    durations[i] = measureTraditionalThreadsIOIntensive(taskCount, ioWorkload);
                }
                
                BenchmarkStats stats = calculateBenchmarkStats(durations);
                double throughput = (double) taskCount * ioWorkload / (stats.average / 1000.0);
                
                System.out.printf("  Duration: %.2f ms%n", stats.average);
                System.out.printf("  Throughput: %.2f I/O ops/sec%n", throughput);
                System.out.printf("  P95: %.2f ms%n", stats.p95);
            }
        }
    }

    // Helper methods for benchmarking
    private void warmupVirtualThreads() {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            measureVirtualThreadsPerformance();
        }
    }

    private void warmupTraditionalThreads() {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            measureTraditionalThreadsPerformance();
        }
    }

    private long measureVirtualThreadsPerformance() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsPerformance() {
        try (ExecutorService executor = Executors.newFixedThreadPool(TASK_COUNT)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureVirtualThreadsThroughput(int taskCount) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsThroughput(int taskCount) {
        try (ExecutorService executor = Executors.newFixedThreadPool(taskCount)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureVirtualThreadsCPUIntensive(int taskCount, int cpuWorkload) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    // CPU intensive work
                    long sum = 0;
                    for (int j = 0; j < cpuWorkload; j++) {
                        sum += j;
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsCPUIntensive(int taskCount, int cpuWorkload) {
        try (ExecutorService executor = Executors.newFixedThreadPool(taskCount)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    // CPU intensive work
                    long sum = 0;
                    for (int j = 0; j < cpuWorkload; j++) {
                        sum += j;
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureVirtualThreadsIOIntensive(int taskCount, int ioWorkload) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    // I/O intensive work (simulated)
                    for (int j = 0; j < ioWorkload; j++) {
                        try {
                            Thread.sleep(1); // Simulate I/O latency
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsIOIntensive(int taskCount, int ioWorkload) {
        try (ExecutorService executor = Executors.newFixedThreadPool(taskCount)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    // I/O intensive work (simulated)
                    for (int j = 0; j < ioWorkload; j++) {
                        try {
                            Thread.sleep(1); // Simulate I/O latency
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for benchmark
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private BenchmarkStats calculateBenchmarkStats(long[] durations) {
        double sum = 0;
        double min = Long.MAX_VALUE;
        double max = Long.MIN_VALUE;
        
        for (long duration : durations) {
            sum += duration;
            if (duration < min) min = duration;
            if (duration > max) max = duration;
        }
        
        double average = sum / durations.length;
        
        // Calculate percentiles
        double[] sorted = new double[durations.length];
        for (int i = 0; i < durations.length; i++) {
            sorted[i] = durations[i];
        }
        java.util.Arrays.sort(sorted);
        
        double p50 = sorted[(int) (durations.length * 0.5)];
        double p95 = sorted[(int) (durations.length * 0.95)];
        double p99 = sorted[(int) (durations.length * 0.99)];
        
        // Calculate standard deviation
        double variance = 0;
        for (long duration : durations) {
            variance += Math.pow(duration - average, 2);
        }
        double standardDeviation = Math.sqrt(variance / durations.length);
        
        return new BenchmarkStats(average, min, max, p50, p95, p99, standardDeviation);
    }

    private LatencyStats calculateLatencyStats(List<Long> latencies) {
        if (latencies.isEmpty()) {
            return new LatencyStats(0, 0, 0, 0, 0, 0);
        }
        
        double sum = 0;
        double min = Long.MAX_VALUE;
        double max = Long.MIN_VALUE;
        
        for (Long latency : latencies) {
            sum += latency;
            if (latency < min) min = latency;
            if (latency > max) max = latency;
        }
        
        double average = sum / latencies.size();
        
        // Calculate percentiles
        double[] sorted = new double[latencies.size()];
        for (int i = 0; i < latencies.size(); i++) {
            sorted[i] = latencies.get(i);
        }
        java.util.Arrays.sort(sorted);
        
        double p50 = sorted[(int) (latencies.size() * 0.5)];
        double p95 = sorted[(int) (latencies.size() * 0.95)];
        double p99 = sorted[(int) (latencies.size() * 0.99)];
        
        return new LatencyStats(average, min, max, p50, p95, p99);
    }

    // Helper classes for benchmark statistics
    private static class BenchmarkStats {
        final double average;
        final double min;
        final double max;
        final double p50;
        final double p95;
        final double p99;
        final double standardDeviation;
        
        BenchmarkStats(double average, double min, double max, double p50, double p95, double p99, double standardDeviation) {
            this.average = average;
            this.min = min;
            this.max = max;
            this.p50 = p50;
            this.p95 = p95;
            this.p99 = p99;
            this.standardDeviation = standardDeviation;
        }
    }

    private static class LatencyStats {
        final double average;
        final double min;
        final double max;
        final double p50;
        final double p95;
        final double p99;
        
        LatencyStats(double average, double min, double max, double p50, double p95, double p99) {
            this.average = average;
            this.min = min;
            this.max = max;
            this.p50 = p50;
            this.p95 = p95;
            this.p99 = p99;
        }
    }
}
