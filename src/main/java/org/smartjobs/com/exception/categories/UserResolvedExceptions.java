package org.smartjobs.com.exception.categories;

public class UserResolvedExceptions {

    private UserResolvedExceptions() {
        // Private constructor to prevent class instantiation.
    }

    public static class NotEnoughCreditException extends RuntimeException {
        public NotEnoughCreditException() {
            super("User does not have enough credits");
        }
    }

    public static class IncorrectAuthenticationException extends RuntimeException {
        public IncorrectAuthenticationException() {
            super("The current username could not be found.");
        }
    }

    public static class NoScoreProvidedException extends RuntimeException {
        public NoScoreProvidedException() {
            super("No Score was provided.");
        }
    }

    public static class NoValueProvidedException extends RuntimeException {
        public NoValueProvidedException() {
            super("No Value was provided.");
        }
    }

    public static class ScoreIsNotNumberException extends RuntimeException {
        public ScoreIsNotNumberException() {
            super("Score is not number.");
        }
    }
}
