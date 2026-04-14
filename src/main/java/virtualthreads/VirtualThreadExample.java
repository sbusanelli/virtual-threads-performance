package virtualthreads;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * Demonstrates virtual threads performance using enhanced Java 21+ features
 */
public class VirtualThreadExample {
    private static final SimpleLogger logger = SimpleLogger.getLogger(VirtualThreadExample.class);
    
    public static void main(String[] args) {
        logger.info("=== Virtual Threads Example ===");
        
        // Basic usage (backward compatible)
        ThreadRunner.runTasks(1000, true);
        
        // Enhanced usage with modern Java features
        logger.info("--- Enhanced Virtual Thread Demo ---");
        ThreadRunner.PerformanceResult result = ThreadRunner.runTasks(1000, ThreadRunner.ThreadType.VIRTUAL);
        
        // Demonstrate record pattern matching
        String analysis = switch (result) {
            case ThreadRunner.PerformanceResult(String threadType, long duration, int count, boolean success) 
                when success && duration < 1000 -> "Excellent performance: " + result.getFormattedResult();
            case ThreadRunner.PerformanceResult(String threadType, long duration, int count, boolean success) 
                when success -> "Good performance: " + result.getFormattedResult();
            default -> "Performance test failed";
        };
        
        logger.info("Analysis: {}", analysis);
    }
}
