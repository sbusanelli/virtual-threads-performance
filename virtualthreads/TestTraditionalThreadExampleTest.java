import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class TestTraditionalThreadExampleTest {

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
        assertDoesNotThrow(() -> TraditionalThreadExample.main(new String[]{}));
        String output = outContent.toString();
        assertTrue(output.contains("Java 17 Traditional Threads Duration"), "Output should contain duration message");
        // Optionally, check that the duration is a positive number
        String[] parts = output.split(":");
        assertTrue(parts.length > 1, "Output should contain ':' separator");
        String msPart = parts[1].replaceAll("[^0-9]", "");
        assertFalse(msPart.isEmpty(), "Duration should be present");
        long duration = Long.parseLong(msPart);
        assertTrue(duration > 0, "Duration should be positive");
    }
}