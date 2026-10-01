package com.divyesh.incomestatementanalysis.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    private final FileTypeValidator fileTypeValidator;
    private final FileSizeValidator fileSizeValidator;

    public FileValidator(FileTypeValidator fileTypeValidator,
                         FileSizeValidator fileSizeValidator) {

        this.fileTypeValidator = fileTypeValidator;
        this.fileSizeValidator = fileSizeValidator;
    }

    public void validate(MultipartFile file) {

        fileTypeValidator.validate(file);

        fileSizeValidator.validate(file);

    }

}