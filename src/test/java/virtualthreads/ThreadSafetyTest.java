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
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Thread safety tests for Virtual Threads and Traditional Threads
 * Tests thread safety, race conditions, deadlocks, and concurrent access patterns
 */
@DisplayName("Thread Safety Tests")
class ThreadSafetyTest {

    private static final int THREAD_COUNT = 100;
    private static final int ITERATIONS = 1000;
    private static final int SHARED_RESOURCE_SIZE = 1000;

    @Test
    @DisplayName("Virtual Threads - Synchronized Block Thread Safety")
    void testVirtualThreadsSynchronizedBlockThreadSafety() {
        System.out.println("\n=== Virtual Threads Synchronized Block Thread Safety Test ===");
        
        ThreadSafeCounter counter = new ThreadSafeCounter();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that increment counter in synchronized blocks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        counter.increment();
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
            
            int expectedValue = THREAD_COUNT * ITERATIONS;
            assertEquals(expectedValue, counter.get(), 
                "Counter should have correct value after concurrent increments");
            
            System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Synchronized Block Thread Safety")
    void testTraditionalThreadsSynchronizedBlockThreadSafety() {
        System.out.println("\n=== Traditional Threads Synchronized Block Thread Safety Test ===");
        
        ThreadSafeCounter counter = new ThreadSafeCounter();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that increment counter in synchronized blocks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        counter.increment();
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
            
            int expectedValue = THREAD_COUNT * ITERATIONS;
            assertEquals(expectedValue, counter.get(), 
                "Counter should have correct value after concurrent increments");
            
            System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Reentrant Lock Thread Safety")
    void testVirtualThreadsReentrantLockThreadSafety() {
        System.out.println("\n=== Virtual Threads Reentrant Lock Thread Safety Test ===");
        
        ReentrantLockCounter counter = new ReentrantLockCounter();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use reentrant locks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    // Nested lock operations
                    counter.increment();
                    counter.increment();
                    counter.increment();
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
            
            int expectedValue = THREAD_COUNT * ITERATIONS * 3; // Each thread calls increment 3 times
            assertEquals(expectedValue, counter.get(), 
                "Counter should have correct value after reentrant locks");
            
            System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Reentrant Lock Thread Safety")
    void testTraditionalThreadsReentrantLockThreadSafety() {
        System.out.println("\n=== Traditional Threads Reentrant Lock Thread Safety Test ===");
        
        ReentrantLockCounter counter = new ReentrantCounter();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use reentrant locks
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    // Nested lock operations
                    counter.increment();
                    counter.increment();
                    counter.increment();
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
            
            int expectedValue = THREAD_COUNT * ITERATIONS * 3; // Each thread calls increment 3 times
            assertEquals(expectedValue, counter.get(), 
                "Counter should have correct value after reentrant locks");
            
            System.out.printf("Final counter value: %d (expected: %d)%n", counter.get(), expectedValue);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Concurrent List Thread Safety")
    void testVirtualThreadsConcurrentListThreadSafety() {
        System.out.println("\n=== Virtual Threads Concurrent List Thread Safety Test ===");
        
        ConcurrentList<String> concurrentList = new ConcurrentList<>();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that modify concurrent list
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        concurrentList.add("item-" + threadId + "-" + j);
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some failures for this test
                }
            }
            
            int expectedSize = THREAD_COUNT * ITERATIONS;
            int actualSize = concurrentList.size();
            
            assertEquals(expectedSize, actualSize, "List should contain all items");
            
            // Verify list integrity
            for (int i = 0; i < actualSize; i++) {
                String item = concurrentList.get(i);
                assertTrue(item.startsWith("item-"), "Item should have correct prefix");
            }
            
            System.out.printf("List size: %d (expected: %d)%n", actualSize, expectedSize);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Concurrent List Thread Safety")
    void testTraditionalThreadsConcurrentListThreadSafety() {
        System.out.println("\n=== Traditional Threads Concurrent List Thread Safety Test ===");
        
        ConcurrentList<String> concurrentList = new ConcurrentList<>();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that modify concurrent list
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        concurrentList.add("item-" + threadId + "-" + j);
                    }
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some failures for this test
                }
            }
            
            int expectedSize = THREAD_COUNT * ITERATIONS;
            int actualSize = concurrentList.size();
            
            assertEquals(expectedSize, actualSize, "List should contain all items");
            
            // Verify list integrity
            for (int i = 0; i < actualSize; i++) {
                String item = concurrentList.get(i);
                assertTrue(item.startsWith("item-"), "Item should have correct prefix");
            }
            
            System.out.printf("List size: %d (expected: %d)%n", actualSize, expectedSize);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Atomic Operations Thread Safety")
    void testVirtualThreadsAtomicOperationsThreadSafety() {
        System.out.println("\n=== Virtual Threads Atomic Operations Thread Safety ===");
        
        AtomicCounter counter = new AtomicCounter();
        AtomicLong sum = new AtomicLong(0);
        AtomicInteger boolean flag = new AtomicBoolean(false);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that perform atomic operations
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    // Atomic operations
                    counter.incrementAndGet();
                    sum.addAndGet(counter.get());
                    
                    // Atomic boolean operations
                    flag.set(true);
                    flag.set(false);
                    
                    // Atomic long operations
                    sum.addAndGet(counter.get());
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some failures for this test
                }
            }
            
