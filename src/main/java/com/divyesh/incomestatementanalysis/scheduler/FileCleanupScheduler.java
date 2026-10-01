package com.divyesh.incomestatementanalysis.scheduler;

import com.divyesh.incomestatementanalysis.service.FileCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

    private final FileCleanupService fileCleanupService;

    /**
     * Automatically cleans temporary uploaded files.
     *
     * Runs every day at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupUploadedFiles() {

        log.info("==================================================");
        log.info("Starting scheduled uploaded file cleanup...");
        log.info("==================================================");

        long startTime = System.currentTimeMillis();

        try {

            int deletedFiles = fileCleanupService.cleanupOldFiles();

            long executionTime =
                    System.currentTimeMillis() - startTime;

            log.info("Cleanup completed successfully.");
            log.info("Deleted Files : {}", deletedFiles);
            log.info("Execution Time : {} ms", executionTime);

        } catch (Exception exception) {

            log.error(
                    "Scheduled file cleanup failed.",
                    exception
            );

        }

        log.info("==================================================");

    }

}