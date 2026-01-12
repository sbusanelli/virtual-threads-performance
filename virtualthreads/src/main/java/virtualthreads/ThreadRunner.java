
package virtualthreads;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadRunner {

    private static final Logger logger = LoggerFactory.getLogger(ThreadRunner.class);
    private static final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    public static void runTasks(int numberOfTasks, boolean useVirtualThreads) {
        Timer.Sample sample = Timer.start(meterRegistry);

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

        String threadType = useVirtualThreads ? "Virtual" : "Traditional";
        Timer timer = meterRegistry.timer("thread.execution.time", "threadType", threadType);
        sample.stop(timer);

        logger.info("Tasks completed", "threadType", threadType, "durationMs", timer.totalTime(TimeUnit.MILLISECONDS));
    }
}
