package com.divyesh.incomestatementanalysis.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AnalysisRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

}