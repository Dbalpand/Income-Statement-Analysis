package com.divyesh.incomestatementanalysis.validation;

import com.divyesh.incomestatementanalysis.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FileTypeValidator {

    private static final Set<String> ALLOWED_TYPES = Set.of(

            "application/pdf",

            "image/png",

            "image/jpeg",

            "image/jpg"

    );

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new InvalidFileException("Uploaded file is empty.");

        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {

            throw new InvalidFileException("Only PDF, PNG and JPG files are allowed.");

        }

    }

}