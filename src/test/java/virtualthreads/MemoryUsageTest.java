package virtualthreads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Memory usage tests for Virtual Threads and Traditional Threads
 * Tests memory consumption, garbage collection behavior, and memory leaks
 */
@DisplayName("Memory Usage Tests")
class MemoryUsageTest {

    private static final int THREAD_COUNT = 1000;
    private static final int ITERATIONS = 100;
    private static final int MEMORY_ALLOCATION_SIZE = 1024; // 1KB per allocation

    @Test
    @DisplayName("Virtual Threads - Memory Usage Test")
    void testVirtualThreadsMemoryUsage() {
        System.out.println("\n=== Virtual Threads Memory Usage Test ===");
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        AtomicLong totalAllocated = new AtomicLong(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that allocate memory
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    byte[] memory = new byte[MEMORY_ALLOCATION_SIZE];
                    totalAllocated.addAndGet(memory.length);
                    
                    // Simulate work
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Memory will be garbage collected when method returns
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
        
        // Force garbage collection
        System.gc();
        Thread.sleep(100); // Give GC time to complete
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Initial Memory: %d KB%n", initialMemory / 1024);
        System.out.printf("Final Memory: %d KB%n", finalMemory / 1024);
        System.out.printf("Memory Used: %d KB%n", memoryUsed / 1024);
        System.out.printf("Total Allocated: %d KB%n", totalAllocated.get() / 1024);
        
        assertTrue(memoryUsed > 0, "Memory should be used during execution");
        assertTrue(totalAllocated.get() > 0, "Memory should be allocated");
    }

    @Test
    @DisplayName("Traditional Threads - Memory Usage Test")
    void testTraditionalThreadsMemoryUsage() {
        System.out.println("\n=== Traditional Threads Memory Usage Test ===");
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        AtomicLong totalAllocated = new AtomicLong(0);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that allocate memory
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    byte[] memory = new byte[MEMORY_ALLOCATION_SIZE];
                    totalAllocated.addAndGet(memory.length);
                    
                    // Simulate work
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Memory will be garbage collected when method returns
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
        
        // Force garbage collection
        System.gc();
        Thread.sleep(100); // Give GC time to complete
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Initial Memory: %d KB%n", initialMemory / 1024);
        System.out.printf("Final Memory: %d KB%n", finalMemory / 1024);
        System.out.printf("Memory Used: %d KB%n", memoryUsed / 1024);
        System.out.printf("Total Allocated: %d KB%n", totalAllocated.get() / 1024);
        
        assertTrue(memoryUsed > 0, "Memory should be used during execution");
        assertTrue(totalAllocated.get() > 0, "Memory should be allocated");
    }

    @Test
    @DisplayName("Virtual Threads - Memory Leak Detection")
    void testVirtualThreadsMemoryLeakDetection() {
        System.out.println("\n=== Virtual Threads Memory Leak Detection ===");
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Run multiple iterations to detect memory leaks
        for (int iteration = 0; iteration < 10; iteration++) {
            System.out.printf("Iteration %d:%n", iteration + 1);
            
            long iterationStartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    // Create threads that allocate memory
                    for (int i = 0; i < THREAD_COUNT; i++) {
                        futures.add(executor.submit(() -> {
                            // Allocate memory and keep reference
                            MemoryLeakDetector.allocatedMemory.add(new byte[MEMORY_ALLOCATION_SIZE]);
                            
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
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
                }
            
            // Clear allocated memory
            MemoryLeakDetector.allocatedMemory.clear();
            
            // Force garbage collection
            System.gc();
            Thread.sleep(100);
            
            long iterationEndMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long iterationMemoryUsed = iterationEndMemory - iterationStartMemory;
            
            System.out.printf("  Memory used in iteration: %d KB%n", iterationMemoryUsed / 1024);
        }
        
        // Final memory measurement
        System.gc();
        Thread.sleep(200);
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalMemoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Total memory used across all iterations: %d KB%n", totalMemoryUsed / 1024);
        
        // Check for memory leaks (memory should return to near initial levels)
        assertTrue(totalMemoryUsed < (initialMemory * 0.5), 
            "Memory usage should return to near initial levels (possible memory leak)");
    }

    @Test
    @DisplayName("Traditional Threads - Memory Leak Detection")
    void testTraditionalThreadsMemoryLeakDetection() {
        System.out.println("\n=== Traditional Threads Memory Leak Detection ===");
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Run multiple iterations to detect memory leaks
        for (int iteration = 0; iteration < 10; iteration++) {
            System.out.printf("Iteration %d:%n", iteration + 1);
            
            long iterationStartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
                    List<Future<?>> futures = new ArrayList<>();
                    
                    // Create threads that allocate memory
                    for (int i = 0; i < THREAD_COUNT; i++) {
                        futures.add(executor.submit(() -> {
                            // Allocate memory and keep reference
                            MemoryLeakDetector.allocatedMemory.add(new byte[MEMORY_ALLOCATION_SIZE]);
                            
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
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
                }
            
            // Clear allocated memory
            MemoryLeakDetector.allocatedMemory.clear();
            
            // Force garbage collection
            System.gc();
            Thread.sleep(100);
            
            long iterationEndMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long iterationMemoryUsed = iterationEndMemory - iterationStartMemory;
            
            System.out.printf("  Memory used in iteration: %d KB%n", iterationMemoryUsed / 1024);
        }
        
        // Final memory measurement
        System.gc();
        Thread.sleep(200);
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalMemoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Total memory used across all iterations: %d KB%n", totalMemoryUsed / 1024);
        
        // Check for memory leaks (memory should return to near initial levels)
        assertTrue(totalMemoryUsed < (initialMemory * 0.5), 
            "Memory usage should return to near initial levels (possible memory leak)");
    }

    @Test
    @DisplayName("Virtual Threads - Garbage Collection Performance")
    void testVirtualThreadsGarbageCollectionPerformance() {
        System.out.println("\n=== Virtual Threads Garbage Collection Performance ===");
        
        // Create many short-lived objects
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < THREAD_COUNT * 10; i++) {
                futures.add(executor.submit(() -> {
                    // Create many short-lived objects
                    for (int j = 0; j < 100; j++) {
                        byte[] temp = new byte[1024];
                        // Object will be eligible for GC immediately
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
        }
        
        // Measure GC performance
        long gcStartTime = System.currentTimeMillis();
        System.gc();
        long gcDuration = System.currentTimeMillis() - gcStartTime;
        
        System.out.printf("Garbage Collection duration: %d ms%n", gcDuration);
        assertTrue(gcDuration < 1000, "GC should complete within reasonable time");
    }

    @Test
    @DisplayName("Traditional Threads - Garbage Collection Performance")
    void testTraditionalThreadsGarbageCollectionPerformance() {
        System.out.println("\n=== Traditional Threads Garbage Collection Performance ===");
        
        // Create many short-lived objects
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            for (int i = 0; i < THREAD_COUNT * 10; i++) {
                futures.add(executor.submit(() -> {
                    // Create many short-lived objects
                    for (int j = 0; j < 100; j++) {
                        byte[] temp = new byte[1024];
                        // Object will be eligible for GC immediately
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
        }
        
        // Measure GC performance
        long gcStartTime = System.currentTimeMillis();
        System.gc();
        long gcDuration = System.currentTimeMillis() - gcStartTime;
        
        System.out.printf("Garbage Collection duration: %d ms%n", gcDuration);
        assertTrue(gcDuration < 1000, "GC should complete within reasonable time");
    }

    @Test
    @DisplayName("Virtual Threads - Large Object Allocation")
    void testVirtualThreadsLargeObjectAllocation() {
        System.out.println("\n=== Virtual Threads Large Object Allocation ===");
        
        int largeObjectSize = 1024 * 1024; // 1MB
        int objectCount = 100;
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that allocate large objects
            for (int i = 0; i < objectCount; i++) {
                futures.add(executor.submit(() -> {
                    byte[] largeObject = new byte[largeObjectSize];
                    
                    // Simulate work with large object
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Large object will be garbage collected when method returns
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
        
        // Force garbage collection
        System.gc();
        Thread.sleep(200); // Give GC more time for large objects
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Initial Memory: %d MB%n", initialMemory / (1024 * 1024));
        System.out.printf("Final Memory: %d MB%n", finalMemory / (1024 * 1024));
        System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
        
        assertTrue(memoryUsed > 0, "Memory should be used during execution");
    }

    @Test
    @DisplayName("Traditional Threads - Large Object Allocation")
    void testTraditionalThreadsLargeObjectAllocation() {
        System.out.println("\n=== Traditional Threads Large Object Allocation ===");
        
        int largeObjectSize = 1024 * 1024; // 1MB
        int objectCount = 100;
        
        // Measure initial memory
        System.gc();
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            List<Future<?>> futures = new ArrayList<>();
            
            // Create threads that allocate large objects
            for (int i = 0; i < objectCount; i++) {
                futures.add(executor.submit(() -> {
                    byte[] largeObject = new byte[largeObjectSize];
                    
                    // Simulate work with large object
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Large object will be garbage collected when method returns
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
        
        // Force garbage collection
        System.gc();
        Thread.sleep(200); // Give GC more time for large objects
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("Initial Memory: %d MB%n", initialMemory / (1024 * 1024));
        System.out.printf("Final Memory: %d MB%n", finalMemory / (1024 * 1024));
        System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
        
        assertTrue(memoryUsed > 0, "Memory should be used during execution");
    }

    @Nested
    @DisplayName("Advanced Memory Tests")
    class AdvancedMemoryTests {

        @Test
        @DisplayName("Virtual Threads - Memory Stress Test")
        void testVirtualThreadsMemoryStress() {
            System.out.println("\n=== Virtual Threads Memory Stress Test ===");
            
            int stressThreadCount = 5000;
            int stressIterations = 50;
            int stressObjectSize = 2048; // 2KB
            
            // Measure initial memory
            System.gc();
            long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create many threads with high memory allocation
                for (int i = 0; i < stressThreadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < stressIterations; j++) {
                            byte[] memory = new byte[stressObjectSize];
                            // Fill memory with data
                            new Random().nextBytes(memory);
                            
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for stress test
                    }
                }
            }
            
            // Force garbage collection
            System.gc();
            Thread.sleep(500);
            
            long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            System.out.printf("Initial Memory: %d MB%n", initialMemory / (1024 * 1024));
            System.out.printf("Final Memory: %d MB%n", finalMemory / (1024 * 1024));
            System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
            
            // Memory should return to near initial levels after GC
            assertTrue(memoryUsed < (initialMemory * 0.8), 
                "Memory should return to near initial levels after stress test");
        }

        @Test
        @DisplayName("Traditional Threads - Memory Stress Test")
        void testTraditionalThreadsMemoryStress() {
            System.out.println("\n=== Traditional Threads Memory Stress Test ===");
            
            int stressThreadCount = 1000; // Limited for traditional threads
            int stressIterations = 50;
            int stressObjectSize = 2048; // 2KB
            
            // Measure initial memory
            System.gc();
            long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            try (ExecutorService executor = Executors.newFixedThreadPool(stressThreadCount)) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create many threads with high memory allocation
                for (int i = 0; i < stressThreadCount; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < stressIterations; j++) {
                            byte[] memory = new byte[stressObjectSize];
                            // Fill memory with data
                            new Random().nextBytes(memory);
                            
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }));
                }
                
                // Wait for all threads to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Ignore exceptions for stress test
                    }
                }
            }
            
            // Force garbage collection
            System.gc();
            Thread.sleep(500);
            
            long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            System.out.printf("Initial Memory: %d MB%n", initialMemory / (1024 * 1024));
            System.out.printf("Final Memory: %d MB%n", finalMemory / (1024 * 1024));
            System.out.printf("Memory Used: %d MB%n", memoryUsed / (1024 * 1024));
            
            // Memory should return to near initial levels after GC
            assertTrue(memoryUsed < (initialMemory * 0.8), 
                "Memory should return to near initial levels after stress test");
        }

        @Test
        @DisplayName("Virtual Threads - Memory Pool Test")
        void testVirtualThreadsMemoryPool() {
            System.out.println("\n=== Virtual Threads Memory Pool Test ===");
            
            int poolSize = 1000;
            int objectSize = 1024;
            
            // Create memory pool
            MemoryPool memoryPool = new MemoryPool(poolSize, objectSize);
            
            // Measure initial memory
            System.gc();
            long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<?>> futures = new ArrayList<>();
                
                // Create threads that use memory pool
                for (int i = 0; i < THREAD_COUNT; i++) {
                    futures.add(executor.submit(() -> {
                        for (int j = 0; j < ITERATIONS; j++) {
                            byte[] memory = memoryPool.acquire();
                            // Use memory
                            memory[0] = (byte) (i % 256);
                            memory[memory.length - 1] = (byte) (j % 256);
                            memoryPool.release(memory);
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
            
            // Verify pool integrity
            assertTrue(memoryPool.validatePoolIntegrity(), "Memory pool should maintain integrity");
            
            // Force garbage collection
            System.gc();
            Thread.sleep(100);
            
            long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            System.out.printf("Initial Memory: %d KB%n", initialMemory / 1024);
            System.out.printf("Final Memory: %d KB%n", finalMemory / 1024);
            System.out.printf("Memory Used: %d KB%n", memoryUsed / 1024);
            System.out.printf("Pool Size: %d, Available: %d%n", 
                memoryPool.getPoolSize(), memoryPool.getAvailableCount());
            
            // Memory pool should reduce memory usage
            assertTrue(memoryPool.getAvailableCount() > 0, "Memory pool should have available objects");
        }
    }

    // Helper classes for memory testing
    private static class MemoryLeakDetector {
        static final List<byte[]> allocatedMemory = new ArrayList<>();
    }

    private static class MemoryPool {
        private final BlockingQueue<byte[]> pool;
        private final int objectSize;
        
        public MemoryPool(int poolSize, int objectSize) {
            this.objectSize = objectSize;
            this.pool = new LinkedBlockingQueue<>(poolSize);
            
            // Pre-populate pool
            for (int i = 0; i < poolSize; i++) {
                this.pool.offer(new byte[objectSize]);
            }
        }
        
        public byte[] acquire() {
            try {
                return pool.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new byte[objectSize];
            }
        }
        
        public void release(byte[] memory) {
            pool.offer(memory);
        }
        
        public int getPoolSize() {
            return pool.size();
        }
        
        public int getAvailableCount() {
            return pool.size();
        }
        
        public boolean validatePoolIntegrity() {
            return pool.size() > 0 && pool.size() <= getPoolSize();
        }
    }
}
