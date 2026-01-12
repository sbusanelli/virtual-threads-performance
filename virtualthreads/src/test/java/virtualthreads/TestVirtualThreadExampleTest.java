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
        assertTrue(output.contains("Tasks completed"), "Output should contain log message");
    }
}
