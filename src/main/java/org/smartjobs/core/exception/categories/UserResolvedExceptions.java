package org.smartjobs.core.exception.categories;

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

    public static class NoRoleSelectedException extends RuntimeException {

        public NoRoleSelectedException() {
            super("A role must be selected for this operation");
        }
    }

    public static class RoleCriteriaLimitReachedException extends RuntimeException {

        public RoleCriteriaLimitReachedException(int criteriaLimit) {
            super(STR. "The criteria limit is \{ criteriaLimit } - Please remove existing criteria before adding more." );
        }
    }

    public static class RoleHasNoCriteriaException extends RuntimeException {

        public RoleHasNoCriteriaException() {
            super("There is no criteria selected for this role. Please select at least one criteria.");
        }
    }
}
