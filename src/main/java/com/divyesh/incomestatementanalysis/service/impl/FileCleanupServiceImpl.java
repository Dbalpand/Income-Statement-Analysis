package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.service.FileCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileCleanupServiceImpl implements FileCleanupService {

    @Value("${file.upload.temp-directory}")
    private String uploadDirectory;

    @Value("${file.cleanup.max-age-hours:24}")
    private long maxAgeHours;

    @Override
    public int cleanupOldFiles() {

        Path directory = Paths.get(uploadDirectory);

        if (!Files.exists(directory)) {

            log.warn("Upload directory does not exist: {}", directory);

            return 0;
        }

        int deletedFiles = 0;

        try (Stream<Path> files = Files.list(directory)) {

            Instant now = Instant.now();

            for (Path file : files.toList()) {

                if (!Files.isRegularFile(file)) {
                    continue;
                }

                FileTime lastModified =
                        Files.getLastModifiedTime(file);

                long age =
                        Duration.between(
                                lastModified.toInstant(),
                                now
                        ).toHours();

                if (age >= maxAgeHours) {

                    try {

                        Files.deleteIfExists(file);

                        deletedFiles++;

                        log.info(
                                "Deleted temporary file: {}",
                                file.getFileName()
                        );

                    } catch (IOException exception) {

                        log.error(
                                "Unable to delete file: {}",
                                file.getFileName(),
                                exception
                        );

                    }

                }

            }

        } catch (IOException exception) {

            log.error(
                    "Failed to clean upload directory.",
                    exception
            );

        }

        log.info(
                "File cleanup completed. Total deleted files: {}",
                deletedFiles
        );

        return deletedFiles;

    }

}