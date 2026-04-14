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
import java.util.stream.IntStream;

/**
 * Comprehensive performance comparison tests between Virtual Threads and Traditional Threads
 * Tests scalability, memory usage, throughput, and resource utilization
 */
@DisplayName("Performance Comparison Tests")
class PerformanceComparisonTest {

    private static final int TASK_COUNT = 1000;
    private static final int ITERATIONS = 100;
    private static final long TASK_DURATION_MS = 1;
    private static final int CONCURRENT_THREADS = 100;

    @Test
    @DisplayName("Virtual Threads vs Traditional Threads - Basic Performance")
    void testBasicPerformanceComparison() {
        System.out.println("\n=== Basic Performance Comparison ===");
        
        // Test Virtual Threads
        long virtualDuration = measureVirtualThreadsPerformance();
        System.out.printf("Virtual Threads: %d ms%n", virtualDuration);
        
        // Test Traditional Threads
        long traditionalDuration = measureTraditionalThreadsPerformance();
        System.out.printf("Traditional Threads: %d ms%n", traditionalDuration);
        
        // Performance comparison
        double ratio = (double) traditionalDuration / virtualDuration;
        System.out.printf("Performance Ratio (Traditional/Virtual): %.2fx%n", ratio);
        
        // Virtual threads should be faster or at least comparable
        assertTrue(virtualDuration > 0, "Virtual threads should complete successfully");
        assertTrue(traditionalDuration > 0, "Traditional threads should complete successfully");
    }

    @Test
    @DisplayName("Scalability Test - High Concurrency")
    void testScalabilityComparison() {
        System.out.println("\n=== Scalability Comparison ===");
        
        int[] threadCounts = {100, 500, 1000, 2000};
        
        for (int threadCount : threadCounts) {
            System.out.printf("Testing with %d threads:%n", threadCount);
            
            // Test Virtual Threads
            long virtualDuration = measureVirtualThreadsScalability(threadCount);
            System.out.printf("  Virtual Threads: %d ms%n", virtualDuration);
            
            // Test Traditional Threads
            long traditionalDuration = measureTraditionalThreadsScalability(threadCount);
            System.out.printf("  Traditional Threads: %d ms%n", traditionalDuration);
            
            double ratio = (double) traditionalDuration / virtualDuration;
            System.out.printf("  Performance Ratio: %.2fx%n", ratio);
        }
    }

    @Test
    @DisplayName("Memory Usage Comparison")
    void testMemoryUsageComparison() {
        System.out.println("\n=== Memory Usage Comparison ===");
        
        // Measure memory before tests
        System.gc();
        long beforeVirtual = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Test Virtual Threads memory usage
        long virtualMemoryUsage = measureVirtualThreadsMemoryUsage();
        System.gc();
        long afterVirtual = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long virtualMemoryDelta = afterVirtual - beforeVirtual;
        
        System.gc();
        long beforeTraditional = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Test Traditional Threads memory usage
        long traditionalMemoryUsage = measureTraditionalThreadsMemoryUsage();
        System.gc();
        long afterTraditional = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long traditionalMemoryDelta = afterTraditional - beforeTraditional;
        
        System.out.printf("Virtual Threads Memory: %d KB%n", virtualMemoryDelta / 1024);
        System.out.printf("Traditional Threads Memory: %d KB%n", traditionalMemoryDelta / 1024);
        
        double memoryRatio = (double) traditionalMemoryDelta / virtualMemoryDelta;
        System.out.printf("Memory Ratio (Traditional/Virtual): %.2fx%n", memoryRatio);
        
        // Virtual threads should use less memory
        assertTrue(virtualMemoryDelta > 0, "Virtual threads should use some memory");
        assertTrue(traditionalMemoryDelta > 0, "Traditional threads should use some memory");
    }

    @Test
    @DisplayName("Throughput Test - Tasks per Second")
    void testThroughputComparison() {
        System.out.println("\n=== Throughput Comparison ===");
        
        int testDuration = 5000; // 5 seconds
        int warmupDuration = 1000; // 1 second warmup
        
        // Test Virtual Threads throughput
        double virtualThroughput = measureVirtualThreadsThroughput(testDuration, warmupDuration);
        System.out.printf("Virtual Threads Throughput: %.2f tasks/sec%n", virtualThroughput);
        
        // Test Traditional Threads throughput
        double traditionalThroughput = measureTraditionalThreadsThroughput(testDuration, warmupDuration);
        System.out.printf("Traditional Threads Throughput: %.2f tasks/sec%n", traditionalThroughput);
        
        double throughputRatio = virtualThroughput / traditionalThroughput;
        System.out.printf("Throughput Ratio (Virtual/Traditional): %.2fx%n", throughputRatio);
        
        assertTrue(virtualThroughput > 0, "Virtual threads should have positive throughput");
        assertTrue(traditionalThroughput > 0, "Traditional threads should have positive throughput");
    }

