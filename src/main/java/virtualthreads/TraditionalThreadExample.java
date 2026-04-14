package virtualthreads;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * Demonstrates traditional threads performance using enhanced Java 21+ features
 */
public class TraditionalThreadExample {
    private static final SimpleLogger logger = SimpleLogger.getLogger(TraditionalThreadExample.class);
    
    public static void main(String[] args) {
        logger.info("=== Traditional Threads Example ===");
        
        // Basic usage (backward compatible)
        ThreadRunner.runTasks(1000, false);
        
        // Enhanced usage with modern Java features
        logger.info("--- Enhanced Traditional Thread Demo ---");
        ThreadRunner.PerformanceResult result = ThreadRunner.runTasks(1000, ThreadRunner.ThreadType.TRADITIONAL);
        
        // Demonstrate record pattern matching
        String analysis = switch (result) {
            case ThreadRunner.PerformanceResult(String threadType, long duration, int count, boolean success) 
                when success -> "Performance: " + result.getFormattedResult();
            default -> "Performance test failed";
        };
        
        logger.info("Analysis: {}", analysis);
    }
}
