
package virtualthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadRunner {

    public static void runTasks(int numberOfTasks, boolean useVirtualThreads) {
        long start = System.currentTimeMillis();

        try (ExecutorService executor = useVirtualThreads ?
                Executors.newVirtualThreadPerTaskExecutor() :
                Executors.newCachedThreadPool()) {

            for (int i = 0; i < numberOfTasks; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(100); // Simulate IO-bound task
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        long end = System.currentTimeMillis();
        String threadType = useVirtualThreads ? "Virtual" : "Traditional";
        System.out.println(threadType + " Threads Duration: " + (end - start) + " ms");
    }
}
