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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Load testing for Virtual Threads and Traditional Threads
 * Tests system behavior under high load conditions and stress scenarios
 */
@DisplayName("Load Tests")
class LoadTest {

    private static final int HIGH_THREAD_COUNT = 10000;
    private static final int EXTREME_THREAD_COUNT = 50000;
    private static final int STRESS_ITERATIONS = 1000;
    private static final long TASK_DURATION_MS = 1;

    @Test
    @DisplayName("Virtual Threads - High Load Test")
    void testVirtualThreadsHighLoad() {
        System.out.println("\n=== Virtual Threads High Load Test ===");
        
        int threadCount = HIGH_THREAD_COUNT;
        int iterations = 100;
        
        System.out.printf("Testing with %d threads, %d iterations each%n", threadCount, iterations);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create high load
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            // Simulate work
                            Thread.sleep(TASK_DURATION_MS);
                        }
                        completedTasks.incrementAndGet();
                    } catch (Exception e) {
                        failedTasks.incrementAndGet();
                    }
                }));
            }
            
            // Wait for completion with timeout
            int completedCount = 0;
            int failedCount = 0;
            
            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    failedCount++;
                } catch (InterruptedException | ExecutionException e) {
                    failedCount++;
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double throughput = (double) completedCount / (duration / 1000.0);
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d tasks%n", completedCount);
        System.out.printf("Failed: %d tasks%n", failedCount);
        System.out.printf("Throughput: %.2f tasks/sec%n", throughput);
        
        assertTrue(completedCount > 0, "Some tasks should complete");
        assertTrue(throughput > 0, "Throughput should be positive");
    }

    @Test
    @DisplayName("Traditional Threads - High Load Test")
    void testTraditionalThreadsHighLoad() {
        System.out.println("\n=== Traditional Threads High Load Test ===");
        
        int threadCount = 1000; // Limited for traditional threads
        int iterations = 100;
        
        System.out.printf("Testing with %d threads, %d iterations each%n", threadCount, iterations);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create high load
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            // Simulate work
                            Thread.sleep(TASK_DURATION_MS);
                        }
                        completedTasks.incrementAndGet();
                    } catch (Exception e) {
                        failedTasks.incrementAndGet();
                    }
                }));
            }
            
            // Wait for completion with timeout
            int completedCount = 0;
            int failedCount = 0;
            
            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    failedCount++;
                } catch (InterruptedException | ExecutionException e) {
                    failedCount++;
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double throughput = (double) completedCount / (duration / 1000.0);
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d tasks%n", completedCount);
        System.out.printf("Failed: %d tasks%n", failedCount);
        System.out.printf("Throughput: %.2f tasks/sec%n", throughput);
        
        assertTrue(completedCount > 0, "Some tasks should complete");
        assertTrue(throughput > 0, "Throughput should be positive");
    }

    @Test
    @DisplayName("Virtual Threads - Extreme Load Test")
    void testVirtualThreadsExtremeLoad() {
        System.out.println("\n=== Virtual Threads Extreme Load Test ===");
        
        int threadCount = EXTREME_THREAD_COUNT;
        int iterations = 10; // Reduced iterations for extreme load
        
        System.out.printf("Testing with %d threads, %d iterations each%n", threadCount, iterations);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create extreme load
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            // Minimal work
                            Thread.sleep(TASK_DURATION_MS);
                        }
                        completedTasks.incrementAndGet();
                    } catch (Exception e) {
                        failedTasks.incrementAndGet();
                    }
                }));
            }
            
            // Wait for completion with timeout
            int completedCount = 0;
            int failedCount = 0;
            
            for (Future<?> future : futures) {
                try {
                    future.get(60, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    failedCount++;
                } catch (InterruptedException | ExecutionException e) {
                    failedCount++;
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double throughput = (double) completedCount / (duration / 1000.0);
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d tasks%n", completedCount);
        System.out.printf("Failed: %d tasks%n", failedCount);
        System.out.printf("Throughput: %.2f tasks/sec%n", throughput);
        
        // Virtual threads should handle extreme load better
        assertTrue(completedCount > threadCount * 0.8, "Most tasks should complete even under extreme load");
    }

    @Test
    @DisplayName("Virtual Threads - Burst Load Test")
    void testVirtualThreadsBurstLoad() {
        System.out.println("\n=== Virtual Threads Burst Load Test ===");
        
        int burstCount = 10;
        int threadsPerBurst = 5000;
        int iterationsPerBurst = 50;
        
        System.out.printf("Testing %d bursts of %d threads, %d iterations each%n", 
            burstCount, threadsPerBurst, iterationsPerBurst);
        
        long totalStartTime = System.currentTimeMillis();
        AtomicInteger totalCompleted = new AtomicInteger(0);
        AtomicInteger totalFailed = new AtomicInteger(0);
        
        for (int burst = 0; burst < burstCount; burst++) {
            System.out.printf("Burst %d/%d:%n", burst + 1, burstCount);
            
            long burstStartTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create burst
                for (int i = 0; i < threadsPerBurst; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        try {
                            for (int j = 0; j < iterationsPerBurst; j++) {
                                Thread.sleep(TASK_DURATION_MS);
                            }
                            totalCompleted.incrementAndGet();
                        } catch (Exception e) {
                            totalFailed.incrementAndGet();
                        }
                    }));
                }
                
                // Wait for burst completion
                int burstCompleted = 0;
                int burstFailed = 0;
                
                for (Future<?> future : futures) {
                    try {
                        future.get(30, TimeUnit.SECONDS);
                        burstCompleted++;
                    } catch (TimeoutException e) {
                        burstFailed++;
                    } catch (InterruptedException | ExecutionException e) {
                        burstFailed++;
                    }
                }
                
                long burstDuration = System.currentTimeMillis() - burstStartTime;
                double burstThroughput = (double) burstCompleted / (burstDuration / 1000.0);
                
                System.out.printf("  Burst %d: %d completed, %d failed, %.2f tasks/sec%n", 
                    burst + 1, burstCompleted, burstFailed, burstThroughput);
            }
            
            // Small delay between bursts
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long totalDuration = System.currentTimeMillis() - totalStartTime;
        double totalThroughput = (double) totalCompleted.get() / (totalDuration / 1000.0);
        
        System.out.printf("Total Duration: %d ms%n", totalDuration);
        System.out.printf("Total Completed: %d tasks%n", totalCompleted.get());
        System.out.printf("Total Failed: %d tasks%n", totalFailed.get());
        System.out.printf("Total Throughput: %.2f tasks/sec%n", totalThroughput);
        
        assertTrue(totalCompleted.get() > 0, "Some tasks should complete");
    }

    @Test
    @DisplayName("Traditional Threads - Burst Load Test")
    void testTraditionalThreadsBurstLoad() {
        System.out.println("\n=== Traditional Threads Burst Load Test ===");
        
        int burstCount = 10;
        int threadsPerBurst = 1000; // Limited for traditional threads
        int iterationsPerBurst = 50;
        
        System.out.printf("Testing %d bursts of %d threads, %d iterations each%n", 
            burstCount, threadsPerBurst, iterationsPerBurst);
        
        long totalStartTime = System.currentTimeMillis();
        AtomicInteger totalCompleted = new AtomicInteger(0);
        AtomicInteger totalFailed = new AtomicInteger(0);
        
        for (int burst = 0; burst < burstCount; burst++) {
            System.out.printf("Burst %d/%d:%n", burst + 1, burstCount);
            
            long burstStartTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(threadsPerBurst)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create burst
                for (int i = 0; i < threadsPerBurst; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        try {
                            for (int j = 0; j < iterationsPerBurst; j++) {
                                Thread.sleep(TASK_DURATION_MS);
                            }
                            totalCompleted.incrementAndGet();
                        } catch (Exception e) {
                            totalFailed.incrementAndGet();
                        }
                    }));
                }
                
                // Wait for burst completion
                int burstCompleted = 0;
                int burstFailed = 0;
                
                for (Future<?> future : futures) {
                    try {
                        future.get(30, TimeUnit.SECONDS);
                        burstCompleted++;
                    } catch (TimeoutException e) {
                        burstFailed++;
                    } catch (InterruptedException | ExecutionException e) {
                        burstFailed++;
                    }
                }
                
                long burstDuration = System.currentTimeMillis() - burstStartTime;
                double burstThroughput = (double) burstCompleted / (burstDuration / 1000.0);
                
                System.out.printf("  Burst %d: %d completed, %d failed, %.2f tasks/sec%n", 
                    burst + 1, burstCompleted, burstFailed, burstThroughput);
            }
            
            // Small delay between bursts
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long totalDuration = System.currentTimeMillis() - totalStartTime;
        double totalThroughput = (double) totalCompleted.get() / (totalDuration / 1000.0);
        
        System.out.printf("Total Duration: %d ms%n", totalDuration);
        System.out.printf("Total Completed: %d tasks%n", totalCompleted.get());
        System.out.printf("Total Failed: %d tasks%n", totalFailed.get());
        System.out.printf("Total Throughput: %.2f tasks/sec%n", totalThroughput);
        
        assertTrue(totalCompleted.get() > 0, "Some tasks should complete");
    }

    @Test
    @DisplayName("Virtual Threads - Sustained Load Test")
    void testVirtualThreadsSustainedLoad() {
        System.out.println("\n=== Virtual Threads Sustained Load Test ===");
        
        int threadCount = 5000;
        int testDuration = 30000; // 30 seconds
        int taskInterval = 10; // Create new task every 10ms
        
        System.out.printf("Testing sustained load: %d threads for %d ms%n", threadCount, testDuration);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        AtomicBoolean running = new AtomicBoolean(true);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create sustained load
            while (System.currentTimeMillis() - startTime < testDuration) {
                if (running.get()) {
                    futures.add(executor.submit(() -> {
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                            completedTasks.incrementAndGet();
                        } catch (Exception e) {
                            failedTasks.incrementAndGet();
                        }
                    }));
                }
                
                try {
                    Thread.sleep(taskInterval);
                } catch (InterruptedException e) {
                    running.set(false);
                    break;
                }
            }
            
            // Wait for remaining tasks to complete
            System.out.println("Waiting for remaining tasks to complete...");
            
            int completedCount = 0;
            int failedCount = 0;
            
            for (Future<?> future : futures) {
                try {
                    future.get(10, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    failedCount++;
                } catch (InterruptedException | ExecutionException e) {
                    failedCount++;
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double throughput = (double) completedCount / (duration / 1000.0);
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d tasks%n", completedCount);
        System.out.printf("Failed: %d tasks%n", failedCount);
        System.out.printf("Throughput: %.2f tasks/sec%n", throughput);
        
        assertTrue(completedCount > 0, "Some tasks should complete");
        assertTrue(throughput > 0, "Throughput should be positive");
    }

    @Test
    @DisplayName("Traditional Threads - Sustained Load Test")
    void testTraditionalThreadsSustainedLoad() {
        System.out.println("\n=== Traditional Threads Sustained Load Test ===");
        
        int threadCount = 1000; // Limited for traditional threads
        int testDuration = 10000; // 10 seconds
        int taskInterval = 50; // Create new task every 50ms
        
        System.out.printf("Testing sustained load: %d threads for %d ms%n", threadCount, testDuration);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger failedTasks = new AtomicInteger(0);
        AtomicBoolean running = new AtomicBoolean(true);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create sustained load
            while (System.currentTimeMillis() - startTime < testDuration) {
                if (running.get()) {
                    futures.add(executor.submit(() -> {
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                            completedTasks.incrementAndGet();
                        } catch (Exception e) {
                            failedTasks.incrementAndGet();
                        }
                    }));
                }
                
                try {
                    Thread.sleep(taskInterval);
                } catch (InterruptedException e) {
                    running.set(false);
                    break;
                }
            }
            
            // Wait for remaining tasks to complete
            System.out.println("Waiting for remaining tasks to complete...");
            
            int completedCount = 0;
            int failedCount = 0;
            
            for (Future<?> future : futures) {
                try {
                    future.get(10, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    failedCount++;
                } catch (InterruptedException | ExecutionException e) {
                    failedCount++;
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double throughput = (double) completedCount / (duration / 1000.0);
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d tasks%n", completedCount);
        System.out.printf("Failed: %d tasks%n", failedCount);
        System.out.printf("Throughput: %.2f tasks/sec%n", throughput);
        
        assertTrue(completedCount > 0, "Some tasks should complete");
        assertTrue(throughput > 0, "Throughput should be positive");
    }

    @Test
    @DisplayName("Virtual Threads - Memory Stress Load Test")
    void testVirtualThreadsMemoryStressLoad() {
        System.out.println("\n=== Virtual Threads Memory Stress Load Test ===");
        
        int threadCount = 5000;
        int memorySize = 1024 * 10; // 10KB per thread
        
        System.out.printf("Testing memory stress: %d threads, %d bytes each%n", threadCount, memorySize);
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create memory stress load
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    // Allocate memory
                    byte[] memory = new byte[memorySize];
                    
                    // Fill memory with data
                    new Random().nextBytes(memory);
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Memory will be garbage collected when method returns
                }));
            }
            
            // Wait for completion
            int completedCount = 0;
            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    // Expected for memory stress test
                } catch (InterruptedException | ExecutionException e) {
                    // Expected for memory stress test
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Force garbage collection
        System.gc();
        Thread.sleep(1000);
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d threads%n", completedCount);
        System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
        System.out.printf("Memory Per Thread: %.2f KB%n", (double) memoryUsed / completedCount / 1024);
        
        assertTrue(completedCount > 0, "Some threads should complete");
    }

    @Test
    @DisplayName("Traditional Threads - Memory Stress Load Test")
    void testTraditionalThreadsMemoryStressLoad() {
        System.out.println("\n=== Traditional Threads Memory Stress Load Test ===");
        
        int threadCount = 1000; // Limited for traditional threads
        int memorySize = 1024 * 10; // 10KB per thread
        
        System.out.printf("Testing memory stress: %d threads, %d bytes each%n", threadCount, memorySize);
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create memory stress load
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    // Allocate memory
                    byte[] memory = new byte[memorySize];
                    
                    // Fill memory with data
                    new Random().nextBytes(memory);
                    
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Memory will be garbage collected when method returns
                }));
            }
            
            // Wait for completion
            int completedCount = 0;
            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    // Expected for memory stress test
                } catch (InterruptedException | ExecutionException e) {
                    // Expected for memory stress test
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Force garbage collection
        System.gc();
        Thread.sleep(1000);
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Duration: %d ms%n", duration);
        System.out.printf("Completed: %d threads%n", completedCount);
        System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
        System.out.printf("Memory Per Thread: %.2f KB%n", (double) memoryUsed / completedCount / 1024);
        
        assertTrue(completedCount > 0, "Some threads should complete");
    }

    @Nested
    @DisplayName("Stress Tests")
    class StressTests {

        @Test
        @DisplayName("Virtual Threads - Concurrent Resource Stress Test")
        void testVirtualThreadsConcurrentResourceStress() {
            System.out.println("\n=== Virtual Threads Concurrent Resource Stress Test ===");
            
            int threadCount = 10000;
            int resourceCount = 100;
            int operationsPerThread = 100;
            
            System.out.printf("Testing concurrent resource stress: %d threads, %d resources, %d ops each%n", 
                threadCount, resourceCount, operationsPerThread);
            
            ConcurrentResourceStress resourceStress = new ConcurrentResourceStress(resourceCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create concurrent resource stress
                for (int i = 0; i < threadCount; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < operationsPerThread; j++) {
                            int resourceId = (threadId * operationsPerThread + j) % resourceCount;
                            resourceStress.access(resourceId);
                        }
                    }));
                }
                
                // Wait for completion
                int completedCount = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get(60, TimeUnit.SECONDS);
                        completedCount++;
                    } catch (TimeoutException e) {
                        // Expected for stress test
                    } catch (InterruptedException | ExecutionException e) {
                        // Expected for stress test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.printf("Duration: %d ms%n", duration);
            System.out.printf("Completed: %d threads%n", completedCount);
            System.out.printf("Resource Integrity: %s%n", resourceStress.validateIntegrity());
            
            // Verify resource integrity
            assertTrue(resourceStress.validateIntegrity(), "Resource should maintain integrity under stress");
        }

        @Test
        @DisplayName("Traditional Threads - Concurrent Resource Stress Test")
        void testTraditionalThreadsConcurrentResourceStress() {
            System.out.println("\n=== Traditional Threads Concurrent Resource Stress Test ===");
            
            int threadCount = 1000; // Limited for traditional threads
            int resourceCount = 100;
            int operationsPerThread = 100;
            
            System.out.printf("Testing concurrent resource stress: %d threads, %d resources, %d ops each%n", 
                threadCount, resourceCount, operationsPerThread);
            
            ConcurrentResourceStress resourceStress = new ConcurrentResourceStress(resourceCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create concurrent resource stress
                for (int i = 0; i < threadCount; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < operationsPerThread; j++) {
                            int resourceId = (threadId * operationsPerThread + j) % resourceCount;
                            resourceStress.access(resourceId);
                        }
                    }));
                }
                
                // Wait for completion
                int completedCount = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get(60, TimeUnit.SECONDS);
                        completedCount++;
                    } catch (TimeoutException e) {
                        // Expected for stress test
                    } catch (InterruptedException | ExecutionException e) {
                        // Expected for stress test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.printf("Duration: %d ms%n", duration);
            System.out.printf("Completed: %d threads%n", completedCount);
            System.out.printf("Resource Integrity: %s%n", resourceStress.validateIntegrity());
            
            // Verify resource integrity
            assertTrue(resourceStress.validateIntegrity(), "Resource should maintain integrity under stress");
        }

        @Test
        @DisplayName("Virtual Threads - Lock Contention Stress Test")
        void testVirtualThreadsLockContentionStress() {
            System.out.println("\n=== Virtual Threads Lock Contention Stress Test ===");
            
            int threadCount = 5000;
            int lockOperations = 100;
            
            System.out.printf("Testing lock contention stress: %d threads, %d lock ops each%n", 
                threadCount, lockOperations);
            
            LockContentionStress lockStress = new LockContentionStress();
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create lock contention stress
                for (int i = 0; i < threadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < lockOperations; j++) {
                            lockStress.performLockOperation();
                        }
                    }));
                }
                
                // Wait for completion
                int completedCount = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get(60, TimeUnit.SECONDS);
                        completedCount++;
                    } catch (TimeoutException e) {
                        // Expected for stress test
                    } catch (InterruptedException | ExecutionException e) {
                        // Expected for stress test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.printf("Duration: %d ms%n", duration);
            System.out.printf("Completed: %d threads%n", completedCount);
            System.out.printf("Lock Operations: %d%n", lockStress.getOperationCount());
            
            // Verify lock operations completed
            assertTrue(lockStress.getOperationCount() > 0, "Some lock operations should complete");
        }

        @Test
        @DisplayName("Traditional Threads - Lock Contention Stress Test")
        void testTraditionalThreadsLockContentionStress() {
            System.out.println("\n=== Traditional Threads Lock Contention Stress Test ===");
            
            int threadCount = 1000; // Limited for traditional threads
            int lockOperations = 100;
            
            System.out.printf("Testing lock contention stress: %d threads, %d lock ops each%n", 
                threadCount, lockOperations);
            
            LockContentionStress lockStress = new LockContentionStress();
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create lock contention stress
                for (int i = 0; i < threadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < lockOperations; j++) {
                            lockStress.performLockOperation();
                        }
                    }));
                }
                
                // Wait for completion
                int completedCount = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get(60, TimeUnit.SECONDS);
                        completedCount++;
                    } catch (TimeoutException e) {
                        // Expected for stress test
                    } catch (InterruptedException | ExecutionException e) {
                        // Expected for stress test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.printf("Duration: %d ms%n", duration);
            System.out.printf("Completed: %d threads%n", completedCount);
            System.out.printf("Lock Operations: %d%n", lockStress.getOperationCount());
            
            // Verify lock operations completed
            assertTrue(lockStress.getOperationCount() > 0, "Some lock operations should complete");
        }
    }

    // Helper classes for load testing
    private static class ConcurrentResourceStress {
        private final int[] resources;
        private final ReentrantLock[] locks;
        private final AtomicInteger totalOperations = new AtomicInteger(0);
        
        public ConcurrentResourceStress(int resourceCount) {
            this.resources = new int[resourceCount];
            this.locks = new ReentrantLock[resourceCount];
            for (int i = 0; i < resourceCount; i++) {
                locks[i] = new ReentrantLock();
            }
        }
        
        public void access(int resourceId) {
            ReentrantLock lock = locks[resourceId];
            lock.lock();
            try {
                resources[resourceId]++;
                totalOperations.incrementAndGet();
            } finally {
                lock.unlock();
            }
        }
        
        public boolean validateIntegrity() {
            // Check that all resources have been accessed
            for (int resource : resources) {
                if (resource == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class LockContentionStress {
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicInteger operationCount = new AtomicInteger(0);
        
        public void performLockOperation() {
            lock.lock();
            try {
                // Simulate work under lock
                Thread.sleep(1);
                operationCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
        
        public int getOperationCount() {
            return operationCount.get();
        }
    }
}
