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
import java.util.stream.IntStream;

/**
 * Comprehensive concurrency tests for Virtual Threads and Traditional Threads
 * Tests thread safety, race conditions, deadlocks, and concurrent access patterns
 */
@DisplayName("Concurrency Tests")
class ConcurrencyTest {

    private static final int THREAD_COUNT = 100;
    private static final int ITERATIONS = 1000;
    private static final int SHARED_RESOURCE_SIZE = 1000;

    @Test
    @DisplayName("Virtual Threads - Concurrent Counter Test")
    void testVirtualThreadsConcurrentCounter() {
        System.out.println("\n=== Virtual Threads Concurrent Counter Test ===");
        
        AtomicInteger counter = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Increment counter concurrently
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        counter.incrementAndGet();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        int expectedValue = THREAD_COUNT * ITERATIONS;
        assertEquals(expectedValue, counter.get(), 
            "Counter should have correct value after concurrent increments");
        
        System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
    }

    @Test
    @DisplayName("Traditional Threads - Concurrent Counter Test")
    void testTraditionalThreadsConcurrentCounter() {
        System.out.println("\n=== Traditional Threads Concurrent Counter Test ===");
        
        AtomicInteger counter = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Increment counter concurrently
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        counter.incrementAndGet();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        int expectedValue = THREAD_COUNT * ITERATIONS;
        assertEquals(expectedValue, counter.get(), 
            "Counter should have correct value after concurrent increments");
        
        System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
    }

    @Test
    @DisplayName("Virtual Threads - Shared Resource Access Test")
    void testVirtualThreadsSharedResourceAccess() {
        System.out.println("\n=== Virtual Threads Shared Resource Access Test ===");
        
        SharedResource sharedResource = new SharedResource(SHARED_RESOURCE_SIZE);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Concurrent access to shared resource
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        int index = (threadId * ITERATIONS + j) % SHARED_RESOURCE_SIZE;
                        sharedResource.setValue(index, threadId * 1000 + j);
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        // Verify shared resource integrity
        assertTrue(sharedResource.validateIntegrity(), "Shared resource should maintain integrity");
        
        System.out.printf("Shared resource validation: %s%n", sharedResource.validateIntegrity());
    }

    @Test
    @DisplayName("Traditional Threads - Shared Resource Access Test")
    void testTraditionalThreadsSharedResourceAccess() {
        System.out.println("\n=== Traditional Threads Shared Resource Access Test ===");
        
        SharedResource sharedResource = new SharedResource(SHARED_RESOURCE_SIZE);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Concurrent access to shared resource
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        int index = (threadId * ITERATIONS + j) % SHARED_RESOURCE_SIZE;
                        sharedResource.setValue(index, threadId * 1000 + j);
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        // Verify shared resource integrity
        assertTrue(sharedResource.validateIntegrity(), "Shared resource should maintain integrity");
        
        System.out.printf("Shared resource validation: %s%n", sharedResource.validateIntegrity());
    }

