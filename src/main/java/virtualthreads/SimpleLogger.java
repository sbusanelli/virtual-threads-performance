package virtualthreads;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging implementation for demonstration purposes
 * In production, this would be replaced with SLF4J + Logback
 */
public class SimpleLogger {
    private final String name;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public SimpleLogger(String name) {
        this.name = name;
    }
    
    public static SimpleLogger getLogger(Class<?> clazz) {
        return new SimpleLogger(clazz.getSimpleName());
    }
    
    public static SimpleLogger getLogger(String name) {
        return new SimpleLogger(name);
    }
    
    public void info(String message) {
        System.out.println(String.format("%s [%s] INFO %s - %s", 
            LocalDateTime.now().format(formatter), 
            Thread.currentThread().getName(), 
            name, 
            message));
    }
    
    public void info(String format, Object... args) {
        String message = format;
        if (args != null && args.length > 0) {
            // Handle both {} and {:.2f} format placeholders
            message = message.replaceAll("\\{:\\.2f\\}", "{}");
            for (Object arg : args) {
                message = message.replaceFirst("\\{\\}", String.valueOf(arg));
            }
        }
        info(message);
    }
    
    public void warn(String message) {
        System.err.println(String.format("%s [%s] WARN %s - %s", 
            LocalDateTime.now().format(formatter), 
            Thread.currentThread().getName(), 
            name, 
            message));
    }
    
    public void warn(String format, Object... args) {
        String message = format;
        if (args != null && args.length > 0) {
            // Handle both {} and {:.2f} format placeholders
            message = message.replaceAll("\\{:\\.2f\\}", "{}");
            for (Object arg : args) {
                message = message.replaceFirst("\\{\\}", String.valueOf(arg));
            }
        }
        warn(message);
    }
    
    public void error(String message) {
        System.err.println(String.format("%s [%s] ERROR %s - %s", 
            LocalDateTime.now().format(formatter), 
            Thread.currentThread().getName(), 
            name, 
            message));
    }
    
    public void error(String message, Throwable throwable) {
        System.err.println(String.format("%s [%s] ERROR %s - %s", 
            LocalDateTime.now().format(formatter), 
            Thread.currentThread().getName(), 
            name, 
            message));
        throwable.printStackTrace();
    }
    
    public void error(String format, Object... args) {
        String message = format;
        if (args != null && args.length > 0) {
            // Handle both {} and {:.2f} format placeholders
            message = message.replaceAll("\\{:\\.2f\\}", "{}");
            for (Object arg : args) {
                message = message.replaceFirst("\\{\\}", String.valueOf(arg));
            }
        }
        error(message);
    }
    
    public void debug(String message) {
        // Debug logging disabled for now
        // System.out.println(String.format("%s [%s] DEBUG %s - %s", 
        //     LocalDateTime.now().format(formatter), 
        //     Thread.currentThread().getName(), 
        //     name, 
        //     message));
    }
    
    public void debug(String format, Object... args) {
        // Debug logging disabled for now
    }
}
