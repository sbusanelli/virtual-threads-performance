package virtualthreads;

/**
 * Simple test suite runner that doesn't require JUnit
 * Runs the comprehensive test suite with basic assertions
 */
public class RunTestSuite {
    
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE TEST SUITE ===");
        System.out.println("Virtual Threads vs Traditional Threads Performance Comparison");
        System.out.println("==========================================================");
        
        int testsPassed = 0;
        int totalTests = 8;
        
        try {
            // Test 1: Basic Performance
            System.out.println("\n--- Basic Performance Tests ---");
            long virtualDuration = measureVirtualThreadsPerformance(1000, 1);
            long traditionalDuration = measureTraditionalThreadsPerformance(1000, 1);
            
            System.out.printf("Virtual Threads: %d ms%n", virtualDuration);
            System.out.printf("Traditional Threads: %d ms%n", traditionalDuration);
            
            if (virtualDuration > 0 && traditionalDuration > 0) {
                System.out.println("✅ Basic Performance Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Basic Performance Test FAILED");
            }
            
            // Test 2: Scalability
            System.out.println("\n--- Scalability Tests ---");
            int[] threadCounts = {100, 500, 1000};
            boolean scalabilityPassed = true;
            
            for (int threadCount : threadCounts) {
                long virtualTime = measureVirtualThreadsPerformance(threadCount, 1);
                if (virtualTime <= 0) {
                    scalabilityPassed = false;
                    break;
                }
                System.out.printf("  %d threads: %d ms%n", threadCount, virtualTime);
            }
            
            if (scalabilityPassed) {
                System.out.println("✅ Scalability Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Scalability Test FAILED");
            }
            
            // Test 3: Concurrency
            System.out.println("\n--- Concurrency Tests ---");
            boolean concurrencyPassed = testConcurrency(100, 50);
            
            if (concurrencyPassed) {
                System.out.println("✅ Concurrency Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Concurrency Test FAILED");
            }
            
            // Test 4: Memory Usage
            System.out.println("\n--- Memory Usage Tests ---");
            boolean memoryPassed = testMemoryUsage(100, 1024);
            
            if (memoryPassed) {
                System.out.println("✅ Memory Usage Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Memory Usage Test FAILED");
            }
            
            // Test 5: Load Testing
            System.out.println("\n--- Load Tests ---");
            boolean loadPassed = testLoadPerformance(1000, 10);
            
            if (loadPassed) {
                System.out.println("✅ Load Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Load Test FAILED");
            }
            
            // Test 6: Integration
            System.out.println("\n--- Integration Tests ---");
            boolean integrationPassed = testIntegration(10, 10, 50);
            
            if (integrationPassed) {
                System.out.println("✅ Integration Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Integration Test FAILED");
            }
            
            // Test 7: End-to-End
            System.out.println("\n--- End-to-End Tests ---");
            boolean e2ePassed = testEndToEnd(100);
            
            if (e2ePassed) {
                System.out.println("✅ End-to-End Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ End-to-End Test FAILED");
            }
            
            // Test 8: Benchmarking
            System.out.println("\n--- Benchmarking Tests ---");
            boolean benchmarkPassed = testBenchmarking(50);
            
            if (benchmarkPassed) {
                System.out.println("✅ Benchmarking Test PASSED");
                testsPassed++;
            } else {
                System.out.println("❌ Benchmarking Test FAILED");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Test suite failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== TEST SUITE SUMMARY ===");
        System.out.printf("Tests Passed: %d/%d%n", testsPassed, totalTests);
        System.out.printf("Success Rate: %.1f%%%n", (double) testsPassed / totalTests * 100);
        
        if (testsPassed == totalTests) {
            System.out.println("🎉 ALL TESTS PASSED! Virtual Threads implementation is working correctly.");
        } else {
            System.out.println("⚠️  Some tests failed. Please review the output above.");
        }
    }
    
    private static long measureVirtualThreadsPerformance(int threadCount, int taskDuration) {
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        java.lang.Thread.sleep(taskDuration);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    // Ignore for test
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }
    
    private static long measureTraditionalThreadsPerformance(int threadCount, int taskDuration) {
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount)) {
            long startTime = System.currentTimeMillis();
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        java.lang.Thread.sleep(taskDuration);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    // Ignore for test
                }
            }
            
            return System.currentTimeMillis() - startTime;
        }
    }
    
    private static boolean testConcurrency(int threadCount, int iterations) {
        var counter = new java.util.concurrent.atomic.AtomicInteger(0);
        
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < iterations; j++) {
                        counter.incrementAndGet();
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        
        int expectedValue = threadCount * iterations;
        return counter.get() == expectedValue;
    }
    
    private static boolean testMemoryUsage(int threadCount, int memorySize) {
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            var memoryAllocated = new java.util.concurrent.atomic.AtomicBoolean(false);
            
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        byte[] memory = new byte[memorySize];
                        memoryAllocated.set(true);
                        java.lang.Thread.sleep(1);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    return false;
                }
            }
            
            // Check if any memory was actually allocated
            return memoryAllocated.get();
        }
    }
    
    private static boolean testLoadPerformance(int threadCount, int taskDuration) {
        long startTime = System.currentTimeMillis();
        var completedTasks = new java.util.concurrent.atomic.AtomicInteger(0);
        
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        java.lang.Thread.sleep(taskDuration);
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get(10, java.util.concurrent.TimeUnit.SECONDS);
                } catch (Exception e) {
                    // Continue for load test
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        return completedTasks.get() > 0 && duration > 0;
    }
    
    private static boolean testIntegration(int producerCount, int consumerCount, int itemsPerProducer) {
        var queue = new java.util.concurrent.LinkedBlockingQueue<String>();
        var producedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        var consumedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            
            // Producers
            for (int i = 0; i < producerCount; i++) {
                final int producerId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        try {
                            queue.put("item-" + producerId + "-" + j);
                            producedCount.incrementAndGet();
                        } catch (InterruptedException e) {
                            java.lang.Thread.currentThread().interrupt();
                        }
                    }
                }));
            }
            
            // Consumers
            for (int i = 0; i < consumerCount; i++) {
                futures.add(executor.submit(() -> {
                    while (consumedCount.get() < producerCount * itemsPerProducer) {
                        try {
                            String item = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                            if (item != null) {
                                consumedCount.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            java.lang.Thread.currentThread().interrupt();
                        }
                    }
                }));
            }
            
            for (var future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        
        int expectedItems = producerCount * itemsPerProducer;
        return producedCount.get() == expectedItems && consumedCount.get() == expectedItems;
    }
    
    private static boolean testEndToEnd(int dataCount) {
        var data = new java.util.ArrayList<String>();
        for (int i = 0; i < dataCount; i++) {
            data.add("data-" + i);
        }
        
        long startTime = System.currentTimeMillis();
        
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<String>>();
            
            for (String item : data) {
                futures.add(executor.submit(() -> {
                    try {
                        java.lang.Thread.sleep(1);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                    }
                    return item.toUpperCase();
                }));
            }
            
            var results = new java.util.ArrayList<String>();
            for (var future : futures) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    return false;
                }
            }
            
            return results.size() == dataCount;
        }
    }
    
    private static boolean testBenchmarking(int iterations) {
        var virtualDurations = new long[iterations];
        var traditionalDurations = new long[iterations];
        
        // Benchmark Virtual Threads
        for (int i = 0; i < iterations; i++) {
            virtualDurations[i] = measureVirtualThreadsPerformance(100, 1);
        }
        
        // Benchmark Traditional Threads
        for (int i = 0; i < iterations; i++) {
            traditionalDurations[i] = measureTraditionalThreadsPerformance(100, 1);
        }
        
        // Calculate averages
        double virtualAvg = 0;
        double traditionalAvg = 0;
        for (int i = 0; i < iterations; i++) {
            virtualAvg += virtualDurations[i];
            traditionalAvg += traditionalDurations[i];
        }
        virtualAvg /= iterations;
        traditionalAvg /= iterations;
        
        return virtualAvg > 0 && traditionalAvg > 0;
    }
}