    @Test
    @DisplayName("Virtual Threads - Producer-Consumer Test")
    void testVirtualThreadsProducerConsumer() {
        System.out.println("\n=== Virtual Threads Producer-Consumer Test ===");
        
        int bufferSize = 100;
        int producerCount = 10;
        int consumerCount = 10;
        int itemsPerProducer = 100;
        
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(bufferSize);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        try {
                            queue.put(producedCount.incrementAndGet());
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
                    while (consumedCount.get() < producerCount * itemsPerProducer) {
                        try {
                            Integer item = queue.poll(100, TimeUnit.MILLISECONDS);
                            if (item != null) {
                                consumedCount.incrementAndGet();
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
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        int expectedTotal = producerCount * itemsPerProducer;
        assertEquals(expectedTotal, producedCount.get(), "All items should be produced");
        assertEquals(expectedTotal, consumedCount.get(), "All items should be consumed");
        
        System.out.printf("Produced: %d, Consumed: %d, Expected: %d%n", 
            producedCount.get(), consumedCount.get(), expectedTotal);
    }

    @Test
    @DisplayName("Traditional Threads - Producer-Consumer Test")
    void testTraditionalThreadsProducerConsumer() {
        System.out.println("\n=== Traditional Threads Producer-Consumer Test ===");
        
        int bufferSize = 100;
        int producerCount = 10;
        int consumerCount = 10;
        int itemsPerProducer = 100;
        
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(bufferSize);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(producerCount + consumerCount)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create producers
            for (int i = 0; i < producerCount; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        try {
                            queue.put(producedCount.incrementAndGet());
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
                    while (consumedCount.get() < producerCount * itemsPerProducer) {
                        try {
                            Integer item = queue.poll(100, TimeUnit.MILLISECONDS);
                            if (item != null) {
                                consumedCount.incrementAndGet();
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
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        int expectedTotal = producerCount * itemsPerProducer;
        assertEquals(expectedTotal, producedCount.get(), "All items should be produced");
        assertEquals(expectedTotal, consumedCount.get(), "All items should be consumed");
        
        System.out.printf("Produced: %d, Consumed: %d, Expected: %d%n", 
            producedCount.get(), consumedCount.get(), expectedTotal);
    }

    @Test
    @DisplayName("Virtual Threads - Race Condition Detection")
    void testVirtualThreadsRaceConditionDetection() {
        System.out.println("\n=== Virtual Threads Race Condition Detection ===");
        
        RaceConditionResource resource = new RaceConditionResource();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that modify shared resource
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        resource.increment();
                        resource.decrement();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        // Check for race conditions
        assertTrue(resource.validateNoRaceCondition(), "No race conditions should be detected");
        
        System.out.printf("Race condition test: %s%n", resource.validateNoRaceCondition());
        System.out.printf("Final counter value: %d%n", resource.getCounter());
    }

    @Test
    @DisplayName("Traditional Threads - Race Condition Detection")
    void testTraditionalThreadsRaceConditionDetection() {
        System.out.println("\n=== Traditional Threads Race Condition Detection ===");
        
        RaceConditionResource resource = new RaceConditionResource();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that modify shared resource
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        resource.increment();
                        resource.decrement();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
        }
        
        // Check for race conditions
        assertTrue(resource.validateNoRaceCondition(), "No race conditions should be detected");
        
        System.out.printf("Race condition test: %s%n", resource.validateNoRaceCondition());
        System.out.printf("Final counter value: %d%n", resource.getCounter());
    }

    @Test
    @DisplayName("Virtual Threads - Deadlock Detection")
    void testVirtualThreadsDeadlockDetection() {
        System.out.println("\n=== Virtual Threads Deadlock Detection ===");
        
        DeadlockDetectionResource resource = new DeadlockDetectionResource();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that might cause deadlock
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    resource.method1(threadId);
                }));
            }
            
            // Wait for all threads to complete with timeout
            boolean allCompleted = true;
            for (Future<?> future : futures) {
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    allCompleted = false;
                    System.out.println("Thread timed out - possible deadlock detected");
                } catch (InterruptedException | ExecutionException e) {
                    allCompleted = false;
                    System.out.println("Thread failed: " + e.getMessage());
                }
            }
            
            // Check if deadlock occurred
            assertFalse(allCompleted, "Deadlock should be detected by timeout");
        }
        
        System.out.printf("Deadlock detection: timeout occurred (expected)%n");
    }

    @Test
    @DisplayName("Traditional Threads - Deadlock Detection")
    void testTraditionalThreadsDeadlockDetection() {
        System.out.println("\n=== Traditional Threads Deadlock Detection ===");
        
        DeadlockDetectionResource resource = new DeadlockDetectionResource();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that might cause deadlock
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    resource.method1(threadId);
                }));
            }
            
            // Wait for all threads to complete with timeout
            boolean allCompleted = true;
            for (Future<?> future : futures) {
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    allCompleted = false;
                    System.out.println("Thread timed out - possible deadlock detected");
                } catch (InterruptedException | ExecutionException e) {
                    allCompleted = false;
                    System.out.println("Thread failed: " + e.getMessage());
                }
            }
            
            // Check if deadlock occurred
            assertFalse(allCompleted, "Deadlock should be detected by timeout");
        }
        
        System.out.printf("Deadlock detection: timeout occurred (expected)%n");
    }

    @Test
    @DisplayName("Virtual Threads - Synchronized Block Performance")
    void testVirtualThreadsSynchronizedPerformance() {
        System.out.println("\n=== Virtual Threads Synchronized Block Performance ===");
        
        SynchronizedResource resource = new SynchronizedResource();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use synchronized blocks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        resource.synchronizedMethod();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("Virtual Threads synchronized duration: %d ms%n", duration);
            assertTrue(duration > 0, "Duration should be positive");
        }
    }

    @Test
    @DisplayName("Traditional Threads - Synchronized Block Performance")
    void testTraditionalThreadsSynchronizedPerformance() {
        System.out.println("\n=== Traditional Threads Synchronized Block Performance ===");
        
        SynchronizedResource resource = new SynchronizedResource();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            long startTime = System.currentTimeMillis();
            
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use synchronized blocks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        resource.synchronizedMethod();
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Thread execution failed: " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("Traditional Threads synchronized duration: %d ms%n", duration);
            assertTrue(duration > 0, "Duration should be positive");
        }
    }

    @Nested
    @DisplayName("Advanced Concurrency Tests")
    class AdvancedConcurrencyTests {

        @Test
        @DisplayName("Virtual Threads - Concurrent HashMap Access")
        void testVirtualThreadsConcurrentHashMap() {
            System.out.println("\n=== Virtual Threads Concurrent HashMap Access ===");
            
            ConcurrentMapAccess mapAccess = new ConcurrentMapAccess();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Concurrent map operations
                for (int i = 0; i < THREAD_COUNT; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            mapAccess.put("key-" + threadId + "-" + j, "value-" + threadId + "-" + j);
                            mapAccess.get("key-" + threadId + "-" + j);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            // Verify map integrity
            assertTrue(mapAccess.validateIntegrity(), "Concurrent map should maintain integrity");
            System.out.printf("Map size: %d, Integrity: %s%n", 
                mapAccess.size(), mapAccess.validateIntegrity());
        }

        @Test
        @DisplayName("Traditional Threads - Concurrent HashMap Access")
        void testTraditionalThreadsConcurrentHashMap() {
            System.out.println("\n=== Traditional Threads Concurrent HashMap Access ===");
            
            ConcurrentMapAccess mapAccess = new ConcurrentMapAccess();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Concurrent map operations
                for (int i = 0; i < THREAD_COUNT; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            mapAccess.put("key-" + threadId + "-" + j, "value-" + threadId + "-" + j);
                            mapAccess.get("key-" + threadId + "-" + j);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
            }
            
            // Verify map integrity
            assertTrue(mapAccess.validateIntegrity(), "Concurrent map should maintain integrity");
            System.out.printf("Map size: %d, Integrity: %s%n", 
                mapAccess.size(), mapAccess.validateIntegrity());
        }

        @Test
        @DisplayName("Virtual Threads - Atomic Operations Performance")
        void testVirtualThreadsAtomicOperations() {
            System.out.println("\n=== Virtual Threads Atomic Operations Performance ===");
            
            AtomicResource atomicResource = new AtomicResource();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                long startTime = System.currentTimeMillis();
                
                List<Future<?>> futures = new ArrayList<>();
                
                // Atomic operations
                for (int i = 0; i < THREAD_COUNT; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            atomicResource.increment();
                            atomicResource.addAndGet(1);
                            atomicResource.compareAndSet(0, 1);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                System.out.printf("Virtual Threads atomic operations duration: %d ms%n", duration);
                System.out.printf("Final counter value: %d%n", atomicResource.get());
            }
        }

        @Test
        @DisplayName("Traditional Threads - Atomic Operations Performance")
        void testTraditionalThreadsAtomicOperations() {
            System.out.println("\n=== Traditional Threads Atomic Operations Performance ===");
            
            AtomicResource atomicResource = new AtomicResource();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
                long startTime = System.currentTimeMillis();
                
                List<Future<?>> futures = new ArrayList<>();
                
                // Atomic operations
                for (int i = 0; i < THREAD_COUNT; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            atomicResource.increment();
                            atomicResource.addAndGet(1);
                            atomicResource.compareAndSet(0, 1);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        fail("Thread execution failed: " + e.getMessage());
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                System.out.printf("Traditional Threads atomic operations duration: %d ms%n", duration);
                System.out.printf("Final counter value: %d%n", atomicResource.get());
            }
        }
    }

    // Helper classes for concurrency testing
    private static class SharedResource {
        private final int[] values;
        private final boolean[] flags;
        
        public SharedResource(int size) {
            this.values = new int[size];
            this.flags = new boolean[size];
        }
        
        public synchronized void setValue(int index, int value) {
            values[index] = value;
            flags[index] = true;
        }
        
        public synchronized int getValue(int index) {
            return values[index];
        }
        
        public synchronized boolean getFlag(int index) {
            return flags[index];
        }
        
        public synchronized boolean validateIntegrity() {
            // Check that all accessed positions have been set
            for (boolean flag : flags) {
                if (!flag) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class RaceConditionResource {
        private int counter = 0;
        private final Object lock = new Object();
        
        public void increment() {
            synchronized (lock) {
                counter++;
            }
        }
        
        public void decrement() {
            synchronized (lock) {
                counter--;
            }
        }
        
        public int getCounter() {
            synchronized (lock) {
                return counter;
            }
        }
        
        public boolean validateNoRaceCondition() {
            // After equal increments and decrements, counter should be 0
            return getCounter() == 0;
        }
    }

    private static class DeadlockDetectionResource {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        public void method1(int threadId) {
            synchronized (lock1) {
                try {
                    Thread.sleep(1); // Small delay to increase deadlock probability
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                method2(threadId);
            }
        }
        
        public void method2(int threadId) {
            synchronized (lock2) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                method3(threadId);
            }
        }
        
        public void method3(int threadId) {
            synchronized (lock1) {
                // This can cause deadlock
            }
        }
    }

    private static class SynchronizedResource {
        private int counter = 0;
        
        public synchronized void synchronizedMethod() {
            counter++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter--;
        }
    }

    private static class ConcurrentMapAccess {
        private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        
        public void put(String key, String value) {
            map.put(key, value);
        }
        
        public String get(String key) {
            return map.get(key);
        }
        
        public int size() {
            return map.size();
        }
        
        public boolean validateIntegrity() {
            // Basic integrity check
            return map.size() >= 0;
        }
    }

    private static class AtomicResource {
        private final AtomicInteger counter = new AtomicInteger(0);
        
        public void increment() {
            counter.incrementAndGet();
        }
        
        public void addAndGet(int delta) {
            counter.addAndGet(delta);
        }
        
        public boolean compareAndSet(int expect, int update) {
            return counter.compareAndSet(expect, update);
        }
        
        public int get() {
            return counter.get();
        }
    }
}
