package virtualthreads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Integration tests for Virtual Threads and Traditional Threads
 * Tests real-world scenarios and end-to-end workflows
 */
@DisplayName("Integration Tests")
class IntegrationTest {

    private static final String TEST_DATA_DIR = "test-data";
    private static final int TEST_FILE_COUNT = 100;
    private static final int TEST_FILE_SIZE = 1024 * 10; // 10KB per file

    @Test
    @DisplayName("Virtual Threads - File Processing Integration")
    void testVirtualThreadsFileProcessingIntegration() {
        System.out.println("\n=== Virtual Threads File Processing Integration ===");
        
        // Create test data directory
        Path testDir = Paths.get(TEST_DATA_DIR);
        try {
            Files.createDirectories(testDir);
            
            // Create test files
            for (int i = 0; i < TEST_FILE_COUNT; i++) {
                    Path file = testDir.resolve("test-file-" + i + ".txt");
                    String content = "Test file " + i + " content\n";
                    Files.write(file, content.getBytes());
                }
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    List<Future<FileProcessingResult>> futures = new ArrayList<>();
                    
                    // Process files concurrently
                    for (int i = 0; i < TEST_FILE_COUNT; i++) {
                        final int fileId = i;
                        futures.add(executor.submit(() -> {
                            Path file = testDir.resolve("test-file-" + fileId + ".txt");
                            
                            // Read file
                            String content = new String(Files.readAllBytes(file));
                            
                            // Process content
                            String processedContent = content.toUpperCase();
                            
                            // Write processed content back
                            Files.write(file, processedContent.getBytes());
                            
                            return new FileProcessingResult(fileId, content.length(), processedContent.length());
                        }));
                    }
                    
                    // Wait for all file processing to complete
                    int processedFiles = 0;
                    long totalOriginalSize = 0;
                    long totalProcessedSize = 0;
                    
                    for (Future<FileProcessingResult> future : futures) {
                        try {
                            FileProcessingResult result = future.get();
                            processedFiles++;
                            totalOriginalSize += result.originalSize;
                            totalProcessedSize += result.processedSize;
                        } catch (InterruptedException | ExecutionException e) {
                            fail("File processing failed: " + e.getMessage());
                        }
                    }
                    
                    long duration = System.currentTimeMillis() - startTime;
                    
                    System.out.printf("Processed %d files in %d ms%n", processedFiles, duration);
                    System.out.printf("Total Original Size: %d bytes%n", totalOriginalSize);
                    System.out.printf("Total Processed Size: %d bytes%n", totalProcessedSize);
                    
                    // Verify all files were processed
                    assertEquals(TEST_FILE_COUNT, processedFiles, "All files should be processed");
                    
                    // Verify content was processed (uppercase)
                    for (int i = 0; i < TEST_FILE_COUNT; i++) {
                        Path file = testDir.resolve("test-file-" + i + ".txt");
                        String content = new String(Files.readAllBytes(file));
                        assertTrue(content.contains("TEST FILE " + i + " CONTENT"), 
                            "File " + i + " should be processed");
                    }
                }
                
                // Cleanup
                try {
                    Files.walk(testDir)
                        .filter(path -> !path.equals(testDir))
                        .forEach(path -> Files.deleteIfExists(path));
                    Files.deleteIfExists(testDir);
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            } catch (IOException e) {
                    fail("Failed to create test directory: " + e.getMessage());
                }
        }
    }

    @Test
    @DisplayName("Traditional Threads - File Processing Integration")
    void testTraditionalThreadsFileProcessingIntegration() {
        System.out.println("\n=== Traditional Threads File Processing Integration ===");
        
        // Create test data directory
        Path testDir = Paths.get(TEST_DATA_DIR);
        try {
            Files.createDirectories(testDir);
            
            // Create test files
            for (int i = 0; i < TEST_FILE_COUNT; i++) {
                Path file = testDir.resolve("test-file-" + i + ".txt");
                String content = "Test file " + i + " content\n";
                Files.write(file, content.getBytes());
            }
                
                long startTime = System.currentTimeMillis();
                
                try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
                    List<Future<FileProcessingResult>> futures = new ArrayList<>();
                    
                    // Process files concurrently
                    for (int i = 0; i < TEST_FILE_COUNT; i++) {
                        final int fileId = i;
                        futures.add(executor.submit(() -> {
                            Path file = testDir.resolve("test-file-" + fileId + ".txt");
                            
                            // Read file
                            String content = new String(Files.readAllBytes(file));
                            
                            // Process content
                            String processedContent = content.toUpperCase();
                            
                            // Write processed content back
                            Files.write(file, processedContent.getBytes());
                            
                            return new FileProcessingResult(fileId, content.length(), processedContent.length());
                        }));
                    }
                    
                    // Wait for all file processing to complete
                    int processedFiles = 0;
                    long totalOriginalSize = 0;
                    long totalProcessedSize = 0;
                    
                    for (Future<FileProcessingResult> future : futures) {
                        try {
                            FileProcessingResult result = future.get();
                            processedFiles++;
                            totalOriginalSize += result.originalSize;
                            totalProcessedSize += result.processedSize;
                        } catch (InterruptedException | ExecutionException e) {
                            fail("File processing failed: " + e.getMessage());
                        }
                    }
                    
                    long duration = System.currentTimeMillis() - startTime;
                    
                    System.out.printf("Processed %d files in %d ms%n", processedFiles, duration);
                    System.out.printf("Total Original Size: %d bytes%n", totalOriginalSize);
                    System.out.printf("Total Processed Size: %d bytes%n", totalProcessedSize);
                    
                    // Verify all files were processed
                    assertEquals(TEST_FILE_COUNT, processedFiles, "All files should be processed");
                    
                    // Verify content was processed (uppercase)
                    for (int i = 0; i < TEST_FILE_COUNT; i++) {
                    Path file = testDir.resolve("test-file-" + i + ".txt");
                    String content = new String(Files.readAllBytes(file));
                    assertTrue(content.contains("TEST FILE " + i + " CONTENT"), 
                        "File " + i + " should be processed");
                }
            }
                
            // Cleanup
            try {
                Files.walk(testDir)
                    .filter(path -> !path.equals(testDir))
                    .forEach(path -> Files.deleteIfExists(path));
                Files.deleteIfExists(testDir);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        } catch (IOException e) {
            fail("Failed to create test directory: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Virtual Threads - Network I/O Integration")
    void testVirtualThreadsNetworkIOIntegration() {
        System.out.println("\n=== Virtual Threads Network I/O Integration ===");
        
        // Simulate network I/O operations
        String[] urls = {
            "https://httpbin.org/bytes/1024",
            "https://httpbin.org/bytes/2048",
            "https://httpbin.org/bytes/4096"
        };
        
        try (ExecutorService executor = executorService != null ? 
                Executors.newVirtualThreadPerTaskExecutor() : null) {
            List<Future<NetworkIOResult>> futures = new ArrayList<>();
            
            // Make network requests concurrently
            for (int i = 0; i < urls.length; i++) {
                final String url = urls[i];
                futures.add(executor.submit(() -> {
                    try {
                        // Simulate HTTP GET request
                        long startTime = System.currentTimeMillis();
                        
                        // In a real scenario, you would use HttpURLConnection or a library like OkHttp
                        // For this test, we'll simulate the network I/O
                        Thread.sleep(100 + (i * 50)); // Simulate network latency
                        
                        long endTime = System.currentTimeMillis();
                        
                        return new NetworkIOResult(url, endTime - startTime, 200, "OK");
                    } catch (Exception e) {
                        return new NetworkIOResult(url, 0, 0, "Error: " + e.getMessage());
                    }
                }));
            }
            
            // Wait for all network operations to complete
            int completedRequests = 0;
            long totalResponseTime = 0;
            
            for (Future<NetworkIOResult> future : futures) {
                try {
                    NetworkIOResult result = future.get();
                    completedRequests++;
                    totalResponseTime += result.responseTime;
                    System.out.printf("URL: %s, Status: %s, Time: %d ms%n", 
                        result.url, result.status, result.responseTime);
                } catch (InterruptedException | ExecutionException e) {
                    fail("Network request failed: " + e.getMessage());
                }
            }
            
            System.out.printf("Completed %d network requests in %d ms%n", completedRequests, totalResponseTime);
            System.out.printf("Average response time: %.2f ms%n", (double) totalResponseTime / completedRequests);
            
            assertTrue(completedRequests > 0, "Some network requests should complete");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Network I/O Integration")
    void testTraditionalThreadsNetworkIOIntegration() {
        System.out.println("\n=== Traditional Threads Network I/O Integration ===");
        
        // Simulate network I/O operations
        String[] urls = {
            "https://httpbin.org/bytes/1024",
            "https://httpbin.org/bytes/2048",
            "https://httpbin.org/bytes/4096"
        };
        
        try (ExecutorService executor = Executors.newFixedThreadPool(urls.length)) {
            List<Future<NetworkIOResult>> futures = new ArrayList<>();
            
            // Make network requests concurrently
            for (int i = 0; i < urls.length; i++) {
                final String url = urls[i];
                futures.add(executor.submit(() -> {
                    try {
                        // Simulate HTTP GET request
                        long startTime = System.currentTimeMillis();
                        
                        // In a real scenario, you would use HttpURLConnection or a library like OkHttp
                        // For this test, we'll simulate the network I/O
                        Thread.sleep(100 + (i * 50)); // Simulate network latency
                        
                        long endTime = System.currentTimeMillis();
                        
                        return new NetworkIOResult(url, endTime - startTime, 200, "OK");
                    } catch (Exception e) {
                        return new NetworkIOResult(url, 0, 0, "Error: " + e.getMessage());
                    }
                }));
            }
            
            // Wait for all network operations to complete
            int completedRequests = 0;
            long totalResponseTime = 0;
            
            for (Future<NetworkIOResult> future : futures) {
                try {
                    NetworkIOResult result = future.get();
                    completedRequests++;
                    totalResponseTime += result.responseTime;
                    System.out.printf("URL: %s, Status: %s, Time: %d ms%n", 
                        result.url, result.status, result.responseTime);
                } catch (InterruptedException | ExecutionException e) {
                    fail("Network request failed: " + e.getMessage());
                }
            }
            
            System.out.printf("Completed %d network requests in %d ms%n", completedRequests, totalResponseTime);
            System.out.printf("Average response time: %.2f ms%n", (double) totalResponseTime / completedRequests);
            
            assertTrue(completedRequests > 0, "Some network requests should complete");
        }
    }

    @Test
    @DisplayName("Virtual Threads - Database Simulation Integration")
    void testVirtualThreadsDatabaseSimulationIntegration() {
        System simulationDatabaseIntegration("Virtual Threads", Executors.newVirtualThreadPerTaskExecutor());
    }

    @Test
    @DisplayName("Traditional Threads - Database Simulation Integration")
    void testTraditionalThreadsDatabaseSimulationIntegration() {
        System simulationDatabaseIntegration("Traditional Threads", Executors.newFixedThreadPool(100));
    }

    @Test
    @DisplayName("Virtual Threads - Message Queue Integration")
    void testVirtualThreadsMessageQueueIntegration() {
        System.out.println("\n=== Virtual Threads Message Queue Integration ===");
        
        int messageCount = 1000;
        int producerCount = 10;
        int consumerCount = 10;
        int messagesPerProducer = 100;
        
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                final int producerId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < messagesPerProducer; j++) {
                        String message = "Producer-" + producerId + "-" + j;
                        messageQueue.offer(message);
                        producedCount.incrementAndGet();
                    }
                }));
            }
            
            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                    futures.add(executor.submit(() -> {
                        while (consumedCount.get() < producerCount * messagesPerProducer) {
                            String message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                            if (message != null) {
                                consumedCount.incrementAndGet();
                            }
                        }
                    }));
                }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for this test
                }
            }
            
            int expectedMessages = producerCount * messagesPerProducer;
            
            System.out.printf("Produced: %d messages%n", producedCount.get());
            System.out.printf("Consumed: %d messages%n", consumedCount.get());
            System.printf("Expected: %d messages%n", expectedMessages);
            
            assertEquals(expectedMessages, producedCount.get(), "All messages should be produced");
            assertEquals(expectedMessages, consumedCount.get(), "All messages should be consumed");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Message Queue Integration")
    void testTraditionalThreadsMessageQueueIntegration() {
        System.out.println("\n=== Traditional Threads Message Queue Integration ===");
        
        int messageCount = 1000;
        int producerCount = 10;
        int consumerCount = 10;
        int messagesPerProducer = 100;
        
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(producerCount + consumerCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                final int producerId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < messagesPerProducer; j++) {
                        String message = "Producer-" + producerId + "-" + j;
                        messageQueue.offer(message);
                        producedCount.incrementAndGet();
                    }
                }));
            }
            
            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                futures.add(executor.submit(() -> {
                    while (consumedCount.get() < producerCount * messagesPerProducer) {
                        String message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (message != null) {
                            consumedCount.incrementAndGet();
                        }
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for this test
                }
            }
            
            int expectedMessages = producerCount * messagesPerProducer;
            
            System.out.printf("Produced: %d messages%n", producedCount.get());
            System.out.printf("Consumed: %d messages%n", consumedCount.get());
            System.out.printf("Expected: %d messages%n", expectedMessages);
            
            assertEquals(expectedMessages, producedCount.get(), "All messages should be produced");
            assertEquals(expectedMessages, consumedCount.get(), "All messages should be consumed");
        }
    }

    @Test
    @DisplayName("Virtual Threads - Cache Simulation Integration")
    void testVirtualThreadsCacheSimulationIntegration() {
        System.out.println("\n=== Virtual Threads Cache Simulation Integration ===");
        
        int cacheSize = 1000;
        int operationCount = 10000;
        double hitRate = 0.8; // 80% hit rate
        
        CacheSimulation cache = new CacheSimulation(cacheSize, hitRate);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<CacheOperation>> futures = new ArrayList<>();
            
            // Create cache operations
            for (int i = 0; i < operationCount; i++) {
                final int operationId = i;
                futures.add(executor.submit(() -> {
                    if (Math.random() < hitRate) {
                        return cache.get("key-" + operationId);
                    } else {
                        return cache.put("key-" + operationId, "value-" + operationId);
                    }
                }));
            }
            
            // Wait for all operations to complete
            int hits = 0;
            int misses = 0;
            
            for (Future<CacheOperation> future : futures) {
                try {
                    CacheOperation result = future.get();
                    if (result.hit) {
                        hits++;
                    } else {
                        misses++;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for this test
                }
            }
            
            double actualHitRate = (double) hits / (hits + misses);
            
            System.out.printf("Operations: %d%n", operationCount);
            System.out.printf("Hits: %d%n", hits);
            System.out.printf("Misses: %d%n", misses);
            System.out.printf("Hit Rate: %.2f%%%n", actualHitRate * 100);
            System.out.printf("Expected Hit Rate: %.2f%%%n", hitRate * 100);
            
            // Hit rate should be close to expected
            assertTrue(Math.abs(actualHitRate - hitRate) < 0.1, 
                "Hit rate should be close to expected");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Cache Simulation Integration")
    void testTraditionalThreadsCacheSimulationIntegration() {
        System.out.println("\n=== Traditional Threads Cache Simulation Integration ===");
        
        int cacheSize = 1000;
        int operationCount = 1000;
        double hitRate = 0.8; // 80% hit rate
        
        CacheSimulation cache = new CacheSimulation(cacheSize, hitRate);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            List<Future<CacheOperation>> futures = new ArrayList<>();
            
            // Create cache operations
            for (int i = 0; i < operationCount; i++) {
                final int operationId = i;
                futures.add(executor.submit(() -> {
                    if (Math.random() < hitRate) {
                        return cache.get("key-" + operationId);
                    } else {
                        return cache.put("key-" + operationId, "value-" + operationId);
                    }
                }));
            }
            
            // Wait for all operations to complete
            int hits = 0;
            int misses = 0;
            
            for (Future<CacheOperation> future : futures) {
                try {
                    CacheOperation result = future.get();
                    if (result.hit) {
                        hits++;
                    } else {
                        misses++;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore exceptions for this test
                }
            }
            
            double actualHitRate = (double) hits / (hits + misses);
            
            System.out.printf("Operations: %d%n", operationCount);
            System.out.printf("Hits: %d%n", hits);
            System.out.printf("Misses: %d%n", misses);
            System.out.printf("Hit Rate: %.2f%%%n", actualHitRate * 100);
            System.out.printf("Expected Hit Rate: %.2f%%%n", hitRate * 100);
            
            // Hit rate should be close to expected
            assertTrue(Math.abs(actualHitRate - hitRate) < 0.1, 
                "Hit rate should be close to expected");
        }
    }

    @Nested
    @DisplayName("End-to-End Tests")
    class EndToEndTests {

        @Test
        @DisplayName("Virtual Threads - Complete Workflow Test")
        void testVirtualThreadsCompleteWorkflow() {
            System.out.println("\n=== Virtual Threads Complete Workflow Test ===");
            
            // Step 1: Data Preparation
            System.out.println("Step 1: Data Preparation");
            int dataItemCount = 1000;
            List<String> dataItems = new ArrayList<>();
            
            for (int i = 0; i < dataItemCount; i++) {
                dataItems.add("data-item-" + i);
            }
            
            // Step 2: Parallel Processing
            System.out.println("Step 2: Parallel Processing");
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<String>> futures = new ArrayList<>();
                
                for (int i = 0; i < dataItemCount; i++) {
                    final int itemId = i;
                    futures.add(executor.submit(() -> {
                        // Simulate data processing
                        String item = dataItems.get(itemId);
                        String processedItem = item.toUpperCase();
                        
                        // Simulate additional processing time
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        return processedItem;
                    }));
                }
                
                // Wait for all processing to complete
                List<String> processedItems = new ArrayList<>();
                for (Future<String> future : futures) {
                    try {
                        processedItems.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                long processingTime = System.currentTimeMillis() - startTime;
                
                System.out.printf("Processed %d items in %d ms%n", processedItems.size(), processingTime);
                
                // Verify all items were processed
                assertEquals(dataItemCount, processedItems.size(), "All items should be processed");
                
                // Verify processing was successful
                for (int i = 0; i < processedItems.size(); i++) {
                    String originalItem = dataItems.get(i);
                    String processedItem = processedItems.get(i);
                    assertEquals(originalItem.toUpperCase(), processedItem, 
                        "Item " + i + " should be processed");
                }
            }
            
            // Step 3: Aggregation
            System.out.println("Step 3: Aggregation");
            startTime = System.currentTimeMillis();
            
            AtomicInteger totalLength = new AtomicInteger(0);
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<Integer>> futures = new ArrayList<>();
                
                for (String item : processedItems) {
                    futures.add(executor.submit(() -> item.length()));
                }
                
                for (Future<Integer> future : futures) {
                    try {
                        totalLength.addAndGet(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                long aggregationTime = System.currentTimeMillis() - startTime;
                
                System.out.printf("Aggregated %d items in %d ms%n", totalLength.get(), aggregationTime);
                System.out.printf("Average item length: %.2f characters%n", (double) totalLength.get() / processedItems.size());
                
                // Calculate expected total length
                int expectedTotalLength = processedItems.stream().map(String::length).reduce(0, Integer::sum);
                assertEquals(expectedTotalLength, totalLength.get(), "Total length should match expected");
            }
            
            // Step 4: Validation
            System.out.println("Step 4: Validation");
            
            // Verify all steps completed successfully
            assertTrue(dataItemCount > 0, "Data items should be created");
            assertTrue(processedItems.size() > 0, "Items should be processed");
            assertTrue(totalLength.get() > 0, "Total length should be calculated");
            
            System.out.println("Complete workflow test completed successfully");
        }

        @Test
        @DisplayName("Traditional Threads - Complete Workflow Test")
        void testTraditionalThreadsCompleteWorkflow() {
            System.out.println("\n=== Traditional Threads Complete Workflow Test ===");
            
            // Step 1: Data Preparation
            System.out.println("Step 1: Data Preparation");
            int dataItemCount = 1000;
            List<String> dataItems = new ArrayList<>();
            
            for (int i = 0; i < dataItemCount; i++) {
                dataItems.add("data-item-" + i);
            }
            
            // Step 2: Parallel Processing
            System.out.println("Step 2: Parallel Processing");
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
                List<Future<String>> futures = new ArrayList<>();
                
                for (int i = 0; i < dataItemCount; i++) {
                    final int itemId = i;
                    futures.add(executor.submit(() -> {
                        // Simulate data processing
                        String item = dataItems.get(itemId);
                        String processedItem = item.toUpperCase();
                        
                        // Simulate additional processing time
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        return processedItem;
                    }));
                }
                
                // Wait for all processing to complete
                List<String> processedItems = new ArrayList<>();
                for (Future<String> future : futures) {
                    try {
                        processedItems.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                long processingTime = System.currentTimeMillis() - startTime;
                
                System.out.printf("Processed %d items in %d ms%n", processedItems.size(), processingTime);
                
                // Verify all items were processed
                assertEquals(dataItemCount, processedItems.size(), "All items should be processed");
                
                // Verify processing was successful
                for (int i = 0; i < processedItems.size(); i++) {
                    String originalItem = dataItems.get(i);
                    String processedItem = processedItems.get(i);
                    assertEquals(originalItem.toUpperCase(), processedItem, 
                        "Item " + i + " should be processed");
                }
            }
            
            // Step 3: Aggregation
            System.out.println("Step 3: Aggregation");
            startTime = System.currentTimeMillis();
            
            AtomicInteger totalLength = new AtomicInteger(0);
            
            try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
                List<Future<Integer>> futures = new ArrayList<>();
                
                for (String item : processedItems) {
                    futures.add(executor.submit(() -> item.length()));
                }
                
                for (Future<Integer> future : futures) {
                    try {
                        totalLength.addAndGet(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                long aggregationTime = System.currentTimeMillis() - startTime;
                
                System.out.printf("Aggregated %d items in %d ms%n", totalLength.get(), aggregationTime);
                System.out.printf("Average item length: %.2f characters%n", (double) totalLength.get() / processedItems.size());
                
                // Calculate expected total length
                int expectedTotalLength = processedItems.stream().map(String::length).reduce(0, Integer::sum);
                assertEquals(expectedTotalLength, totalLength.get(), "Total length should match expected");
            }
            
            // Step 4: Validation
            System.out.println("Step 4: Validation");
            
            // Verify all steps completed successfully
            assertTrue(dataItemCount > 0, "Data items should be created");
            assertTrue(processedItems.size() > 0, "Items should be processed");
            assertTrue(totalLength.get() > 0, "Total length should be calculated");
            
            System.out.println("Complete workflow test completed successfully");
        }
    }

    @Test
        @DisplayName("Virtual Threads - Error Recovery Test")
        void testVirtualThreadsErrorRecovery() {
            System.out.println("\n=== Virtual Threads Error Recovery Test ===");
            
            int taskCount = 1000;
            double errorRate = 0.1; // 10% error rate
            
            AtomicInteger successfulTasks = new AtomicInteger(0);
            AtomicInteger failedTasks = new AtomicInteger(0);
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<Boolean>> futures = new ArrayList<>();
                
                // Create tasks with potential errors
                for (int i = 0; i < taskCount; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        if (Math.random() < errorRate) {
                            failedTasks.incrementAndGet();
                            throw new RuntimeException("Simulated error for task " + taskId);
                        } else {
                            successfulTasks.incrementAndGet();
                        }
                        return true;
                    }));
                }
                
                // Wait for all tasks to complete
                for (Future<Boolean> future : futures) {
                    try {
                    future.get();
                } catch (ExecutionException e) {
                    failedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                double successRate = (double) successfulTasks.get() / (successfulTasks.get() + failedTasks.get()) * 100;
                
                System.out.printf("Total Tasks: %d%n", taskCount);
                System.out.printf("Successful Tasks: %d%n", successfulTasks.get());
                System.out.printf("Failed Tasks: %d%n", failedTasks.get());
                System.out.printf("Success Rate: %.2f%%%n", successRate);
                
                // Should handle errors gracefully
                assertTrue(successRate > 80, "Should handle errors gracefully");
            }
        }

        @Test
        @DisplayName("Traditional Threads - Error Recovery Test")
        void testTraditionalThreadsErrorRecovery() {
            System.out.println("\n=== Traditional Threads Error Recovery Test ===");
            
            int taskCount = 1000;
            double errorRate = 0.1; // 10% error rate
            
            AtomicInteger successfulTasks = new AtomicInteger(0);
            AtomicInteger failedTasks = new AtomicInteger(0);
            
            try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
                List<Future<Boolean>> futures = new ArrayList<>();
                
                // Create tasks with potential errors
                for (int i = 0; i < taskCount; i++) {
                    final int taskId = i;
                    futures.add(executor.submit(() -> {
                        if (Math.random() < errorRate) {
                            failedTasks.incrementAndGet();
                            throw new RuntimeException("Simulated error for task " + taskId);
                        } else {
                            successfulTasks.incrementAndGet();
                        }
                        return true;
                    }));
                }
                
                // Wait for all tasks to complete
                for (Future<Boolean> future : futures) {
                    try {
                    future.get();
                } catch (ExecutionException e) {
                    failedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                double successRate = (double) successfulTasks.get() / (successfulTasks.get() + failedTasks.get()) * 100;
                
                System.out.printf("Total Tasks: %d%n", taskCount);
                System.out.printf("Successful Tasks: %d%n", successfulTasks.get());
                System.out.printf("Failed Tasks: %d%n", failedTasks.get());
                System.out.printf("Success Rate: %.2f%%%n", successRate);
                
                // Should handle errors gracefully
                assertTrue(successRate > 80, "Should handle errors gracefully");
            }
        }
    }

    // Helper methods
    private void simulationDatabaseIntegration(String threadType, ExecutorService executor) {
        int recordCount = 1000;
        int queryCount = 100;
        
        System.out.printf("%s Database Simulation Test:%n", threadType);
        
        try {
            // Simulate database operations
            List<Future<DatabaseOperation>> futures = new ArrayList<>();
            
            // Create database records
            for (int i = 0; i < recordCount; i++) {
                final int recordId = i;
                futures.add(executor.submit(() -> {
                    // Simulate database insert
                    try {
                        Thread.sleep(5); // Simulate database latency
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return new DatabaseOperation("INSERT", recordId, "record-" + recordId);
                }));
            }
            
            // Simulate database queries
            for (int i = 0; i < queryCount; i++) {
                final int queryId = i;
                futures.add(executor.submit(() -> {
                    // Simulate database query
                    try {
                        Thread.sleep(3); // Simulate query latency
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return new DatabaseOperation("SELECT", queryId, "result-" + queryId);
                }));
            }
            
            // Wait for all operations to complete
            int completedOperations = 0;
            long totalLatency = 0;
            
            for (Future<DatabaseOperation> future : futures) {
                try {
                    DatabaseOperation operation = future.get();
                    completedOperations++;
                    totalLatency += operation.latency;
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some failures for this test
                }
            }
            
            System.out.printf("Completed %d operations in %d ms%n", completedOperations);
            System.out.printf("Average latency: %.2f ms%n", (double) totalLatency / completedOperations);
            
            // Verify all operations completed
            assertEquals(recordCount + queryCount, completedOperations, "All database operations should complete");
        } catch (Exception e) {
            System.out.printf("Database simulation failed: %s%n", e.getMessage());
        }
    }

    // Helper classes
    private static class FileProcessingResult {
        final int fileId;
        final int originalSize;
        final int processedSize;
        
        FileProcessingResult(int fileId, int originalSize, int processedSize) {
            this.fileId = fileId;
            this.originalSize = originalSize;
            this.processedSize = processedSize;
        }
    }

    private static class NetworkIOResult {
        final String url;
        final long responseTime;
        final int statusCode;
        final String status;
        
        NetworkIOResult(String url, long responseTime, int statusCode, String status) {
            this.url = url;
            this.responseTime = responseTime;
            this.statusCode = statusCode;
            this.status = status;
        }
    }

    private static class CacheSimulation {
        private final ConcurrentHashMap<String, String> cache;
        private final double hitRate;
        private final Random random = new Random();
        
        public CacheSimulation(int size, double hitRate) {
            this.cache = new ConcurrentHashMap<>(size);
            this.hitRate = hitRate;
            
            // Pre-populate cache
            for (int i = 0; i < size; i++) {
                cache.put("key-" + i, "value-" + i);
            }
        }
        
        public String get(String key) {
            if (random.nextDouble() < hitRate) {
                return cache.get(key);
            } else {
                return cache.put(key, "value-" + key);
            }
        }
        
        public String put(String key, String value) {
            return cache.put(key, value);
        }
    }

    private static class DatabaseOperation {
        final String operation;
        final int recordId;
        final String result;
        
        DatabaseOperation(String operation, int recordId, String result) {
            this.operation = operation;
            this.recordId = recordId;
            this.result = result;
        }
    }
}
