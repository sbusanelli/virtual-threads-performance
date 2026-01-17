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
 * Regression tests for Virtual Threads and Traditional Threads
 * Ensures that performance and behavior remain consistent across different scenarios
 */
@DisplayName("Regression Tests")
class RegressionTest {

    private static final int REGRESSION_THREAD_COUNT = 1000;
    private static final int REGRESSION_ITERATIONS = 100;
    private static final long REGRESSION_TASK_DURATION_MS = 1;

    @Test
    @DisplayName("Virtual Threads - Performance Regression Test")
    void testVirtualThreadsPerformanceRegression() {
        System.out.println("\n=== Virtual Threads Performance Regression Test ===");
        
        // Baseline performance measurement
        double baselineThroughput = measureVirtualThreadsBaselineThroughput();
        
        // Current performance measurement
        double currentThroughput = measureVirtualThreadsCurrentThroughput();
        
        System.out.printf("Baseline Throughput: %.2f tasks/sec%n", baselineThroughput);
        System.out.printf("Current Throughput: %.2f tasks/sec%n", currentThroughput);
        
        // Performance should not degrade significantly (within 20% tolerance)
        double performanceRatio = currentThroughput / baselineThroughput;
        double tolerance = 0.8; // 80% tolerance
        
        System.out.printf("Performance Ratio: %.2f (Tolerance: %.2f)%n", performanceRatio, tolerance);
        
        assertTrue(performanceRatio >= tolerance, 
            String.format("Performance regression detected: %.2f%% degradation", (1 - performanceRatio) * 100));
    }

    @Test
    @DisplayName("Traditional Threads - Performance Regression Test")
    void testTraditionalThreadsPerformanceRegression() {
        System.out.println("\n=== Traditional Threads Performance Regression Test ===");
        
        // Baseline performance measurement
        double baselineThroughput = measureTraditionalThreadsBaselineThroughput();
        
        // Current performance measurement
        double currentThroughput = measureTraditionalThreadsCurrentThroughput();
        
        System.out.printf("Baseline Throughput: %.2f tasks/sec%n", baselineThroughput);
        System.out.printf("Current Throughput: %.2f tasks/sec%n", currentThroughput);
        
        // Performance should not degrade significantly (within 20% tolerance)
        double performanceRatio = currentThroughput / baselineThroughput;
        double tolerance = 0.8; // 80% tolerance
        
        System.out.printf("Performance Ratio: %.2f (Tolerance: %.2f)%n", performanceRatio, tolerance);
        
        assertTrue(performanceRatio >= tolerance, 
            String.format("Performance regression detected: %.2f%% degradation", (1 - performanceRatio) * 100));
    }

    @Test
    @DisplayName("Virtual Threads - Memory Usage Regression Test")
    void testVirtualThreadsMemoryUsageRegression() {
        System.out.println("\n=== Virtual Threads Memory Usage Regression Test ===");
        
        // Baseline memory measurement
        long baselineMemory = measureVirtualThreadsBaselineMemory();
        
        // Current memory measurement
        long currentMemory = measureVirtualThreadsCurrentMemory();
        
        System.out.printf("Baseline Memory: %d KB%n", baselineMemory / 1024);
        System.out.printf("Current Memory: %d KB%n", currentMemory / 1024);
        
        // Memory usage should not increase significantly (within 50% tolerance)
        double memoryRatio = (double) currentMemory / baselineMemory;
        double tolerance = 1.5; // 150% tolerance
        
        System.out.printf("Memory Ratio: %.2f (Tolerance: %.2f)%n", memoryRatio, tolerance);
        
        assertTrue(memoryRatio <= tolerance, 
            String.format("Memory usage regression detected: %.2f%% increase", (memoryRatio - 1) * 100));
    }

    @Test
    @DisplayName("Traditional Threads - Memory Usage Regression Test")
    void testTraditionalThreadsMemoryUsageRegression() {
        System.out.println("\n=== Traditional Threads Memory Usage Regression Test ===");
        
        // Baseline memory measurement
        long baselineMemory = measureTraditionalThreadsBaselineMemory();
        
        // Current memory measurement
        long currentMemory = measureTraditionalThreadsCurrentMemory();
        
        System.out.printf("Baseline Memory: %d KB%n", baselineMemory / 1024);
        System.out.printf("Current Memory: %d KB%n", currentMemory / 1024);
        
        // Memory usage should not increase significantly (within 50% tolerance)
        double memoryRatio = (double) currentMemory / baselineMemory;
        double tolerance = 1.5; // 150% tolerance
        
        System.out.printf("Memory Ratio: %.2f (Tolerance: %.2f)%n", memoryRatio, tolerance);
        
        assertTrue(memoryRatio <= tolerance, 
            String.format("Memory usage regression detected: %.2f%% increase", (memoryRatio - 1) * 100));
    }