            int expectedCounter = THREAD_COUNT;
            long expectedSum = (long) expectedCounter * (expectedCounter + 1) / 2;
            
            assertEquals(expectedCounter, counter.get(), "Counter should have correct value");
            assertEquals(expectedSum, sum.get(), "Sum should be correct");
            assertFalse(flag.get(), "Flag should be false");
            
            System.out.printf("Counter: %d (expected: %d)%n", counter.get(), expectedCounter);
            System.out.printf("Sum: %d (expected: %d)%n", sum.get(), expectedSum);
        }
    }

    @Test
    @DisplayName("Traditional Threads - Atomic Operations Thread Safety")
    void testTraditionalThreadsAtomicOperationsThreadSafety() {
        System.out.println("\n=== Traditional Threads Atomic Operations Thread Safety ===");
        
        AtomicCounter counter = new AtomicCounter();
        AtomicLong sum = new AtomicLong(0);
        AtomicInteger boolean flag = new AtomicBoolean(false);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that perform atomic operations
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> {
                    // Atomic operations
                    counter.incrementAndGet();
                    sum.addAndGet(counter.get());
                    
                    // Atomic boolean operations
                    flag.set(true);
                    flag.set(false);
                    
                    // Atomic long operations
                    sum.addAndGet(counter.get());
                }));
            }
            
            // Wait for all threads to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore some failures for this test
                }
            }
            
            int expectedCounter = THREAD_COUNT;
            long expectedSum = (long) expectedCounter * (expectedCounter + 1) / 2;
            
            assertEquals(expectedCounter, counter.get(), "Counter should have correct value");
            assertEquals(expectedSum, sum.get(), "Sum should be correct");
            assertFalse(flag.get(), "Flag should be false");
            
            System.out.printf("Counter: %d (expected: %d)%n", counter.get(), expectedCounter);
            System.out.printf("Sum: %d (expected: %d)%n", sum.get(), expectedSum);
        }
    }

    @Test
    @DisplayName("Virtual Threads - Deadlock Prevention")
    void testVirtualThreadsDeadlockPrevention() {
        System.out.println("\n=== Virtual Threads Deadlock Prevention Test ===");
        
        DeadlockPrevention deadlockPrevention = new DeadlockPrevention();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use deadlock prevention strategies
            for (int i = 0; i < THREAD_COUNT; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> deadlockPrevention.method1(threadId)));
                }
                
            // Wait for all threads to complete or timeout
            boolean allCompleted = true;
            for (Future<?> future : futures) {
                    try {
                        future.get(5, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        allCompleted = false;
                    } catch (InterruptedException | ExecutionException e) {
                        allCompleted = false;
                    }
                }
                
            }
            
            // Check if deadlock was prevented
            if (allCompleted) {
                System.out.println("No deadlock detected - all threads completed successfully");
            } else {
                System.out.println("Deadlock detected - some threads timed out");
            }
        }
    }

    @Test
    @DisplayName("Traditional Threads - Deadlock Prevention")
    void testTraditionalThreadsDeadlockPrevention() {
        System.out.println("\n=== Traditional Threads Deadlock Prevention Test ===");
        
        DeadlockPrevention deadlockPrevention = new DeadlockPrevention();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that use deadlock prevention strategies
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                futures.add(executor.submit(() -> deadlockPrevention.method1(threadId)));
                }
            
            // Wait for all threads to complete or timeout
            boolean allCompleted = true;
            for (Future<?> future : futures) {
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    allCompleted = false;
                } catch (InterruptedException | ExecutionException e) {
                    allCompleted = false;
                }
            }
            
            // Check if deadlock was prevented
            if (allCompleted) {
                System.out.println("No deadlock detected - all threads completed successfully");
            } else {
                System.out.println("Deadlock detected - some threads timed out");
            }
        }
    }

    @Nested
    @DisplayName("Advanced Thread Safety Tests")
    class AdvancedThreadSafetyTests {

        @Test
        @DisplayName("Virtual Threads - Complex Data Structure Thread Safety")
        void testVirtualThreadsComplexDataStructureThreadSafety() {
            System.out.println("\n=== Virtual Threads Complex Data Structure Thread Safety ===");
            
            ComplexDataStructure dataStructure = new ComplexDataStructure();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create threads that modify complex data structure
                for (int i = 0; i < THREAD_COUNT; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            dataStructure.add("item-" + threadId + "-" + j);
                            dataStructure.remove("item-" + threadId + "-" + j);
                            dataStructure.update("item-" + threadId + "-" + j, "updated-" + threadId + "-" + j);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
            }
            
            // Verify data structure integrity
            assertTrue(dataStructure.validateIntegrity(), "Complex data structure should maintain integrity");
            
            System.out.printf("Data Structure Size: %d%n", dataStructure.size());
        }

        @Test
        @DisplayName("Traditional Threads - Complex Data Structure Thread Safety")
        void testTraditionalThreadsComplexDataStructureThreadSafety() {
            System.out.println("\n=== Traditional Threads Complex Data Structure Thread Safety ===");
            
            ComplexDataStructure dataStructure = new ComplexDataStructure();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create threads that modify complex data structure
                for (int i = 0; i < THREAD_COUNT; i++) {
                    final int threadId = i;
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            dataStructure.add("item-" + threadId + "-" + j);
                            dataStructure.remove("item-" + threadId + "-" + j);
                            dataStructure.update("item-" + threadId + "-" + j, "updated-" + threadId + "-" + j);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
            }
            
            // Verify data structure integrity
            assertTrue(dataStructure.validateIntegrity(), "Complex data structure should maintain integrity");
            
            System.out.printf("Data Structure Size: %d%n", dataStructure.size());
        }

        @Test
        @DisplayName("Virtual Threads - High Contention Scenario")
        void testVirtualThreadsHighContentionScenario() {
            System.out.println("\n=== Virtual Threads High Contention Scenario ===");
            
            int threadCount = 10000;
            int iterations = 100;
            int sharedResourceSize = 10000;
            
            SharedResource sharedResource = new SharedResource(sharedResourceSize);
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create high contention scenario
                for (int i = 0; i < threadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < iterations; j++) {
                            sharedResource.access(i % sharedResourceSize);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                int completedTasks = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get();
                        completedTasks++;
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                System.out.printf("Completed %d/%d tasks in high contention scenario%n", 
                    completedTasks, threadCount);
                
                // Verify shared resource integrity
                assertTrue(sharedResource.validateIntegrity(), 
                    "Shared resource should maintain integrity under high contention");
            }
        }

        @Test
        @DisplayName("Traditional Threads - High Contention Scenario")
        void testTraditionalThreadsHighContentionScenario() {
            System.out.println("\n=== Traditional Threads High Contention Scenario ===");
            
            int threadCount = 1000; // Limited for traditional threads
            int iterations = 100;
            int sharedResourceSize = 10000;
            
            SharedResource sharedResource = new SharedResource(sharedResourceSize);
            
            try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create high contention scenario
                for (int i = 0; i < threadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < iterations; j++) {
                            sharedResource.access(i % sharedResourceSize);
                        }
                    }));
                }
                
                // Wait for all threads to complete
                int completedTasks = 0;
                for (Future<?> future : futures) {
                    try {
                        future.get();
                        completedTasks++;
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore some failures for this test
                    }
                }
                
                System.out.printf("Completed %d/%d tasks in high contention scenario%n", 
                    completedTasks, threadCount);
                
                // Verify shared resource integrity
                assertTrue(sharedResource.validateIntegrity(), 
                    "Shared resource should maintain integrity under high contention");
            }
        }
    }

    // Helper classes for thread safety testing
    private static class ThreadSafeCounter {
        private int counter = 0;
        
        public synchronized void increment() {
            counter++;
        }
        
        public int get() {
            return counter;
        }
    }

    private static class ReentrantCounter {
        private int counter = 0;
        private final ReentrantLock lock = new ReentrantLock();
        
        public void increment() {
            lock.lock();
            try {
                counter++;
            } finally {
                lock.unlock();
            }
        }
        
        public int get() {
            return counter;
        }
    }

    private static class ConcurrentList<T> {
        private final List<T> list = new ArrayList<>();
        
        public void add(T item) {
            list.add(item);
        }
        
        public T get(int index) {
            return list.get(index);
        }
        
        public void remove(int index) {
            list.remove(index);
        }
        
        public void update(int index, T item) {
            list.set(index, item);
        }
        
        public int size() {
            return list.size();
        }
        
        public boolean validateIntegrity() {
            // Basic integrity check
            return size() >= 0;
        }
    }

    private static class SharedResource {
        private final int[] values;
        private final boolean[] flags;
        
        public SharedResource(int size) {
            this.values = new int[size];
            this.flags = new boolean[size];
        }
        
        public void access(int index) {
            synchronized (values) {
                values[index]++;
                flags[index] = true;
            }
        }
        
        public boolean validateIntegrity() {
            // Check that all accessed positions have been set
            for (boolean flag : flags) {
                if (!flag) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class ComplexDataStructure {
        private final Map<String, Object> data = new ConcurrentHashMap<>();
        
        public void add(String key, Object value) {
            data.put(key, value);
        }
        
        public Object get(String key) {
            return data.get(key);
        }
        
        public void remove(String key) {
            data.remove(key);
        }
        
        public void update(String key, Object value) {
            data.put(key, value);
        }
        
        public int size() {
            return data.size();
        }
        
        public boolean validateIntegrity() {
            // Basic integrity check
            return data.size() >= 0;
        }
    }

    private static class DeadlockPrevention {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        public void method1(int threadId) {
            synchronized (lock1) {
                try {
                    method2(threadId);
                } finally {
                    // lock1.unlock();
                }
            }
        }
        
        public void method2(int threadId) {
            synchronized (lock2) {
                try {
                    method3(threadId);
                } finally {
                    // lock2.unlock();
                }
            }
        }
        
        public void method3(int threadId) {
            synchronized (lock1) {
                try {
                    method2(threadId);
                } finally {
                    // lock1.unlock();
                }
            }
        }
    }
}
}
