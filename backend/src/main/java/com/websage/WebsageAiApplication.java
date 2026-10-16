package com.websage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class WebsageAiApplication {

    private static final Logger log = LoggerFactory.getLogger(WebsageAiApplication.class);

    public static void main(String[] args) {
        loadDotenv();
        SpringApplication.run(WebsageAiApplication.class, args);
    }

    /**
     * Automatically loads environment variables from a .env file located
     * either in the current directory or parent directory if present.
     */
    private static void loadDotenv() {
        Path[] candidatePaths = new Path[]{
                Paths.get(".env"),
                Paths.get("..", ".env"),
                Paths.get("backend", ".env")
        };

        for (Path path : candidatePaths) {
            if (Files.exists(path) && Files.isRegularFile(path)) {
                log.info("Loading environment configuration from: {}", path.toAbsolutePath().normalize());
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                            continue;
                        }
                        int equalIdx = trimmed.indexOf('=');
                        if (equalIdx > 0) {
                            String key = trimmed.substring(0, equalIdx).trim();
                            String value = trimmed.substring(equalIdx + 1).trim();

                            // Strip surrounding single or double quotes
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }

                            // If not already defined in system environment, set it as a system property
                            if (System.getenv(key) == null && System.getProperty(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                    return;
                } catch (IOException e) {
                    log.warn("Could not read .env file at {}: {}", path, e.getMessage());
                }
            }
        }
    }
}
