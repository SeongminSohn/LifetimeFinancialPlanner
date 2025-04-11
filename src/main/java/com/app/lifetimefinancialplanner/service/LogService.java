package com.app.lifetimefinancialplanner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class LogService {
    // Logger instance for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private final Path logDirectory = Paths.get("logs/simulation");

    // Constructor that creates the log directory if it doesn't exist
    public LogService() {
        try {
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }
        } catch (IOException e) {
            logger.error("Could not create log directory", e);
        }
    }

    public void writeCsvLog(String fileName, String header, List<String> rows) {
        Path file = logDirectory.resolve(fileName);
        try {
            // Write the header row, creating or truncating the file
            Files.write(file, (header + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            // Append each row.
            for (String row : rows) {
                Files.write(file, (row + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            logger.error("Error writing CSV log file", e);
        }
    }

    public void writeTextLog(String fileName, String logEntry) {
        Path file = logDirectory.resolve(fileName);
        try {
            // Append the log entry (Create the log file if it doesn't exist)
            Files.write(file, (logEntry + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Error writing text log file", e);
        }
    }
}
