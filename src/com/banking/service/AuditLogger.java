package com.banking.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private static final String LOG_FILE = "data/audit.log";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String event) {

        try {

            java.io.File file =
                    new java.io.File(LOG_FILE);

            java.io.File parent =
                    file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (PrintWriter writer =
                         new PrintWriter(
                                 new FileWriter(file, true))) {

                writer.println(
                        "[" +
                        LocalDateTime.now().format(FORMATTER) +
                        "] " +
                        event
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error writing audit log: "
                            + e.getMessage()
            );
        }
    }
}