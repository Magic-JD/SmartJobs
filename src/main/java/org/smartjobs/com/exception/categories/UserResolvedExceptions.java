package org.smartjobs.com.exception.categories;

public class UserResolvedExceptions {

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
}
