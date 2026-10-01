package com.divyesh.incomestatementanalysis.validation;

import com.divyesh.incomestatementanalysis.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileSizeValidator {

    private static final long MAX_SIZE = 20 * 1024 * 1024;

    public void validate(MultipartFile file) {

        if (file.getSize() > MAX_SIZE) {

            throw new InvalidFileException(

                    "Maximum file size is 20 MB."

            );

        }

    }

}