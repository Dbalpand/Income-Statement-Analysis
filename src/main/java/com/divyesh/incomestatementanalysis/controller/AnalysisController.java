package com.divyesh.incomestatementanalysis.controller;

import com.divyesh.incomestatementanalysis.dto.ApiResponse;
import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.service.AnalysisService;
import com.divyesh.incomestatementanalysis.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final HistoryService historyService;

    /**
     * Endpoint to upload an Income Statement PDF/Image and extract structured data.
     *
     * POST /api/v1/analysis/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<IncomeStatement>> analyzeIncomeStatement(
            @RequestParam("file") MultipartFile file) {

        log.info("Received file upload request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File cannot be empty. Please upload a valid document."));
        }

        IncomeStatement incomeStatement = analysisService.analyze(file);

        return ResponseEntity.ok(
                ApiResponse.success("Income statement analyzed and processed successfully.", incomeStatement)
        );
    }

    /**
     * Endpoint to fetch all analysis history records.
     *
     * GET /api/v1/analysis/history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AnalysisHistory>>> getAllHistory() {
        List<AnalysisHistory> historyList = historyService.getAllHistory();
        return ResponseEntity.ok(
                ApiResponse.success("Fetched all analysis history records.", historyList)
        );
    }

    /**
     * Endpoint to fetch a single analysis history record by ID.
     *
     * GET /api/v1/analysis/history/{id}
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<ApiResponse<AnalysisHistory>> getHistoryById(@PathVariable Long id) {
        AnalysisHistory history = historyService.getHistoryById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Fetched analysis history record.", history)
        );
    }
}