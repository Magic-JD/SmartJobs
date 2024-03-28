package org.smartjobs.com.exception.categories;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class AsynchronousExceptions {

    @Getter
    public static class FileTypeNotSupportedException extends RuntimeException {

        private final List<String> allowedFileTypes;
        private final String fileName;

        public FileTypeNotSupportedException(List<String> allowedFileTypes, String fileName) {
            super(STR. "\{ fileName } failed to upload as filetype is not supported" );
            this.allowedFileTypes = Collections.unmodifiableList(allowedFileTypes);
            this.fileName = fileName;
        }
    }

    @Getter
    public static class PdfTextExtractionException extends RuntimeException {
        private final String fileName;

        public PdfTextExtractionException(String fileName, Exception e) {
            super(STR. "An error occurred when processing file \{ fileName }" , e);
            this.fileName = fileName;
        }
    }

    @Getter
    public static class TxtTextExtractionException extends RuntimeException {
        private final String fileName;

        public TxtTextExtractionException(String fileName, Exception e) {
            super(STR. "An error occurred when processing file \{ fileName }" , e);
            this.fileName = fileName;
        }
    }

    public static class JustificationException extends RuntimeException {
        public JustificationException() {
            super("No justification was received.");
        }
    }
}