    @Test
    @DisplayName("Virtual Threads - Latency Regression Test")
    void testVirtualThreadsLatencyRegression() {
        System.out.println("\n=== Virtual Threads Latency Regression Test ===");
        
        // Baseline latency measurement
        double baselineLatency = measureVirtualThreadsBaselineLatency();
        
        // Current latency measurement
        double currentLatency = measureVirtualThreadsCurrentLatency();
        
        System.out.printf("Baseline Latency: %.2f ms%n", baselineLatency);
        System.out.printf("Current Latency: %.2f ms%n", currentLatency);
        
        // Latency should not increase significantly (within 50% tolerance)
        double latencyRatio = currentLatency / baselineLatency;
        double tolerance = 1.5; // 150% tolerance
        
        System.out.printf("Latency Ratio: %.2f (Tolerance: %.2f)%n", latencyRatio, tolerance);
        
        assertTrue(latencyRatio <= tolerance, 
            String.format("Latency regression detected: %.2f%% increase", (latencyRatio - 1) * 100));
    }

    @Test
    @DisplayName("Traditional Threads - Latency Regression Test")
    void testTraditionalThreadsLatencyRegression() {
        System.out.println("\n=== Traditional Threads Latency Regression Test ===");
        
        // Baseline latency measurement
        double baselineLatency = measureTraditionalThreadsBaselineLatency();
        
        // Current latency measurement
        double currentLatency = measureTraditionalThreadsCurrentLatency();
        
        System.out.printf("Baseline Latency: %.2f ms%n", baselineLatency);
        System.out.printf("Current Latency: %.2f ms%n", currentLatency);
        
        // Latency should not increase significantly (within 50% tolerance)
        double latencyRatio = currentLatency / baselineLatency;
        double tolerance = 1.5; // 150% tolerance
        
        System.out.printf("Latency Ratio: %.2f (Tolerance: %.2f)%n", latencyRatio, tolerance);
        
        assertTrue(latencyRatio <= tolerance, 
            String.format("Latency regression detected: %.2f%% increase", (latencyRatio - 1) * 100));
    }

    @Test
    @DisplayName("Virtual Threads - Scalability Regression Test")
    void testVirtualThreadsScalabilityRegression() {
        System.out.println("\n=== Virtual Threads Scalability Regression Test ===");
        
        int[] threadCounts = {100, 500, 1000, 2000, 5000};
        
        for (int threadCount : threadCounts) {
            System.out.printf("Testing scalability with %d threads:%n", threadCount);
            
            // Baseline scalability measurement
            double baselineThroughput = measureVirtualThreadsBaselineScalability(threadCount);
            
            // Current scalability measurement
            double currentThroughput = measureVirtualThreadsCurrentScalability(threadCount);
            
            System.out.printf("  Baseline Throughput: %.2f tasks/sec%n", baselineThroughput);
            System.out.printf("  Current Throughput: %.2f tasks/sec%n", currentThroughput);
            
            // Scalability should not degrade significantly (within 30% tolerance)
            double scalabilityRatio = currentThroughput / baselineThroughput;
            double tolerance = 0.7; // 70% tolerance
            
            System.out.printf("  Scalability Ratio: %.2f (Tolerance: %.2f)%n", scalabilityRatio, tolerance);
            
            assertTrue(scalabilityRatio >= tolerance, 
                String.format("Scalability regression detected for %d threads: %.2f%% degradation", 
                    threadCount, (1 - scalabilityRatio) * 100));
        }
    }

