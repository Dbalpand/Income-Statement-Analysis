package com.divyesh.incomestatementanalysis.service;

/**
 * Service responsible for cleaning temporary uploaded files.
 */
public interface FileCleanupService {

    /**
     * Deletes old uploaded files from the configured
     * temporary upload directory.
     *
     * @return number of deleted files
     */
    int cleanupOldFiles();

}