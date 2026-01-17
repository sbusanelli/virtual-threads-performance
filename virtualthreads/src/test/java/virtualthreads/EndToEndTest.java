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
import java.util.concurrent.ConcurrentHashMap;

/**
 * End-to-end tests for Virtual Threads and Traditional Threads
 * Tests complete workflows and real-world scenarios
 */
@DisplayName("End-to-End Tests")
class EndToEndTest {

    private static final int E2E_THREAD_COUNT = 1000;
    private static final int E2E_ITERATIONS = 100;
    private static final long E2E_TASK_DURATION_MS = 1;

    @Test
    @DisplayName("Virtual Threads - Complete Workflow E2E Test")
    void testVirtualThreadsCompleteWorkflowE2E() {
        System.out.println("\n=== Virtual Threads Complete Workflow E2E Test ===");
        
        // Step 1: Data Preparation
        System.out.println("Step 1: Data Preparation");
        int dataItemCount = 1000;
        List<String> dataItems = new ArrayList<>();
        
        for (int i = 0; i < dataItemCount; i++) {
            dataItems.add("data-item-" + i);
        }
        
        assertEquals(dataItemCount, dataItems.size(), "Data items should be prepared");
        
        // Step 2: Parallel Processing
        System.out.println("Step 2: Parallel Processing");
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();
            
            for (int i = 0; i < dataItemCount; i++) {
                final int itemId = i;
                futures.add(executor.submit(() -> {
                    String item = dataItems.get(itemId);
                    String processedItem = item.toUpperCase();
                    
                    // Simulate processing time
                    try {
                        Thread.sleep(E2E_TASK_DURATION_MS);
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
                    fail("Processing failed: " + e.getMessage());
                }
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            assertEquals(dataItemCount, processedItems.size(), "All items should be processed");
            
            // Verify processing was successful
            for (int i = 0; i < processedItems.size(); i++) {
                String originalItem = dataItems.get(i);
                String processedItem = processedItems.get(i);
                assertEquals(originalItem.toUpperCase(), processedItem, 
                    "Item " + i + " should be processed correctly");
            }
            
            System.out.printf("Processed %d items in %d ms%n", processedItems.size(), processingTime);
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
                    fail("Aggregation failed: " + e.getMessage());
                }
            }
            
            long aggregationTime = System.currentTimeMillis() - startTime;
            
            // Calculate expected total length
            int expectedTotalLength = processedItems.stream().map(String::length).reduce(0, Integer::sum);
            assertEquals(expectedTotalLength, totalLength.get(), "Total length should match expected");
            
            System.out.printf("Aggregated %d items in %d ms%n", totalLength.get(), aggregationTime);
            System.out.printf("Average item length: %.2f characters%n", (double) totalLength.get() / processedItems.size());
        }
        
        // Step 4: Validation
        System.out.println("Step 4: Validation");
        
        assertTrue(dataItemCount > 0, "Data items should be created");
        assertTrue(processedItems.size() > 0, "Items should be processed");
        assertTrue(totalLength.get() > 0, "Total length should be calculated");
        
        System.out.println("Complete workflow test completed successfully");
    }

    @Test
    @DisplayName("Traditional Threads - Complete Workflow E2E Test")
    void testTraditionalThreadsCompleteWorkflowE2E() {
        System.out.println("\n=== Traditional Threads Complete Workflow E2E Test ===");
        
        // Step 1: Data Preparation
        System.out.println("Step 1: Data Preparation");
        int dataItemCount = 1000;
        List<String> dataItems = new ArrayList<>();
        
        for (int i = 0; i < dataItemCount; i++) {
            dataItems.add("data-item-" + i);
        }
        
        assertEquals(dataItemCount, dataItems.size(), "Data items should be prepared");
        
        // Step 2: Parallel Processing
        System.out.println("Step 2: Parallel Processing");
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            List<Future<String>> futures = new ArrayList<>();
            
            for (int i = 0; i < dataItemCount; i++) {
                final int itemId = i;
                futures.add(executor.submit(() -> {
                    String item = dataItems.get(itemId);
                    String processedItem = item.toUpperCase();
                    
                    // Simulate processing time
                    try {
                        Thread.sleep(E2E_TASK_DURATION_MS);
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
                    fail("Processing failed: " + e.getMessage());
                }
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            assertEquals(dataItemCount, processedItems.size(), "All items should be processed");
            
            // Verify processing was successful
            for (int i = 0; i < processedItems.size(); i++) {
                String originalItem = dataItems.get(i);
                String processedItem = processedItems.get(i);
                assertEquals(originalItem.toUpperCase(), processedItem, 
                    "Item " + i + " should be processed correctly");
            }
            
            System.out.printf("Processed %d items in %d ms%n", processedItems.size(), processingTime);
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
                    fail("Aggregation failed: " + e.getMessage());
                }
            }
            
            long aggregationTime = System.currentTimeMillis() - startTime;
            
            // Calculate expected total length
            int expectedTotalLength = processedItems.stream().map(String::length).reduce(0, Integer::sum);
            assertEquals(expectedTotalLength, totalLength.get(), "Total length should match expected");
            
            System.out.printf("Aggregated %d items in %d ms%n", totalLength.get(), aggregationTime);
            System.out.printf("Average item length: %.2f characters%n", (double) totalLength.get() / processedItems.size());
        }
        
        // Step 4: Validation
        System.out.println("Step 4: Validation");
        
        assertTrue(dataItemCount > 0, "Data items should be created");
        assertTrue(processedItems.size() > 0, "Items should be processed");
        assertTrue(totalLength.get() > 0, "Total length should be calculated");
        
        System.out.println("Complete workflow test completed successfully");
    }

    @Test
    @DisplayName("Virtual Threads - Producer-Consumer Pipeline E2E Test")
    void testVirtualThreadsProducerConsumerPipelineE2E() {
        System.out.println("\n=== Virtual Threads Producer-Consumer Pipeline E2E Test ===");
        
        int producerCount = 10;
        int consumerCount = 10;
        int itemsPerProducer = 100;
        int totalItems = producerCount * itemsPerProducer;
        
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                final int producerId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        String item = "producer-" + producerId + "-item-" + j;
                        try {
                            queue.put(item);
                            producedCount.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
            }
            
            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                futures.add(executor.submit(() -> {
                    while (consumedCount.get() < totalItems) {
                        try {
                            String item = queue.poll(100, TimeUnit.MILLISECONDS);
                            if (item != null) {
                                // Process item
                                String processedItem = item.toUpperCase();
                                consumedCount.incrementAndGet();
                                processedCount.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some exceptions for E2E test
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        assertEquals(totalItems, producedCount.get(), "All items should be produced");
        assertEquals(totalItems, consumedCount.get(), "All items should be consumed");
        assertEquals(totalItems, processedCount.get(), "All items should be processed");
        
        System.out.printf("Pipeline completed in %d ms%n", duration);
        System.out.printf("Produced: %d, Consumed: %d, Processed: %d%n", 
            producedCount.get(), consumedCount.get(), processedCount.get());
        System.out.printf("Throughput: %.2f items/sec%n", (double) totalItems / (duration / 1000.0));
    }

    @Test
    @DisplayName("Traditional Threads - Producer-Consumer Pipeline E2E Test")
    void testTraditionalThreadsProducerConsumerPipelineE2E() {
        System.out.println("\n=== Traditional Threads Producer-Consumer Pipeline E2E Test ===");
        
        int producerCount = 10;
        int consumerCount = 10;
        int itemsPerProducer = 100;
        int totalItems = producerCount * itemsPerProducer;
        
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(producerCount + consumerCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                final int producerId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        String item = "producer-" + producerId + "-item-" + j;
                        try {
                            queue.put(item);
                            producedCount.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
            }
            
            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                futures.add(executor.submit(() -> {
                    while (consumedCount.get() < totalItems) {
                        try {
                            String item = queue.poll(100, TimeUnit.MILLISECONDS);
                            if (item != null) {
                                // Process item
                                String processedItem = item.toUpperCase();
                                consumedCount.incrementAndGet();
                                processedCount.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some exceptions for E2E test
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        assertEquals(totalItems, producedCount.get(), "All items should be produced");
        assertEquals(totalItems, consumedCount.get(), "All items should be consumed");
        assertEquals(totalItems, processedCount.get(), "All items should be processed");
        
        System.out.printf("Pipeline completed in %d ms%n", duration);
        System.out.printf("Produced: %d, Consumed: %d, Processed: %d%n", 
            producedCount.get(), consumedCount.get(), processedCount.get());
        System.out.printf("Throughput: %.2f items/sec%n", (double) totalItems / (duration / 1000.0));
    }

    @Test
    @DisplayName("Virtual Threads - Map-Reduce E2E Test")
    void testVirtualThreadsMapReduceE2E() {
        System.out.println("\n=== Virtual Threads Map-Reduce E2E Test ===");
        
        int dataCount = 10000;
        List<Integer> data = new ArrayList<>();
        
        // Generate test data
        for (int i = 0; i < dataCount; i++) {
            data.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Map phase: Square each number
            List<Future<Integer>> mapFutures = new ArrayList<>();
            
            for (Integer number : data) {
                mapFutures.add(executor.submit(() -> {
                    // Simulate computation
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return number * number;
                }));
            }
            
            // Collect map results
            List<Integer> mappedData = new ArrayList<>();
            for (Future<Integer> future : mapFutures) {
                try {
                    mappedData.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    fail("Map phase failed: " + e.getMessage());
                }
            }
            
            // Reduce phase: Sum all squared numbers
            AtomicInteger sum = new AtomicInteger(0);
            List<Future<?>> reduceFutures = new ArrayList<>();
            
            // Split data into chunks for parallel reduction
            int chunkSize = 100;
            for (int i = 0; i < mappedData.size(); i += chunkSize) {
                final int start = i;
                final int end = Math.min(i + chunkSize, mappedData.size());
                
                reduceFutures.add(executor.submit(() -> {
                    int chunkSum = 0;
                    for (int j = start; j < end; j++) {
                        chunkSum += mappedData.get(j);
                    }
                    sum.addAndGet(chunkSum);
                }));
            }
            
            // Wait for reduction to complete
            for (Future<?> future : reduceFutures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Reduce phase failed: " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Verify result
            int expectedSum = data.stream().mapToInt(n -> n * n).sum();
            assertEquals(expectedSum, sum.get(), "Map-Reduce result should be correct");
            
            System.out.printf("Map-Reduce completed in %d ms%n", duration);
            System.out.printf("Data size: %d, Result: %d%n", dataCount, sum.get());
        }
    }

    @Test
    @DisplayName("Traditional Threads - Map-Reduce E2E Test")
    void testTraditionalThreadsMapReduceE2E() {
        System.out.println("\n=== Traditional Threads Map-Reduce E2E Test ===");
        
        int dataCount = 1000; // Reduced for traditional threads
        List<Integer> data = new ArrayList<>();
        
        // Generate test data
        for (int i = 0; i < dataCount; i++) {
            data.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            // Map phase: Square each number
            List<Future<Integer>> mapFutures = new ArrayList<>();
            
            for (Integer number : data) {
                mapFutures.add(executor.submit(() -> {
                    // Simulate computation
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return number * number;
                }));
            }
            
            // Collect map results
            List<Integer> mappedData = new ArrayList<>();
            for (Future<Integer> future : mapFutures) {
                try {
                    mappedData.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    fail("Map phase failed: " + e.getMessage());
                }
            }
            
            // Reduce phase: Sum all squared numbers
            AtomicInteger sum = new AtomicInteger(0);
            List<Future<?>> reduceFutures = new ArrayList<>();
            
            // Split data into chunks for parallel reduction
            int chunkSize = 100;
            for (int i = 0; i < mappedData.size(); i += chunkSize) {
                final int start = i;
                final int end = Math.min(i + chunkSize, mappedData.size());
                
                reduceFutures.add(executor.submit(() -> {
                    int chunkSum = 0;
                    for (int j = start; j < end; j++) {
                        chunkSum += mappedData.get(j);
                    }
                    sum.addAndGet(chunkSum);
                }));
            }
            
            // Wait for reduction to complete
            for (Future<?> future : reduceFutures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Reduce phase failed: " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Verify result
            int expectedSum = data.stream().mapToInt(n -> n * n).sum();
            assertEquals(expectedSum, sum.get(), "Map-Reduce result should be correct");
            
            System.out.printf("Map-Reduce completed in %d ms%n", duration);
            System.out.printf("Data size: %d, Result: %d%n", dataCount, sum.get());
        }
    }

    @Nested
    @DisplayName("Complex Scenario Tests")
    class ComplexScenarioTests {

        @Test
        @DisplayName("Virtual Threads - Multi-Stage Pipeline E2E Test")
        void testVirtualThreadsMultiStagePipelineE2E() {
            System.out.println("\n=== Virtual Threads Multi-Stage Pipeline E2E Test ===");
            
            int itemCount = 1000;
            int stageCount = 5;
            
            BlockingQueue<String>[] stages = new BlockingQueue[stageCount];
            for (int i = 0; i < stageCount; i++) {
                stages[i] = new LinkedBlockingQueue<>();
            }
            
            AtomicInteger[] stageCounts = new AtomicInteger[stageCount];
            for (int i = 0; i < stageCount; i++) {
                stageCounts[i] = new AtomicInteger(0);
            }
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Stage 0: Input generator
                futures.add(executor.submit(() -> {
                    for (int i = 0; i < itemCount; i++) {
                        try {
                            stages[0].put("input-" + i);
                            stageCounts[0].incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
                
                // Intermediate stages: Processors
                for (int stage = 1; stage < stageCount; stage++) {
                    final int currentStage = stage;
                    final int previousStage = stage - 1;
                    
                    futures.add(executor.submit(() -> {
                        while (stageCounts[previousStage].get() < itemCount || !stages[previousStage].isEmpty()) {
                            try {
                                String item = stages[previousStage].poll(100, TimeUnit.MILLISECONDS);
                                if (item != null) {
                                    String processedItem = item + "-stage" + currentStage;
                                    stages[currentStage].put(processedItem);
                                    stageCounts[currentStage].incrementAndGet();
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }));
                }
                
                // Final stage: Output collector
                List<String> finalResults = new ArrayList<>();
                futures.add(executor.submit(() -> {
                    while (stageCounts[stageCount - 1].get() < itemCount || !stages[stageCount - 1].isEmpty()) {
                        try {
                            String item = stages[stageCount - 1].poll(100, TimeUnit.MILLISECONDS);
                            if (item != null) {
                                finalResults.add(item);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }));
                
                // Wait for all stages to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some exceptions for E2E test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            assertEquals(itemCount, finalResults.size(), "All items should reach final stage");
            
            // Verify pipeline processing
            for (String result : finalResults) {
                assertTrue(result.contains("stage4"), "Final results should contain stage4");
            }
            
            System.out.printf("Multi-stage pipeline completed in %d ms%n", duration);
            System.out.printf("Items processed: %d, Stages: %d%n", finalResults.size(), stageCount);
        }

        @Test
        @DisplayName("Virtual Threads - Concurrent Cache Simulation E2E Test")
        void testVirtualThreadsConcurrentCacheSimulationE2E() {
            System.out.println("\n=== Virtual Threads Concurrent Cache Simulation E2E Test ===");
            
            int cacheSize = 1000;
            int operationCount = 10000;
            int threadCount = 100;
            double hitRate = 0.8;
            
            ConcurrentCache cache = new ConcurrentCache(cacheSize);
            AtomicInteger hits = new AtomicInteger(0);
            AtomicInteger misses = new AtomicInteger(0);
            AtomicInteger puts = new AtomicInteger(0);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create cache operations
                for (int i = 0; i < threadCount; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        Random random = new Random(threadId);
                        
                        for (int j = 0; j < operationCount / threadCount; j++) {
                            String key = "key-" + random.nextInt(cacheSize * 2);
                            
                            if (random.nextDouble() < hitRate) {
                                // Get operation
                                String value = cache.get(key);
                                if (value != null) {
                                    hits.incrementAndGet();
                                } else {
                                    misses.incrementAndGet();
                                }
                            } else {
                                // Put operation
                                String value = "value-" + threadId + "-" + j;
                                cache.put(key, value);
                                puts.incrementAndGet();
                            }
                        }
                    }));
                }
                
                // Wait for all operations to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some exceptions for E2E test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            int totalOperations = hits.get() + misses.get() + puts.get();
            double actualHitRate = (double) hits.get() / (hits.get() + misses.get());
            
            System.out.printf("Cache simulation completed in %d ms%n", duration);
            System.out.printf("Total Operations: %d%n", totalOperations);
            System.out.printf("Hits: %d, Misses: %d, Puts: %d%n", hits.get(), misses.get(), puts.get());
            System.out.printf("Hit Rate: %.2f%%%n", actualHitRate * 100);
            System.out.printf("Cache Size: %d%n", cache.size());
            
            // Verify cache behavior
            assertTrue(totalOperations > 0, "Operations should be performed");
            assertTrue(cache.size() <= cacheSize, "Cache size should not exceed limit");
        }

        @Test
        @DisplayName("Virtual Threads - Distributed Task Simulation E2E Test")
        void testVirtualThreadsDistributedTaskSimulationE2E() {
            System.out.println("\n=== Virtual Threads Distributed Task Simulation E2E Test ===");
            
            int taskCount = 1000;
            int workerCount = 50;
            int taskDuration = 10; // ms
            
            TaskQueue taskQueue = new TaskQueue();
            ResultCollector resultCollector = new ResultCollector();
            AtomicInteger completedTasks = new AtomicInteger(0);
            AtomicInteger failedTasks = new AtomicInteger(0);
            
            long startTime = System.currentTimeMillis();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Task submitter
                futures.add(executor.submit(() -> {
                    for (int i = 0; i < taskCount; i++) {
                        Task task = new Task(i, "task-" + i, taskDuration);
                        taskQueue.submit(task);
                    }
                }));
                
                // Workers
                for (int i = 0; i < workerCount; i++) {
                    final int workerId = i;
                    futures.add(executor.submit(() -> {
                        while (completedTasks.get() + failedTasks.get() < taskCount) {
                            Task task = taskQueue.take();
                            if (task != null) {
                                try {
                                    // Execute task
                                    Thread.sleep(task.duration);
                                    
                                    // Generate result
                                    Result result = new Result(task.id, "result-" + task.id, workerId);
                                    resultCollector.add(result);
                                    completedTasks.incrementAndGet();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    failedTasks.incrementAndGet();
                                } catch (Exception e) {
                                    failedTasks.incrementAndGet();
                                }
                            }
                        }
                    }));
                }
                
                // Wait for all tasks to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some exceptions for E2E test
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            assertEquals(taskCount, completedTasks.get() + failedTasks.get(), "All tasks should be processed");
            assertEquals(completedTasks.get(), resultCollector.size(), "Results should match completed tasks");
            
            System.out.printf("Distributed task simulation completed in %d ms%n", duration);
            System.out.printf("Tasks: %d completed, %d failed%n", completedTasks.get(), failedTasks.get());
            System.out.printf("Workers: %d, Throughput: %.2f tasks/sec%n", 
                workerCount, (double) completedTasks.get() / (duration / 1000.0));
        }
    }

    // Helper classes for E2E testing
    private static class ConcurrentCache {
        private final ConcurrentHashMap<String, String> cache;
        private final int maxSize;
        
        public ConcurrentCache(int maxSize) {
            this.cache = new ConcurrentHashMap<>();
            this.maxSize = maxSize;
        }
        
        public String get(String key) {
            return cache.get(key);
        }
        
        public void put(String key, String value) {
            if (cache.size() >= maxSize) {
                // Simple eviction: remove first entry
                String firstKey = cache.keys().nextElement();
                cache.remove(firstKey);
            }
            cache.put(key, value);
        }
        
        public int size() {
            return cache.size();
        }
    }

    private static class TaskQueue {
        private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
        
        public void submit(Task task) {
            queue.offer(task);
        }
        
        public Task take() {
            try {
                return queue.poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    private static class ResultCollector {
        private final List<Result> results = new ArrayList<>();
        
        public synchronized void add(Result result) {
            results.add(result);
        }
        
        public synchronized int size() {
            return results.size();
        }
    }

    private static class Task {
        final int id;
        final String name;
        final int duration;
        
        Task(int id, String name, int duration) {
            this.id = id;
            this.name = name;
            this.duration = duration;
        }
    }

    private static class Result {
        final int taskId;
        final String value;
        final int workerId;
        
        Result(int taskId, String value, int workerId) {
            this.taskId = taskId;
            this.value = value;
            this.workerId = workerId;
        }
    }
}