    @Test
    @DisplayName("Traditional Threads - Scalability Regression Test")
    void testTraditionalThreadsScalabilityRegression() {
        System.out.println("\n=== Traditional Threads Scalability Regression Test ===");
        
        int[] threadCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
        
        for (int threadCount : threadCounts) {
            System.out.printf("Testing scalability with %d threads:%n", threadCount);
            
            // Baseline scalability measurement
            double baselineThroughput = measureTraditionalThreadsBaselineScalability(threadCount);
            
            // Current scalability measurement
            double currentThroughput = measureTraditionalThreadsCurrentScalability(threadCount);
            
            System.out.printf("  Baseline Throughput: %.2f tasks/sec%n", baselineThroughput);
            System.out.printf("  Current Throughput: %.2f tasks/sec%n", currentThroughput);
            
            // Scalability should not degrade significantly (within 30% tolerance)
            double scalabilityRatio = currentThroughput / baselineThroughput;
            double tolerance = 0.7; // 70% tolerance
            
            System.out.printf("  Scalability Ratio: %.2f (Tolerance: %.2f)%n", scalabilityRatio, tolerance);
            
            assertTrue(scalabilityRatio >= tolerance, 
                String.format("Scalability regression detected for %d threads: %.2f%% degradation", 
                    threadCount, (1 - scalabilityRatio) * 100));
        }
    }

    @Nested
    @DisplayName("Behavioral Regression Tests")
    class BehavioralRegressionTests {

