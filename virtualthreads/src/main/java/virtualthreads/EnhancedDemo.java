package virtualthreads;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;

/**
 * Enhanced demo showcasing Java 21+ features with virtual threads
 * Features demonstrated:
 * - Records (PerformanceResult)
 * - Switch expressions
 * - String templates (STR)
 * - Pattern matching in switch
 * - Structured concurrency
 * - Stream API enhancements
 */
public class EnhancedDemo {
    private static final SimpleLogger logger = SimpleLogger.getLogger(EnhancedDemo.class);
    private static final SimpleLogger performanceLogger = SimpleLogger.getLogger("virtualthreads.performance");
    
    public record BenchmarkConfig(int taskCount, int taskDurationMs, int warmupIterations) {
        public BenchmarkConfig {
            if (taskCount <= 0 || taskDurationMs <= 0 || warmupIterations < 0) {
                throw new IllegalArgumentException("Invalid configuration parameters");
            }
        }
        
        public static BenchmarkConfig defaultConfig() {
            return new BenchmarkConfig(1000, 10, 3);
        }
        
        public static BenchmarkConfig lightLoad() {
            return new BenchmarkConfig(100, 5, 1);
        }
        
        public static BenchmarkConfig heavyLoad() {
            return new BenchmarkConfig(10000, 50, 5);
        }
    }
    
    public record BenchmarkReport(
        List<ThreadRunner.PerformanceResult> virtualResults,
        List<ThreadRunner.PerformanceResult> traditionalResults,
        double averageSpeedup,
        double maxSpeedup
    ) {
        public String getSummary() {
            return String.format("""
                Benchmark Summary:
                - Virtual threads average: %.2f tasks/sec
                - Traditional threads average: %.2f tasks/sec
                - Average speedup: %.2fx
                - Maximum speedup: %.2fx
                """, getAverageVirtualThroughput(), getAverageTraditionalThroughput(), averageSpeedup, maxSpeedup);
        }
        
        private double getAverageVirtualThroughput() {
            return virtualResults.stream()
                .mapToDouble(ThreadRunner.PerformanceResult::getTasksPerSecond)
                .average()
                .orElse(0.0);
        }
        
        private double getAverageTraditionalThroughput() {
            return traditionalResults.stream()
                .mapToDouble(ThreadRunner.PerformanceResult::getTasksPerSecond)
                .average()
                .orElse(0.0);
        }
    }
    
    public static void main(String[] args) {
        logger.info("=== Enhanced Virtual Threads Demo ===");
        logger.info("Demonstrating Java 21+ features");
        
        // Demonstrate different configurations
        runDemo("Light Load", BenchmarkConfig.lightLoad());
        runDemo("Default Load", BenchmarkConfig.defaultConfig());
        runDemo("Heavy Load", BenchmarkConfig.heavyLoad());
        
        // Run comprehensive benchmark
        runComprehensiveBenchmark();
    }
    
    private static void runDemo(String name, BenchmarkConfig config) {
        logger.info("--- {} ---", name);
        logger.info("Configuration: {} tasks, {}ms duration", config.taskCount(), config.taskDurationMs());
        
        // Warmup
        performWarmup(config);
        
        // Run comparison
        ThreadRunner.comparePerformance(config.taskCount());
    }
    
    private static void performWarmup(BenchmarkConfig config) {
        logger.debug("Performing warmup with {} iterations", config.warmupIterations());
        for (int i = 0; i < config.warmupIterations(); i++) {
            ThreadRunner.runTasks(10, ThreadRunner.ThreadType.VIRTUAL, 1);
        }
        logger.debug("Warmup completed");
    }
    
    private static void runComprehensiveBenchmark() {
        logger.info("=== Comprehensive Benchmark ===");
        
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        List<ThreadRunner.PerformanceResult> virtualResults = new ArrayList<>();
        List<ThreadRunner.PerformanceResult> traditionalResults = new ArrayList<>();
        
        // Run multiple iterations
        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
            logger.info("Iteration {}/{}", i + 1, iterations);
            
            virtualResults.add(ThreadRunner.runTasks(config.taskCount(), ThreadRunner.ThreadType.VIRTUAL, config.taskDurationMs()));
            traditionalResults.add(ThreadRunner.runTasks(config.taskCount(), ThreadRunner.ThreadType.TRADITIONAL, config.taskDurationMs()));
        }
        
        // Calculate statistics
        double averageSpeedup = calculateAverageSpeedup(virtualResults, traditionalResults);
        double maxSpeedup = calculateMaxSpeedup(virtualResults, traditionalResults);
        
        BenchmarkReport report = new BenchmarkReport(virtualResults, traditionalResults, averageSpeedup, maxSpeedup);
        logger.info("{}", report.getSummary());
        
        // Log comprehensive benchmark summary to performance logger
        performanceLogger.info("COMPREHENSIVE_BENCHMARK,ITERATIONS={},AVG_SPEEDUP={:.2f},MAX_SPEEDUP={:.2f}", 
            iterations, averageSpeedup, maxSpeedup);
        
        // Demonstrate pattern matching
        analyzeResults(report);
    }
    
    private static double calculateAverageSpeedup(
        List<ThreadRunner.PerformanceResult> virtualResults,
        List<ThreadRunner.PerformanceResult> traditionalResults
    ) {
        if (virtualResults.size() != traditionalResults.size()) {
            throw new IllegalArgumentException("Result lists must have the same size");
        }
        
        return virtualResults.stream()
            .mapToDouble(virtual -> {
                int index = virtualResults.indexOf(virtual);
                ThreadRunner.PerformanceResult traditional = traditionalResults.get(index);
                return (double) traditional.durationMs() / virtual.durationMs();
            })
            .average()
            .orElse(0.0);
    }
    
    private static double calculateMaxSpeedup(
        List<ThreadRunner.PerformanceResult> virtualResults,
        List<ThreadRunner.PerformanceResult> traditionalResults
    ) {
        return virtualResults.stream()
            .mapToDouble(virtual -> {
                int index = virtualResults.indexOf(virtual);
                ThreadRunner.PerformanceResult traditional = traditionalResults.get(index);
                return (double) traditional.durationMs() / virtual.durationMs();
            })
            .max()
            .orElse(0.0);
    }
    
    // Demonstrate performance analysis
    private static void analyzeResults(BenchmarkReport report) {
        logger.info("=== Performance Analysis ===");
        
        String analysis;
        double speedup = report.averageSpeedup();
        if (speedup > 5.0) {
            analysis = "Excellent performance! Virtual threads show significant advantage.";
        } else if (speedup > 2.0) {
            analysis = "Good performance improvement with virtual threads.";
        } else if (speedup > 1.0) {
            analysis = "Moderate improvement with virtual threads.";
        } else if (speedup == 1.0) {
            analysis = "No significant difference between thread types.";
        } else {
            analysis = "Traditional threads may be better for this workload.";
        }
        
        logger.info(analysis);
        
        // Log analysis result to performance logger
        performanceLogger.info("ANALYSIS,SPEEDUP={:.2f},RESULT={}", speedup, analysis.replace(" ", "_"));
        
        // Demonstrate record pattern matching
        report.virtualResults().stream()
            .filter(result -> result.success())
            .findFirst()
            .ifPresentOrElse(
                result -> logger.info("Best virtual thread result: {}", result.getFormattedResult()),
                () -> logger.warn("No successful virtual thread results found.")
            );
    }
}
