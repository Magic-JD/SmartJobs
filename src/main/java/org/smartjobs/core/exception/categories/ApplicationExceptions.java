package org.smartjobs.core.exception.categories;

public class ApplicationExceptions {

    private ApplicationExceptions() {
        // Private to prevent instantiation.
    }

    public static class GptClientConnectionFailure extends RuntimeException {
        public GptClientConnectionFailure(Exception e, String url) {
            super(STR. "Failure to connect to GPT client. Please verify the url (\{ url }) is set correctly and then restart the service." , e);
        }
    }

    public static class DatabaseEnumCompatibilityException extends RuntimeException {
        public DatabaseEnumCompatibilityException(String fromDb, String enumName) {
            super(STR. "The string \{ fromDb } could not be parsed for enum \{ enumName }. Please check that the data is correct and restart the service." );
        }
    }

    public static class IncorrectIdForDefinedScoringCriteriaException extends RuntimeException {
        public IncorrectIdForDefinedScoringCriteriaException(long id) {
            super(STR. "No defined scoring criteria could be found for id \{ id }" );
        }
    }

    public static class IncorrectIdForRoleRetrievalException extends RuntimeException {
        public IncorrectIdForRoleRetrievalException(long id) {
            super(STR. "No role could be found for id \{ id }" );
        }
    }

    public static class HashKnownButCvNotFound extends RuntimeException {
        public HashKnownButCvNotFound(String hash) {
            super(STR. "The hash \{ hash } was found in the database but the matching cv was not." );
        }
    }
}