        @Test
        @DisplayName("Virtual Threads - Thread Safety Regression Test")
        void testVirtualThreadsThreadSafetyRegression() {
            System.out.println("\n=== Virtual Threads Thread Safety Regression Test ===");
            
            // Test thread safety with concurrent counter
            ThreadSafeCounter counter = new ThreadSafeCounter();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create concurrent threads
                for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < REGRESSION_ITERATIONS; j++) {
                            counter.increment();
                        }
                    }));
                }
                
                // Wait for completion
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            int expectedValue = REGRESSION_THREAD_COUNT * REGRESSION_ITERATIONS;
            assertEquals(expectedValue, counter.get(), 
                "Thread safety regression detected: counter value incorrect");
            
            System.out.printf("Thread Safety Test Passed: %d (expected: %d)%n", counter.get(), expectedValue);
        }

        @Test
        @DisplayName("Traditional Threads - Thread Safety Regression Test")
        void testTraditionalThreadsThreadSafetyRegression() {
            System.out.println("\n=== Traditional Threads Thread Safety Regression Test ===");
            
            // Test thread safety with concurrent counter
            ThreadSafeCounter counter = new ThreadSafeCounter();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(REGRESSION_THREAD_COUNT)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create concurrent threads
                for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < REGRESSION_ITERATIONS; j++) {
                            counter.increment();
                        }
                    }));
                }
                
                // Wait for completion
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            int expectedValue = REGRESSION_THREAD_COUNT * REGRESSION_ITERATIONS;
            assertEquals(expectedValue, counter.get(), 
                "Thread safety regression detected: counter value incorrect");
            
            System.out.printf("Thread Safety Test Passed: %d (expected: %d)%n", counter.get(), expectedValue);
        }

        @Test
        @DisplayName("Virtual Threads - Exception Handling Regression Test")
        void testVirtualThreadsExceptionHandlingRegression() {
            System.out.println("\n=== Virtual Threads Exception Handling Regression Test ===");
            
            double errorRate = 0.1; // 10% error rate
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<Boolean>> futures = new ArrayList<>();
                
                // Create threads with potential exceptions
                for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        if (Math.random() < errorRate) {
                            throw new RuntimeException("Simulated error for task " + taskId);
                        }
                        return true;
                    }));
                }
                
                // Wait for completion and count exceptions
                int successCount = 0;
                int exceptionCount = 0;
                
                for (Future<Boolean> future : futures) {
                    try {
                        future.get();
                        successCount++;
                    } catch (ExecutionException e) {
                        exceptionCount++;
                    } catch (InterruptedException e) {
                        exceptionCount++;
                    }
                }
                
                double actualErrorRate = (double) exceptionCount / (successCount + exceptionCount);
                double tolerance = 0.15; // 15% tolerance around expected error rate
                
                System.out.printf("Success Count: %d%n", successCount);
                System.out.printf("Exception Count: %d%n", exceptionCount);
                System.out.printf("Actual Error Rate: %.2f%%%n", actualErrorRate * 100);
                System.out.printf("Expected Error Rate: %.2f%%%n", errorRate * 100);
                
                // Error rate should be within tolerance
                assertTrue(Math.abs(actualErrorRate - errorRate) <= tolerance, 
                    "Exception handling regression detected: error rate outside tolerance");
            }
        }

        @Test
        @DisplayName("Traditional Threads - Exception Handling Regression Test")
        void testTraditionalThreadsExceptionHandlingRegression() {
            System.out.println("\n=== Traditional Threads Exception Handling Regression Test ===");
            
            double errorRate = 0.1; // 10% error rate
            
            try (ExecutorService executor = Executors.newFixedThreadPool(REGRESSION_THREAD_COUNT)) {
                List<Future<Boolean>> futures = new ArrayList<>();
                
                // Create threads with potential exceptions
                for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        if (Math.random() < errorRate) {
                            throw new RuntimeException("Simulated error for task " + taskId);
                        }
                        return true;
                    }));
                }
                
                // Wait for completion and count exceptions
                int successCount = 0;
                int exceptionCount = 0;
                
                for (Future<Boolean> future : futures) {
                    try {
                        future.get();
                        successCount++;
                    } catch (ExecutionException e) {
                        exceptionCount++;
                    } catch (InterruptedException e) {
                        exceptionCount++;
                    }
                }
                
                double actualErrorRate = (double) exceptionCount / (successCount + exceptionCount);
                double tolerance = 0.15; // 15% tolerance around expected error rate
                
                System.out.printf("Success Count: %d%n", successCount);
                System.out.printf("Exception Count: %d%n", exceptionCount);
                System.out.printf("Actual Error Rate: %.2f%%%n", actualErrorRate * 100);
                System.out.printf("Expected Error Rate: %.2f%%%n", errorRate * 100);
                
                // Error rate should be within tolerance
                assertTrue(Math.abs(actualErrorRate - errorRate) <= tolerance, 
                    "Exception handling regression detected: error rate outside tolerance");
            }
        }

        @Test
        @DisplayName("Virtual Threads - Resource Cleanup Regression Test")
        void testVirtualThreadsResourceCleanupRegression() {
            System.out.println("\n=== Virtual Threads Resource Cleanup Regression Test ===");
            
            // Test resource cleanup with many short-lived threads
            int cleanupIterations = 100;
            
            for (int iteration = 0; iteration < cleanupIterations; iteration++) {
                System.out.printf("Cleanup Iteration %d/%d:%n", iteration + 1, cleanupIterations);
                
                // Measure memory before
                System.gc();
                long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                
                // Create and destroy many threads
                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    for (int i = 0; i < 1000; i++) {
                        futures.add(executor.submit(() -> {
                            // Allocate some memory
                            byte[] memory = new byte[1024];
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            // Memory will be garbage collected
                        }));
                    }
                    
                    // Wait for completion
                    for (Future<?> future : futures) {
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            // Ignore exceptions for cleanup test
                        }
                    }
                }
                
                // Measure memory after
                System.gc();
                Thread.sleep(100);
                long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                
                long memoryUsed = afterMemory - beforeMemory;
                
                System.out.printf("  Memory Used: %d KB%n", memoryUsed / 1024);
                
                // Memory usage should not grow significantly over iterations
                if (iteration > 0) {
                    // Allow some tolerance for memory fluctuations
                    assertTrue(memoryUsed < 10 * 1024 * 1024, // 10MB tolerance
                        "Resource cleanup regression detected: memory leak suspected");
                }
            }
        }

        @Test
        @DisplayName("Traditional Threads - Resource Cleanup Regression Test")
        void testTraditionalThreadsResourceCleanupRegression() {
            System.out.println("\n=== Traditional Threads Resource Cleanup Regression Test ===");
            
            // Test resource cleanup with many short-lived threads
            int cleanupIterations = 50; // Reduced for traditional threads
            
            for (int iteration = 0; iteration < cleanupIterations; iteration++) {
                System.out.printf("Cleanup Iteration %d/%d:%n", iteration + 1, cleanupIterations);
                
                // Measure memory before
                System.gc();
                long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                
                // Create and destroy many threads
                try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    for (int i = 0; i < 100; i++) {
                        futures.add(executor.submit(() -> {
                            // Allocate some memory
                            byte[] memory = new byte[1024];
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            // Memory will be garbage collected
                        }));
                    }
                    
                    // Wait for completion
                    for (Future<?> future : futures) {
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            // Ignore exceptions for cleanup test
                        }
                    }
                }
                
                // Measure memory after
                System.gc();
                Thread.sleep(100);
                long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                
                long memoryUsed = afterMemory - beforeMemory;
                
                System.out.printf("  Memory Used: %d KB%n", memoryUsed / 1024);
                
                // Memory usage should not grow significantly over iterations
                if (iteration > 0) {
                    // Allow some tolerance for memory fluctuations
                    assertTrue(memoryUsed < 10 * 1024 * 1024, // 10MB tolerance
                        "Resource cleanup regression detected: memory leak suspected");
                }
            }
        }
    }

    // Helper methods for regression testing
    private double measureVirtualThreadsBaselineThroughput() {
        // Simulate baseline measurement
        return 10000.0; // Placeholder: 10,000 tasks/sec
    }

    private double measureVirtualThreadsCurrentThroughput() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for throughput measurement
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            return (double) REGRESSION_THREAD_COUNT / (duration / 1000.0);
        }
    }

    private double measureTraditionalThreadsBaselineThroughput() {
        // Simulate baseline measurement
        return 5000.0; // Placeholder: 5,000 tasks/sec
    }

    private double measureTraditionalThreadsCurrentThroughput() {
        try (ExecutorService executor = Executors.newFixedThreadPool(REGRESSION_THREAD_COUNT)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for throughput measurement
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            return (double) REGRESSION_THREAD_COUNT / (duration / 1000.0);
        }
    }

    private long measureVirtualThreadsBaselineMemory() {
        // Simulate baseline memory measurement
        return 1024 * 1024; // Placeholder: 1MB
    }

    private long measureVirtualThreadsCurrentMemory() {
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    byte[] memory = new byte[1024];
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for memory measurement
                }
            }
        }
        
        System.gc();
        Thread.sleep(100);
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        return afterMemory - beforeMemory;
    }

    private long measureTraditionalThreadsBaselineMemory() {
        // Simulate baseline memory measurement
        return 2 * 1024 * 1024; // Placeholder: 2MB
    }

    private long measureTraditionalThreadsCurrentMemory() {
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(REGRESSION_THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < REGRESSION_THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    byte[] memory = new byte[1024];
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for memory measurement
                }
            }
        }
        
        System.gc();
        Thread.sleep(100);
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        return afterMemory - beforeMemory;
    }

    private double measureVirtualThreadsBaselineLatency() {
        // Simulate baseline latency measurement
        return 5.0; // Placeholder: 5ms
    }

    private double measureVirtualThreadsCurrentLatency() {
        List<Long> latencies = new ArrayList<>();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Long>> futures = new ArrayList<>();
            
            for (int i = 0; i < 100; i++) {
                futures.add(executor.submit(() -> {
                    long startTime = System.nanoTime();
                    
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for latency measurement
                }
            }
        }
        
        // Calculate average latency
        double sum = 0;
        for (Long latency : latencies) {
            sum += latency;
        }
        
        return sum / latencies.size() / 1_000_000.0; // Convert to milliseconds
    }

    private double measureTraditionalThreadsBaselineLatency() {
        // Simulate baseline latency measurement
        return 10.0; // Placeholder: 10ms
    }

    private double measureTraditionalThreadsCurrentLatency() {
        List<Long> latencies = new ArrayList<>();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            List<Future<Long>> futures = new ArrayList<>();
            
            for (int i = 0; i < 100; i++) {
                futures.add(executor.submit(() -> {
                    long startTime = System.nanoTime();
                    
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for latency measurement
                }
            }
        }
        
        // Calculate average latency
        double sum = 0;
        for (Long latency : latencies) {
            sum += latency;
        }
        
        return sum / latencies.size() / 1_000_000.0; // Convert to milliseconds
    }

    private double measureVirtualThreadsBaselineScalability(int threadCount) {
        // Simulate baseline scalability measurement
        return 10000.0 / threadCount; // Placeholder: inversely proportional to thread count
    }

    private double measureVirtualThreadsCurrentScalability(int threadCount) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for scalability measurement
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            return (double) threadCount / (duration / 1000.0);
        }
    }

    private double measureTraditionalThreadsBaselineScalability(int threadCount) {
        // Simulate baseline scalability measurement
        return 5000.0 / threadCount; // Placeholder: inversely proportional to thread count
    }

    private double measureTraditionalThreadsCurrentScalability(int threadCount) {
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(REGRESSION_TASK_DURATION_MS);
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
                    // Ignore exceptions for scalability measurement
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            return (double) threadCount / (duration / 1000.0);
        }
    }

    // Helper class for thread safety testing
    private static class ThreadSafeCounter {
        private int counter = 0;
        
        public synchronized void increment() {
            counter++;
        }
        
        public int get() {
            return counter;
        }
    }
}
