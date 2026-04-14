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
 * Scalability tests for Virtual Threads and Traditional Threads
 * Tests performance under various load conditions, thread counts, and task complexities
 */
@DisplayName("Scalability Tests")
class ScalabilityTest {

    private static final int BASE_THREAD_COUNT = 100;
    private static final int BASE_ITERATIONS = 1000;
    private static final long TASK_DURATION_MS = 1;

    @Test
    @DisplayName("Virtual Threads - Linear Scalability Test")
    void testVirtualThreadsLinearScalability() {
        System.out.println("\n=== Virtual Threads Linear Scalability Test ===");
        
        int[] threadCounts = {100, 500, 1000, 2000, 5000, 10000};
        
        for (int threadCount : threadCounts) {
            System.out.printf("Testing with %d threads:%n", threadCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                    for (int i = 0; i < threadCount; i++) {
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
                            fail("Thread execution failed: " + e.getMessage());
                        }
                    }
                }
            
            long duration = System.currentTimeMillis() - startTime;
            double throughput = (double) threadCount / (duration / 1000.0);
            
            System.out.printf("  Duration: %d ms%n", duration);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            
            assertTrue(duration > 0, "Duration should be positive");
            assertTrue(throughput > 0, "Throughput should be positive");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Linear Scalability Test")
    void testTraditionalThreadsLinearScalability() {
        System.out.println("\n=== Traditional Threads Linear Scalability Test ===");
        
        int[] threadCounts = {100, 500, 1000, 2000}; // Limited for traditional threads
        
        for (int threadCount : threadCounts) {
            System.out.printf("Testing with %d threads:%n", threadCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                for (int i = 0; i < threadCount; i++) {
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
                        fail("Thread execution failed: " e.getMessage());
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            double throughput = (double) threadCount / (duration / 1000.0);
            
            System.out.printf("  Duration: %d ms%n", duration);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            
            assertTrue(duration > 0, "Duration should be positive");
            assertTrue(throughput > 0, "Throughput should be positive");
        }
    }

    @Test
    @DisplayName("Virtual Threads - Task Complexity Scalability")
    void testVirtualThreadsTaskComplexityScalability() {
        System.out.println("\n=== Virtual Threads Task Complexity Scalability Test ===");
        
        int[] complexities = {1, 10, 100, 1000, 10000}; // Number of operations per task
        
        for (int complexity : complexities) {
            System.out.printf("Testing with complexity %d operations:%n", complexity);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                for (int i = 0; i < BASE_THREAD_COUNT; i++) {
                    final int taskId = i;
                    final int taskComplexity = complexity;
                    futures.add(executor.submit(() -> {
                        // Simulate work with varying complexity
                        long sum = 0;
                        for (int j = 0; j < taskComplexity; j++) {
                            sum += j;
                        }
                        
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        return taskId * 1000000 + sum;
                    }));
                }
                
                // Wait for all tasks to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("  Duration: %d ms%n", duration);
            
            assertTrue(duration > 0, "Duration should be positive");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Task Complexity Scalability")
    void testTraditionalThreadsTaskComplexityScalability() {
        System.out.println("\n=== Traditional Threads Task Complexity Scalability Test ===");
        
        int[] complexities = {1, 10, 100, 1000, 10000}; // Number of operations per task
        
        for (int complexity : complexities) {
            System.out.printf("Testing with complexity %d operations:%n", complexity);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT)) {
                List<Future<?>> futures = new ArrayList<>();
                
                for (int i = 0; i < BASE_THREAD_COUNT; i++) {
                    final int taskId = i;
                    final int taskComplexity = complexity;
                    futures.add(executor.submit(() -> {
                        // Simulate work with varying complexity
                        long sum = 0;
                        for (int j = 0; j < taskComplexity; j++) {
                            sum += j;
                        }
                        
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        return taskId * 1000000 + sum;
                    }));
                }
                
                // Wait for all tasks to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("  Duration: %d ms%n", duration);
            
            assertTrue(duration > 0, "Duration should be positive");
        }
    }

    @Test
    @DisplayName("Virtual Threads - Burst Load Test")
    void testVirtualThreadsBurstLoad() {
        System.out.println("\n=== Virtual Threads Burst Load Test ===");
        
        int burstSize = 10000;
        int burstCount = 5;
        
        for (int burst = 0; burst < burstCount; burst++) {
            System.out.printf("Burst %d/%d:%n", burst + 1, burstCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                    
                    // Create burst of threads
                    for (int i = 0; i < burstSize; i++) {
                        futures.add(executor.submit(() -> {
                            try {
                                Thread.sleep(TASK_DURATION_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }));
                    }
                    
                    // Wait for burst to complete
                    for (Future<?> future : futures) {
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            // Ignore some failures during burst load
                        }
                    }
                }
            
            long duration = System.currentTimeMillis() - startTime;
            double throughput = (double) burstSize / (duration / 1000.0);
            
            System.out.printf("  Duration: %d ms%n", duration);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Burst Load Test")
    void testTraditionalThreadsBurstLoad() {
        System.out.println("\n=== Traditional Threads Burst Load Test ===");
        
        int burstSize = 1000; // Limited for traditional threads
        int burstCount = 5;
        
        for (int burst = 0; burst < burstCount; burst++) {
            System.out.printf("Burst %d/%d:%n", burst + 1, burstCount);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(burstSize)) {
                List<Future<?>> futures = new ArrayList<>();
                    
                    // Create burst of threads
                    for (int i = 0; i < burstSize; i++) {
                        futures.add(executor.submit(() -> {
                            try {
                                Thread.sleep(TASK_DURATION_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }));
                    }
                    
                    // Wait for burst to complete
                    for (Future<?> future : futures) {
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            // Ignore some failures during burst load
                        }
                    }
                }
            
            long duration = System.currentTimeMillis() - startTime;
            double throughput = (double) burstSize / (duration / 1000.0);
            
            System.out.printf("  Duration: %d ms%n", duration);
            System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Sustained Load Test")
    void testVirtualThreadsSustainedLoad() {
        System.out.println("\n=== Virtual Threads Sustained Load Test ===");
        
        int threadCount = 1000;
        int testDuration = 10000; // 10 seconds
        int taskInterval = 10; // Create new task every 10ms
        
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + testDuration;
            
            // Create sustained load
            while (System.currentTimeMillis() < endTime) {
                try {
                    Future<?> future = executor.submit(() -> {
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        completedTasks.incrementAndGet();
                    });
                    
                    // Don't wait for completion to maintain load
                    Thread.sleep(taskInterval);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            // Wait for remaining tasks to complete
            Thread.sleep(1000);
            
            long actualDuration = System.currentTimeMillis() - startTime;
            double actualThroughput = (double) completedTasks.get() / (actualDuration / 1000.0);
            
            System.out.printf("Test Duration: %d ms%n", actualDuration);
            System.out.printf("Completed Tasks: %d%n", completedTasks.get());
            System.out.printf("Actual Throughput: %.2f tasks/sec%n", actualThroughput);
            
            assertTrue(completedTasks.get() > 0, "Tasks should be completed");
            assertTrue(actualThroughput > 0, "Throughput should be positive");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Sustained Load Test")
    void testTraditionalThreadsSustainedLoad() {
        System.out.println("\n=== Traditional Threads Sustained Load Test ===");
        
        int threadCount = 100; // Limited for traditional threads
        int testDuration = 5000; // 5 seconds
        int taskInterval = 50; // Create new task every 50ms
        
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + testDuration;
            
            // Create sustained load
            while (System.currentTimeMillis() < endTime) {
                try {
                    Future<?> future = executor.submit(() -> {
                        try {
                            Thread.sleep(TASK_DURATION_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        completedTasks.incrementAndGet();
                    });
                    
                    // Don't wait for completion to maintain load
                    Thread.sleep(taskInterval);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            // Wait for remaining tasks to complete
            Thread.sleep(1000);
            
            long actualDuration = System.currentTimeMillis() - startTime;
            double actualThroughput = (double) completedTasks.get() / (actualDuration / 1000.0);
            
            System.out.printf("Test Duration: %d ms%n", actualDuration);
            System.out.printf("Completed Tasks: %d%n", completedTasks.get());
            System.out.printf("Actual Throughput: %.2f tasks/sec%n", actualThroughput);
            
            assertTrue(completedTasks.get() > 0, "Tasks should be completed");
            assertTrue(actualThroughput > 0, "Throughput should be positive");
        }
    }

    @Test
    @DisplayName("Virtual Threads - Resource Exhaustion Test")
    void testVirtualThreadsResourceExhaustion() {
        System.out.println("\n=== Virtual Threads Resource Exhaustion Test ===");
        
        // Test with extremely high thread count
        int extremeThreadCount = 100000;
        int shortIterations = 10;
        
        System.out.printf("Testing with %d extreme threads:%n", extremeThreadCount);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create extreme number of threads
            for (int i = 0; i < extremeThreadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for some threads to complete
            int completedCount = 0;
            for (Future<?> future : futures) {
                try {
                    future.get(100, TimeUnit.MILLISECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    // Expected for extreme load
                } catch (InterruptedException | ExecutionException e) {
                    // Expected for extreme load
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("Completed %d/%d threads in %d ms%n", completedCount, extremeThreadCount, duration);
            
            // Should handle extreme load gracefully
            assertTrue(completedCount > 0, "Some threads should complete even under extreme load");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Resource Exhaustion Test")
    void testTraditionalThreadsResourceExhaustion() {
        System.out.println("\n=== Traditional Threads Resource Exhaustion Test ===");
        
        // Test with high thread count (limited by system)
        int highThreadCount = 1000;
        int shortIterations = 10;
        
        System.out.printf("Testing with %d high threads:%n", highThreadCount);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(highThreadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create high number of threads
            for (int i = 0; i < highThreadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            
            // Wait for some threads to complete
            int completedCount = 0;
            for (Future<?> future : futures) {
                try {
                    future.get(5000, TimeUnit.MILLISECONDS);
                    completedCount++;
                } catch (TimeoutException e) {
                    // Expected for high load
                } catch (InterruptedException | ExecutionException e) {
                    // Expected for high load
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("Completed %d/%d threads in %d ms%n", completedCount, highThreadCount, duration);
            
            // Should handle high load gracefully
            assertTrue(completedCount > 0, "Some threads should complete even under high load");
        }
    }

    @Nested
    @DisplayName("Advanced Scalability Tests")
    class AdvancedScalabilityTests {

        @Test
        @DisplayName("Virtual Threads - Mixed Workload Test")
        void testVirtualThreadsMixedWorkload() {
            System.out.println("\n=== Virtual Threads Mixed Workload Test ===");
            
            // Different task types with varying durations
            TaskType[] taskTypes = {
                new TaskType("short", 1, 5000),
                new TaskType("medium", 10, 500),
                new TaskType("long", 100, 100)
            };
            
            for (TaskType taskType : taskTypes) {
                System.out.printf("Testing %s tasks (%dms duration):%n", taskType.name, taskType.duration);
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    int taskCount = BASE_THREAD_COUNT / taskTypes.length;
                    
                    for (int i = 0; i < taskCount; i++) {
                        futures.add(executor.submit(() -> {
                            try {
                                Thread.sleep(taskType.duration);
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
                            fail("Thread execution failed: " + e.getMessage());
                        }
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                double throughput = (double) taskCount / (duration / 1000.0);
                
                System.out.printf("  Duration: %d ms%n", duration);
                System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
                
                assertTrue(duration > 0, "Duration should be positive");
                assertTrue(throughput > 0, "Throughput should be positive");
            }
        }

        @Test
        @DisplayName("Traditional Threads - Mixed Workload Test")
        void testTraditionalThreadsMixedWorkload() {
            System.out.println("\n=== Traditional Threads Mixed Workload Test ===");
            
            // Different task types with varying durations
            TaskType[] taskTypes = {
                new TaskType("short", 1, 1000),
                new TaskType("medium", 10, 100),
                new TaskType("long", 100, 20)
            };
            
            for (TaskType taskType : taskTypes) {
                System.out.printf("Testing %s tasks (%dms duration):%n", taskType.name, taskType.duration);
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT)) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    int taskCount = BASE_THREAD_COUNT / taskTypes.length;
                    
                    for (int i = 0; i < taskCount; i++) {
                        futures.add(executor.submit(() -> {
                            try {
                                Thread.sleep(taskType.duration);
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
                            fail("Thread execution failed: " + e.getMessage());
                        }
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                double throughput = (double) taskCount / (duration / 1000.0);
                
                System.out.printf("  Duration: %d ms%n", duration);
                System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
                
                assertTrue(duration > 0, "Duration should be positive");
                assertTrue(throughput > 0, "Throughput should be positive");
            }
        }

        @Test
        @DisplayName("Virtual Threads - Adaptive Load Scaling")
        void testVirtualThreadsAdaptiveLoadScaling() {
            System.out.println("\n=== Virtual Threads Adaptive Load Scaling ===");
            
            // Start with low load and gradually increase
            int[] loadLevels = {100, 500, 1000, 2000, 5000, 10000};
            
            for (int loadLevel : loadLevels) {
                System.out.printf("Testing load level %d (%d threads):%n", loadLevel, loadLevel);
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    for (int i = 0; i < loadLevel; i++) {
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
                            // Ignore some failures during scaling
                        }
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                double throughput = (double) loadLevel / (duration / 1000.0);
                
                System.out.printf("  Duration: %d ms%n", duration);
                System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            }
        }

        @Test
        @DisplayName("Traditional Threads - Adaptive Load Scaling")
        void testTraditionalThreadsAdaptiveLoadScaling() {
            System.out.println("\n=== Traditional Threads Adaptive Load Scaling ===");
            
            // Start with low load and gradually increase
            int[] loadLevels = {100, 500, 1000, 2000}; // Limited for traditional threads
            
            for (int loadLevel : loadLevels) {
                System.out.printf("Testing load level %d (%d threads):%n", loadLevel, loadLevel);
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newFixedThreadPool(loadLevel)) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    for (int i = 0; i < loadLevel; i++) {
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
                            // Ignore some failures during scaling
                        }
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                double throughput = (double) loadLevel / (duration / 1000.0);
                
                System.out.printf("  Duration: %d ms%n", duration);
                System.out.printf("  Throughput: %.2f tasks/sec%n", throughput);
            }
        }
    }

    // Helper class for task types
    private static class TaskType {
        final String name;
        final int duration;
        final int count;
        
        TaskType(String name, int duration, int count) {
            this.name = name;
            this.duration = duration;
            this.count = count;
        }
    }
}