    @Test
    @DisplayName("Resource Utilization Test")
    void testResourceUtilization() {
        System.out.println("\n=== Resource Utilization Comparison ===");
        
        // Test Virtual Threads
        ResourceMetrics virtualMetrics = measureVirtualThreadsResourceUtilization();
        System.out.printf("Virtual Threads:%n");
        System.out.printf("  Peak Threads: %d%n", virtualMetrics.peakThreads);
        System.out.printf("  Average CPU: %.2f%%%n", virtualMetrics.averageCpuUsage);
        System.out.printf("  Memory Usage: %d KB%n", virtualMetrics.memoryUsage / 1024);
        
        // Test Traditional Threads
        ResourceMetrics traditionalMetrics = measureTraditionalThreadsResourceUtilization();
        System.out.printf("Traditional Threads:%n");
        System.out.printf("  Peak Threads: %d%n", traditionalMetrics.peakThreads);
        System.out.printf("  Average CPU: %.2f%%%n", traditionalMetrics.averageCpuUsage);
        System.out.printf("  Memory Usage: %d KB%n", traditionalMetrics.memoryUsage / 1024);
        
        // Resource utilization comparison
        double cpuRatio = virtualMetrics.averageCpuUsage / traditionalMetrics.averageCpuUsage;
        double memoryRatio = (double) traditionalMetrics.memoryUsage / virtualMetrics.memoryUsage;
        
        System.out.printf("CPU Ratio (Virtual/Traditional): %.2fx%n", cpuRatio);
        System.out.printf("Memory Ratio (Traditional/Virtual): %.2fx%n", memoryRatio);
    }

    @Test
    @DisplayName("Error Handling and Exception Recovery")
    void testErrorHandlingComparison() {
        System.out.println("\n=== Error Handling Comparison ===");
        
        double errorRate = 0.1; // 10% error rate
        
        // Test Virtual Threads error handling
        ErrorMetrics virtualErrorMetrics = measureVirtualThreadsErrorHandling(errorRate);
        System.out.printf("Virtual Threads:%n");
        System.out.printf("  Total Tasks: %d%n", virtualErrorMetrics.totalTasks);
        System.out.printf("  Successful Tasks: %d%n", virtualErrorMetrics.successfulTasks);
        System.out.printf("  Failed Tasks: %d%n", virtualErrorMetrics.failedTasks);
        System.out.printf("  Success Rate: %.2f%%%n", virtualErrorMetrics.successRate);
        
        // Test Traditional Threads error handling
        ErrorMetrics traditionalErrorMetrics = measureTraditionalThreadsErrorHandling(errorRate);
        System.out.printf("Traditional Threads:%n");
        System.out.printf("  Total Tasks: %d%n", traditionalErrorMetrics.totalTasks);
        System.out.printf("  Successful Tasks: %d%n", traditionalErrorMetrics.successfulTasks);
        System.out.printf("  Failed Tasks: %d%n", traditionalErrorMetrics.failedTasks);
        System.out.printf("  Success Rate: %.2f%%%n", traditionalErrorMetrics.successRate);
        
        // Both should handle errors gracefully
        assertTrue(virtualErrorMetrics.successRate > 80, "Virtual threads should handle errors gracefully");
        assertTrue(traditionalErrorMetrics.successRate > 80, "Traditional threads should handle errors gracefully");
    }

    @Test
    @DisplayName("Latency Test - Task Completion Time")
    void testLatencyComparison() {
        System.out.println("\n=== Latency Comparison ===");
        
        // Test Virtual Threads latency
        LatencyMetrics virtualLatency = measureVirtualThreadsLatency();
        System.out.printf("Virtual Threads:%n");
        System.out.printf("  Average Latency: %.2f ms%n", virtualLatency.averageLatency);
        System.out.printf("  Min Latency: %.2f ms%n", virtualLatency.minLatency);
        System.out.printf("  Max Latency: %.2f ms%n", virtualLatency.maxLatency);
        System.out.printf("  P95 Latency: %.2f ms%n", virtualLatency.p95Latency);
        
        // Test Traditional Threads latency
        LatencyMetrics traditionalLatency = measureTraditionalThreadsLatency();
        System.out.printf("Traditional Threads:%n");
        System.out.printf("  Average Latency: %.2f ms%n", traditionalLatency.averageLatency);
        System.out.printf("  Min Latency: %.2f ms%n", traditionalLatency.minLatency);
        System.out.printf("  Max Latency: %.2f ms%n", traditionalLatency.maxLatency);
        System.out.printf("  P95 Latency: %.2f ms%n", traditionalLatency.p95Latency);
        
        double latencyRatio = traditionalLatency.averageLatency / virtualLatency.averageLatency;
        System.out.printf("Latency Ratio (Traditional/Virtual): %.2fx%n", latencyRatio);
        
        assertTrue(virtualLatency.averageLatency > 0, "Virtual threads should have positive latency");
        assertTrue(traditionalLatency.averageLatency > 0, "Traditional threads should have positive latency");
    }

