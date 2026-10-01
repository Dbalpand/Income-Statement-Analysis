package com.divyesh.incomestatementanalysis.util;

import org.springframework.web.multipart.MultipartFile;

public final class ValidationUtil {

    private ValidationUtil() {

    }

    public static boolean isEmpty(MultipartFile file) {

        return file == null || file.isEmpty();

    }

    public static boolean hasText(String value) {

        return value != null && !value.trim().isEmpty();

    }

}