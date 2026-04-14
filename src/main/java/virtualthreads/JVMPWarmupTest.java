package virtualthreads;

import virtualthreads.ThreadRunner;
import virtualthreads.SimpleLogger;

/**
 * Test to demonstrate JVM warming impact on virtual threads performance
 */
public class JVMPWarmupTest {
    
    public static void main(String[] args) {
        System.out.println("=== JVM Warming Performance Test ===");
        
        // Test without explicit warmup
        System.out.println("\n--- Testing WITHOUT JVM Warmup ---");
        ThreadRunner.PerformanceResult virtualNoWarmup = runTest(false);
        ThreadRunner.PerformanceResult traditionalNoWarmup = runTraditionalTest(false);
        
        // Test with explicit warmup
        System.out.println("\n--- Testing WITH JVM Warmup ---");
        ThreadRunner.PerformanceResult virtualWithWarmup = runTest(true);
        ThreadRunner.PerformanceResult traditionalWithWarmup = runTraditionalTest(true);
        
        // Performance comparison
        System.out.println("\n=== PERFORMANCE COMPARISON ===");
        System.out.println("Virtual Threads:");
        System.out.printf("  Without warmup: %d ms (%.2f tasks/sec)%n", 
            virtualNoWarmup.durationMs(), virtualNoWarmup.getTasksPerSecond());
        System.out.printf("  With warmup:    %d ms (%.2f tasks/sec)%n", 
            virtualWithWarmup.durationMs(), virtualWithWarmup.getTasksPerSecond());
        
        System.out.println("\nTraditional Threads:");
        System.out.printf("  Without warmup: %d ms (%.2f tasks/sec)%n", 
            traditionalNoWarmup.durationMs(), traditionalNoWarmup.getTasksPerSecond());
        System.out.printf("  With warmup:    %d ms (%.2f tasks/sec)%n", 
            traditionalWithWarmup.durationMs(), traditionalWithWarmup.getTasksPerSecond());
        
        // Calculate improvement percentages
        double virtualImprovement = ((double)(virtualNoWarmup.durationMs() - virtualWithWarmup.durationMs()) / virtualNoWarmup.durationMs()) * 100;
        double traditionalImprovement = ((double)(traditionalNoWarmup.durationMs() - traditionalWithWarmup.durationMs()) / traditionalNoWarmup.durationMs()) * 100;
        
        System.out.println("\n=== WARMUP IMPACT ===");
        System.out.printf("Virtual threads improvement: %.1f%%%n", virtualImprovement);
        System.out.printf("Traditional threads improvement: %.1f%%%n", traditionalImprovement);
        
        // Analysis
        System.out.println("\n=== ANALYSIS ===");
        if (virtualImprovement > 5 || traditionalImprovement > 5) {
            System.out.println("✅ JVM warming provides significant performance benefits");
        } else if (virtualImprovement > 2 || traditionalImprovement > 2) {
            System.out.println("📈 JVM warming provides moderate performance benefits");
        } else {
            System.out.println("📊 JVM warming provides minimal performance benefits");
        }
        
        System.out.println("\nKey Insights:");
        System.out.println("- JIT compilation optimization improves performance");
        System.out.println("- Thread pool initialization reduces startup overhead");
        System.out.println("- Memory allocation patterns are optimized");
        System.out.println("- Benefits may vary based on workload and JVM state");
    }
    
    private static ThreadRunner.PerformanceResult runTest(boolean useWarmup) {
        if (useWarmup) {
            // Run with warmup
            return ThreadRunner.runTasks(1000, ThreadRunner.ThreadType.VIRTUAL, 100);
        } else {
            // Run without warmup by calling a new method that skips warmup
            return runTasksWithoutWarmup(1000, ThreadRunner.ThreadType.VIRTUAL, 100);
        }
    }
    
    private static ThreadRunner.PerformanceResult runTraditionalTest(boolean useWarmup) {
        if (useWarmup) {
            return ThreadRunner.runTasks(1000, ThreadRunner.ThreadType.TRADITIONAL, 100);
        } else {
            return runTasksWithoutWarmup(1000, ThreadRunner.ThreadType.TRADITIONAL, 100);
        }
    }
    
    private static ThreadRunner.PerformanceResult runTasksWithoutWarmup(int numberOfTasks, ThreadRunner.ThreadType threadType, int taskDurationMs) {
        // This method bypasses the warmup to test cold JVM performance
        var logger = SimpleLogger.getLogger("cold-jvm-test");
        logger.info("Starting {} tasks with {} threads (COLD JVM), duration: {}ms", numberOfTasks, threadType, taskDurationMs);
        
        long start = System.currentTimeMillis();
        boolean success = true;

        try (var executor = switch (threadType) {
            case ThreadRunner.ThreadType.VIRTUAL -> java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
            case ThreadRunner.ThreadType.TRADITIONAL -> java.util.concurrent.Executors.newCachedThreadPool();
        }) {

            // Use structured concurrency with CompletableFuture
            @SuppressWarnings("unchecked")
            var futures = new java.util.concurrent.Future[numberOfTasks];

            for (int i = 0; i < numberOfTasks; i++) {
                final int taskId = i;
                futures[i] = executor.submit(() -> {
                    try {
                        // Simulate I/O-bound task
                        java.lang.Thread.sleep(taskDurationMs);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                });
            }

            // Wait for all tasks to complete with timeout
            try {
                for (var future : futures) {
                    future.get(60, java.util.concurrent.TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                logger.error("Task execution failed: {}", e.getMessage());
                success = false;
            }
        }

        long duration = System.currentTimeMillis() - start;
        var result = new ThreadRunner.PerformanceResult(threadType.toString(), duration, numberOfTasks, success);
        
        logger.info("Cold JVM {} Threads: {} tasks in {}ms ({:.2f} tasks/sec)", 
            threadType, numberOfTasks, duration, result.getTasksPerSecond());
        
        return result;
    }
}
