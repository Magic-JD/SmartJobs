package org.smartjobs.core.exception.categories;

import lombok.Getter;

public class AsynchronousExceptions {

    @Getter
    public static class FileTypeNotSupportedException extends TextExtractionException {

        public FileTypeNotSupportedException(String fileName) {
            super(fileName);
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
