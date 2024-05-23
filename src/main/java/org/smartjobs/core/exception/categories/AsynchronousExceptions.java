package org.smartjobs.core.exception.categories;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class AsynchronousExceptions {

    @Getter
    public static class FileTypeNotSupportedException extends TextExtractionException {

        private final List<String> allowedFileTypes;

        public FileTypeNotSupportedException(List<String> allowedFileTypes, String fileName) {
            super(fileName);
            this.allowedFileTypes = Collections.unmodifiableList(allowedFileTypes);
        }
    }

    @Getter
    public static class TextExtractionException extends RuntimeException {

        private final String fileName;

        public TextExtractionException(String fileName) {
            super(STR. "An error occurred when processing file \{ fileName }" );
            this.fileName = fileName;
        }

        public TextExtractionException(String fileName, Exception e) {
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
