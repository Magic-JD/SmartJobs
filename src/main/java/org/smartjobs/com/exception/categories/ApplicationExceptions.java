package org.smartjobs.com.exception.categories;

public class ApplicationExceptions {
    public static class GptClientConnectionFailure extends RuntimeException {
        public GptClientConnectionFailure(Exception e) {
            super("Failure to connect to GPT client. Please verify the url is set correctly and then restart the service.", e);
        }
    }
}
