
package virtualthreads;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ThreadRunner {
    private static final SimpleLogger logger = SimpleLogger.getLogger(ThreadRunner.class);
    private static final SimpleLogger performanceLogger = SimpleLogger.getLogger("virtualthreads.performance");

    public record PerformanceResult(String threadType, long durationMs, int taskCount, boolean success) {
        public double getTasksPerSecond() {
            return durationMs > 0 ? (taskCount * 1000.0) / durationMs : 0;
        }
        
        public String getFormattedResult() {
            return String.format("%s Threads: %d tasks in %dms (%.2f tasks/sec)", 
                threadType, taskCount, durationMs, getTasksPerSecond());
        }
    }

    public enum ThreadType {
        VIRTUAL(true),
        TRADITIONAL(false);
        
        private final boolean useVirtualThreads;
        
        ThreadType(boolean useVirtualThreads) {
            this.useVirtualThreads = useVirtualThreads;
        }
        
        public boolean useVirtualThreads() {
            return useVirtualThreads;
        }
    }

    public static PerformanceResult runTasks(int numberOfTasks, ThreadType threadType) {
        return runTasks(numberOfTasks, threadType, 100);
    }

    public static PerformanceResult runTasks(int numberOfTasks, ThreadType threadType, int taskDurationMs) {
        logger.info("Starting {} tasks with {} threads, duration: {}ms", numberOfTasks, threadType, taskDurationMs);
        long start = System.currentTimeMillis();
        boolean success = true;

        try (ExecutorService executor = switch (threadType) {
            case VIRTUAL -> Executors.newVirtualThreadPerTaskExecutor();
            case TRADITIONAL -> Executors.newCachedThreadPool();
        }) {

            // Use structured concurrency with CompletableFuture
            @SuppressWarnings("unchecked")
            CompletableFuture<Void>[] futures = IntStream.range(0, numberOfTasks)
                .mapToObj(i -> CompletableFuture.runAsync(() -> simulateTask(taskDurationMs), executor))
                .toArray(CompletableFuture[]::new);

            // Wait for all tasks to complete
            CompletableFuture.allOf(futures).get(60, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            logger.error("Tasks timed out after 60 seconds");
            success = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Tasks were interrupted", e);
            success = false;
        } catch (ExecutionException e) {
            logger.error("Error executing tasks: {}", e.getCause().getMessage());
            success = false;
        }

        long end = System.currentTimeMillis();
        String threadTypeName = switch (threadType) {
            case VIRTUAL -> "Virtual";
            case TRADITIONAL -> "Traditional";
        };
        
        PerformanceResult result = new PerformanceResult(threadTypeName, end - start, numberOfTasks, success);
        
        // Log performance metrics to both regular and performance loggers
        logger.info(result.getFormattedResult());
        performanceLogger.info("THREAD_TYPE={},TASK_COUNT={},DURATION_MS={},TASKS_PER_SEC={}", 
            threadTypeName, numberOfTasks, result.durationMs(), String.format("%.2f", result.getTasksPerSecond()));
        
        return result;
    }

    // Legacy method for backward compatibility
    public static void runTasks(int numberOfTasks, boolean useVirtualThreads) {
        ThreadType threadType = useVirtualThreads ? ThreadType.VIRTUAL : ThreadType.TRADITIONAL;
        runTasks(numberOfTasks, threadType);
    }

    private static void simulateTask(int durationMs) {
        try {
            // Simulate IO-bound task with more realistic work
            Thread.sleep(durationMs);
            
            // Add some CPU work to make it more realistic
            long sum = 0;
            for (int i = 0; i < 1000; i++) {
                sum += i;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task interrupted", e);
        }
    }

    public static void comparePerformance(int numberOfTasks) {
        logger.info("=== Performance Comparison for {} tasks ===", numberOfTasks);
        
        PerformanceResult virtualResult = runTasks(numberOfTasks, ThreadType.VIRTUAL);
        PerformanceResult traditionalResult = runTasks(numberOfTasks, ThreadType.TRADITIONAL);
        
        if (virtualResult.success() && traditionalResult.success()) {
            double speedup = (double) traditionalResult.durationMs() / virtualResult.durationMs();
            logger.info("Virtual threads are {:.2f}x faster", speedup);
            
            double efficiency = virtualResult.getTasksPerSecond() / traditionalResult.getTasksPerSecond();
            logger.info("Efficiency improvement: {:.2f}x", efficiency);
            
            // Log comparison metrics to performance logger
            performanceLogger.info("COMPARISON,TASK_COUNT={},VIRTUAL_SPEEDUP={:.2f},EFFICIENCY={:.2f}", 
                numberOfTasks, speedup, efficiency);
        } else {
            logger.warn("Performance comparison failed - one or both tests did not complete successfully");
        }
    }
}
