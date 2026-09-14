package com.banking.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {

    private static final String LOG_FILE = "data/audit.log";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String event) {

        if (event == null || event.trim().isEmpty()) {
            return;
        }

        try {

            File file = new File(LOG_FILE);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (PrintWriter writer =
                         new PrintWriter(
                                 new FileWriter(file, true))) {

                writer.println(
                        "[" + LocalDateTime.now().format(FORMATTER)
                                + "] " + event
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error writing audit log: "
                            + e.getMessage()
            );
        }
    }

    public List<String> getLogs() {

        List<String> logs = new ArrayList<>();

        File file = new File(LOG_FILE);

        if (!file.exists()) {
            return logs;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.trim().isEmpty()) {
                    logs.add(line);
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error reading audit log: "
                            + e.getMessage()
            );
        }

        return logs;
    }

    public List<String> getLogsContaining(
            String keyword) {

        List<String> matchingLogs =
                new ArrayList<>();

        if (keyword == null
                || keyword.trim().isEmpty()) {
            return matchingLogs;
        }

        String searchText =
                keyword.toLowerCase();

        for (String log : getLogs()) {

            if (log.toLowerCase()
                    .contains(searchText)) {

                matchingLogs.add(log);
            }
        }

        return matchingLogs;
    }

    public void displayLogs() {

        displayLogs(getLogs());
    }

    public void displayLogs(
            List<String> logs) {

        System.out.println(
                "\n========== AUDIT LOG =========="
        );

        if (logs == null || logs.isEmpty()) {

            System.out.println(
                    "No audit activity found."
            );

        } else {

            for (String log : logs) {
                System.out.println(log);
            }
        }

        System.out.println(
                "==============================="
        );
    }

    public String getLogFilePath() {

        return LOG_FILE;
    }
}
