package virtualthreads;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Simple test runner to verify functionality without external dependencies
 */
public class SimpleTestRunner {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Simple Tests ===");
        
        testVirtualThreadExample();
        testTraditionalThreadExample();
        testThreadRunner();
        testEnhancedDemo();
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("✅ All tests passed!");
            System.exit(0);
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }
    
    private static void testVirtualThreadExample() {
        System.out.println("\n--- Testing VirtualThreadExample ---");
        
        try {
            // Capture output
            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            
            // Run the example
            VirtualThreadExample.main(new String[]{});
            
            // Restore original output
            System.setOut(originalOut);
            String output = outContent.toString();
            
            // Verify output contains expected elements
            boolean hasVirtualThreads = output.contains("Virtual Threads:");
            boolean hasTasksInfo = output.contains("tasks in");
            boolean hasTasksPerSec = output.contains("tasks/sec");
            boolean hasLoggerInfo = output.contains("INFO VirtualThreadExample");
            
            if (hasVirtualThreads && hasTasksInfo && hasTasksPerSec && hasLoggerInfo) {
                System.out.println("✅ VirtualThreadExample test passed");
                testsPassed++;
            } else {
                System.out.println("❌ VirtualThreadExample test failed");
                System.out.println("  Virtual Threads: " + hasVirtualThreads);
                System.out.println("  Tasks Info: " + hasTasksInfo);
                System.out.println("  Tasks/Sec: " + hasTasksPerSec);
                System.out.println("  Logger Info: " + hasLoggerInfo);
                testsFailed++;
            }
            
        } catch (Exception e) {
            System.out.println("❌ VirtualThreadExample test failed with exception: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testTraditionalThreadExample() {
        System.out.println("\n--- Testing TraditionalThreadExample ---");
        
        try {
            // Capture output
            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            
            // Run the example
            TraditionalThreadExample.main(new String[]{});
            
            // Restore original output
            System.setOut(originalOut);
            String output = outContent.toString();
            
            // Verify output contains expected elements
            boolean hasTraditionalThreads = output.contains("Traditional Threads:");
            boolean hasTasksInfo = output.contains("tasks in");
            boolean hasTasksPerSec = output.contains("tasks/sec");
            boolean hasLoggerInfo = output.contains("INFO TraditionalThreadExample");
            
            if (hasTraditionalThreads && hasTasksInfo && hasTasksPerSec && hasLoggerInfo) {
                System.out.println("✅ TraditionalThreadExample test passed");
                testsPassed++;
            } else {
                System.out.println("❌ TraditionalThreadExample test failed");
                System.out.println("  Traditional Threads: " + hasTraditionalThreads);
                System.out.println("  Tasks Info: " + hasTasksInfo);
                System.out.println("  Tasks/Sec: " + hasTasksPerSec);
                System.out.println("  Logger Info: " + hasLoggerInfo);
                testsFailed++;
            }
            
        } catch (Exception e) {
            System.out.println("❌ TraditionalThreadExample test failed with exception: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testThreadRunner() {
        System.out.println("\n--- Testing ThreadRunner ---");
        
        try {
            // Test virtual threads
            ThreadRunner.PerformanceResult virtualResult = ThreadRunner.runTasks(100, ThreadRunner.ThreadType.VIRTUAL, 10);
            boolean virtualSuccess = virtualResult.success() && virtualResult.durationMs() > 0;
            
            // Test traditional threads
            ThreadRunner.PerformanceResult traditionalResult = ThreadRunner.runTasks(100, ThreadRunner.ThreadType.TRADITIONAL, 10);
            boolean traditionalSuccess = traditionalResult.success() && traditionalResult.durationMs() > 0;
            
            // Test legacy method
            ThreadRunner.runTasks(50, true);
            ThreadRunner.runTasks(50, false);
            
            if (virtualSuccess && traditionalSuccess) {
                System.out.println("✅ ThreadRunner test passed");
                testsPassed++;
            } else {
                System.out.println("❌ ThreadRunner test failed");
                System.out.println("  Virtual Success: " + virtualSuccess);
                System.out.println("  Traditional Success: " + traditionalSuccess);
                testsFailed++;
            }
            
        } catch (Exception e) {
            System.out.println("❌ ThreadRunner test failed with exception: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testEnhancedDemo() {
        System.out.println("\n--- Testing EnhancedDemo ---");
        
        try {
            // Test configuration records
            EnhancedDemo.BenchmarkConfig defaultConfig = EnhancedDemo.BenchmarkConfig.defaultConfig();
            boolean configValid = defaultConfig.taskCount() > 0 && defaultConfig.taskDurationMs() > 0;
            
            // Test different configurations
            EnhancedDemo.BenchmarkConfig lightConfig = EnhancedDemo.BenchmarkConfig.lightLoad();
            EnhancedDemo.BenchmarkConfig heavyConfig = EnhancedDemo.BenchmarkConfig.heavyLoad();
            
            boolean configsValid = lightConfig.taskCount() > 0 && heavyConfig.taskCount() > 0;
            
            if (configValid && configsValid) {
                System.out.println("✅ EnhancedDemo test passed");
                testsPassed++;
            } else {
                System.out.println("❌ EnhancedDemo test failed");
                System.out.println("  Config Valid: " + configValid);
                System.out.println("  Configs Valid: " + configsValid);
                testsFailed++;
            }
            
        } catch (Exception e) {
            System.out.println("❌ EnhancedDemo test failed with exception: " + e.getMessage());
            testsFailed++;
        }
    }
}
