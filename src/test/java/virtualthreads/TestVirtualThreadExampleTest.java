package virtualthreads;

import virtualthreads.VirtualThreadExample;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class TestVirtualThreadExampleTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testMainRunsAndPrintsDuration() {
        assertDoesNotThrow(() -> VirtualThreadExample.main(new String[]{}));
        String output = outContent.toString();
        assertTrue(output.contains("Virtual Threads:"), "Output should contain virtual threads message");
        assertTrue(output.contains("tasks in"), "Output should contain task count and duration");
        assertTrue(output.contains("tasks/sec"), "Output should contain tasks per second");
        assertTrue(output.contains("INFO VirtualThreadExample"), "Output should contain logger information");
    }
}