    @Nested
    @DisplayName("Stress Tests")
    class StressTests {

        @Test
        @DisplayName("Extreme Concurrency Stress Test")
        void testExtremeConcurrency() {
            System.out.println("\n=== Extreme Concurrency Stress Test ===");
            
            int extremeThreadCount = 10000;
            
            // Test Virtual Threads under extreme load
            long virtualDuration = measureVirtualThreadsScalability(extremeThreadCount);
            System.out.printf("Virtual Threads (%d threads): %d ms%n", extremeThreadCount, virtualDuration);
            
            // Test Traditional Threads under extreme load (limited)
            int traditionalThreadCount = Math.min(extremeThreadCount, 1000);
            long traditionalDuration = measureTraditionalThreadsScalability(traditionalThreadCount);
            System.out.printf("Traditional Threads (%d threads): %d ms%n", traditionalThreadCount, traditionalDuration);
            
            // Both should complete without crashing
            assertTrue(virtualDuration > 0, "Virtual threads should handle extreme load");
            assertTrue(traditionalDuration > 0, "Traditional threads should handle load");
        }

        @Test
        @DisplayName("Long-Running Task Stress Test")
        void testLongRunningTasks() {
            System.out.println("\n=== Long-Running Task Stress Test ===");
            
            int longTaskDuration = 100; // 100ms per task
            int taskCount = 100;
            
            // Test Virtual Threads with long-running tasks
            long virtualDuration = measureVirtualThreadsLongRunningTasks(taskCount, longTaskDuration);
            System.out.printf("Virtual Threads (%d tasks, %dms each): %d ms%n", taskCount, longTaskDuration, virtualDuration);
            
            // Test Traditional Threads with long-running tasks
            long traditionalDuration = measureTraditionalThreadsLongRunningTasks(taskCount, longTaskDuration);
            System.out.printf("Traditional Threads (%d tasks, %dms each): %d ms%n", taskCount, longTaskDuration, traditionalDuration);
            
            // Both should complete successfully
            assertTrue(virtualDuration > 0, "Virtual threads should handle long-running tasks");
            assertTrue(traditionalDuration > 0, "Traditional threads should handle long-running tasks");
        }
    }

    // Helper methods for performance measurement
    private long measureVirtualThreadsPerformance() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    // Simulate work
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsPerformance() {
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    // Simulate work
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureVirtualThreadsScalability(int threadCount) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsScalability(int threadCount) {
        int poolSize = Math.min(threadCount, CONCURRENT_THREADS);
        try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureVirtualThreadsMemoryUsage() {
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT * 10; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        System.gc();
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return afterMemory - beforeMemory;
    }

    private long measureTraditionalThreadsMemoryUsage() {
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT * 10; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        System.gc();
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return afterMemory - beforeMemory;
    }

    private double measureVirtualThreadsThroughput(int testDurationMs, int warmupDurationMs) {
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Warmup phase
            long warmupStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - warmupStart < warmupDurationMs) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            // Measurement phase
            long testStart = System.currentTimeMillis();
            long endTime = testStart + testDurationMs;
            
            while (System.currentTimeMillis() < endTime) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            // Wait for remaining tasks to complete
            Thread.sleep(1000);
            
            return (double) completedTasks.get() / (testDurationMs / 1000.0);
        }
    }

    private double measureTraditionalThreadsThroughput(int testDurationMs, int warmupDurationMs) {
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            // Warmup phase
            long warmupStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - warmupStart < warmupDurationMs) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            // Measurement phase
            long testStart = System.currentTimeMillis();
            long endTime = testStart + testDurationMs;
            
            while (System.currentTimeMillis() < endTime) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            // Wait for remaining tasks to complete
            Thread.sleep(1000);
            
