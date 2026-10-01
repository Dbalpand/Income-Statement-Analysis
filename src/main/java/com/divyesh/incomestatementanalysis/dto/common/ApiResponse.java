package com.divyesh.incomestatementanalysis.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private LocalDateTime timestamp;

    private T data;

}