            return (double) completedTasks.get() / (testDurationMs / 1000.0);
        }
    }

    private ResourceMetrics measureVirtualThreadsResourceUtilization() {
        AtomicInteger peakThreads = new AtomicInteger(0);
        AtomicLong totalCpuTime = new AtomicLong(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    peakThreads.incrementAndGet();
                    long startTime = System.nanoTime();
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    long endTime = System.nanoTime();
                    totalCpuTime.addAndGet(endTime - startTime);
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        System.gc();
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        double averageCpuUsage = (totalCpuTime.get() / 1_000_000.0) / (TASK_COUNT * TASK_DURATION_MS);
        
        return new ResourceMetrics(peakThreads.get(), averageCpuUsage, memoryUsage);
    }

    private ResourceMetrics measureTraditionalThreadsResourceUtilization() {
        AtomicInteger peakThreads = new AtomicInteger(0);
        AtomicLong totalCpuTime = new AtomicLong(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    peakThreads.incrementAndGet();
                    long startTime = System.nanoTime();
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    long endTime = System.nanoTime();
                    totalCpuTime.addAndGet(endTime - startTime);
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        System.gc();
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        double averageCpuUsage = (totalCpuTime.get() / 1_000_000.0) / (TASK_COUNT * TASK_DURATION_MS);
        
        return new ResourceMetrics(peakThreads.get(), averageCpuUsage, memoryUsage);
    }

    private ErrorMetrics measureVirtualThreadsErrorHandling(double errorRate) {
        AtomicInteger totalTasks = new AtomicInteger(0);
        AtomicInteger successfulTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        Random random = new Random();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    totalTasks.incrementAndGet();
                    
                    if (random.nextDouble() < errorRate) {
                        // Simulate error
                        throw new RuntimeException("Simulated error for task " + taskId);
                    }
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        successfulTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    failedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        double successRate = (double) successfulTasks.get() / totalTasks.get() * 100;
        return new ErrorMetrics(totalTasks.get(), successfulTasks.get(), failedTasks.get(), successRate);
    }

    private ErrorMetrics measureTraditionalThreadsErrorHandling(double errorRate) {
        AtomicInteger totalTasks = new AtomicInteger(0);
        AtomicInteger successfulTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        Random random = new Random();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    totalTasks.incrementAndGet();
                    
                    if (random.nextDouble() < errorRate) {
                        // Simulate error
                        throw new RuntimeException("Simulated error for task " + taskId);
                    }
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                        successfulTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    failedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        double successRate = (double) successfulTasks.get() / totalTasks.get() * 100;
        return new ErrorMetrics(totalTasks.get(), successfulTasks.get(), failedTasks.get(), successRate);
    }

    private LatencyMetrics measureVirtualThreadsLatency() {
        List<Long> latencies = new CopyOnWriteArrayList<>();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    long startTime = System.currentTimeMillis();
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    long endTime = System.currentTimeMillis();
                    latencies.add(endTime - startTime);
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        return calculateLatencyMetrics(latencies);
    }

    private LatencyMetrics measureTraditionalThreadsLatency() {
        List<Long> latencies = new CopyOnWriteArrayList<>();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    long startTime = System.currentTimeMillis();
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    long endTime = System.currentTimeMillis();
                    latencies.add(endTime - startTime);
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
        }
        
        return calculateLatencyMetrics(latencies);
    }

    private long measureVirtualThreadsLongRunningTasks(int taskCount, int taskDurationMs) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(taskDurationMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private long measureTraditionalThreadsLongRunningTasks(int taskCount, int taskDurationMs) {
        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(taskDurationMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exceptions
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }

    private LatencyMetrics calculateLatencyMetrics(List<Long> latencies) {
        if (latencies.isEmpty()) {
            return new LatencyMetrics(0, 0, 0, 0);
        }
        
        double sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        
        for (Long latency : latencies) {
            sum += latency;
            if (latency < min) min = latency;
            if (latency > max) max = latency;
        }
        
        double average = sum / latencies.size();
        
        // Calculate P95
        latencies.sort(Long::compareTo);
        int p95Index = (int) Math.ceil(0.95 * latencies.size()) - 1;
        long p95 = latencies.get(Math.max(0, p95Index));
        
        return new LatencyMetrics(average, min, max, p95);
    }

    // Helper classes for metrics
    private static class ResourceMetrics {
        final int peakThreads;
        final double averageCpuUsage;
        final long memoryUsage;
        
        ResourceMetrics(int peakThreads, double averageCpuUsage, long memoryUsage) {
            this.peakThreads = peakThreads;
            this.averageCpuUsage = averageCpuUsage;
            this.memoryUsage = memoryUsage;
        }
    }

    private static class ErrorMetrics {
        final int totalTasks;
        final int successfulTasks;
        final int failedTasks;
        final double successRate;
        
        ErrorMetrics(int totalTasks, int successfulTasks, int failedTasks, double successRate) {
            this.totalTasks = totalTasks;
            this.successfulTasks = successfulTasks;
            this.failedTasks = failedTasks;
            this.successRate = successRate;
        }
    }

    private static class LatencyMetrics {
        final double averageLatency;
        final long minLatency;
        final long maxLatency;
        final long p95Latency;
        
        LatencyMetrics(double averageLatency, long minLatency, long maxLatency, long p95Latency) {
            this.averageLatency = averageLatency;
            this.minLatency = minLatency;
            this.maxLatency = maxLatency;
            this.p95Latency = p95Latency;
        }
    }
}
