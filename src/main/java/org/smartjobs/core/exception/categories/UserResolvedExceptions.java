package org.smartjobs.core.exception.categories;

import lombok.Getter;

public class UserResolvedExceptions extends RuntimeException {


    @Getter
    private final long userId;

    public UserResolvedExceptions(long userId, String message) {
        super(message);
        this.userId = userId;
    }

    public static class NotEnoughCreditException extends UserResolvedExceptions {
        public NotEnoughCreditException(long userId) {
            super(userId, "User does not have enough credits");
        }
    }

    public static class IncorrectAuthenticationException extends UserResolvedExceptions {
        public IncorrectAuthenticationException(long userId) {
            super(userId, "The current username could not be found.");
        }
    }

    public static class NoScoreProvidedException extends UserResolvedExceptions {
        public NoScoreProvidedException(long userId) {
            super(userId, "No Score was provided.");
        }
    }

    public static class NoValueProvidedException extends UserResolvedExceptions {
        public NoValueProvidedException(long userId) {
            super(userId, "No Value was provided.");
        }
    }

    public static class ScoreIsNotNumberException extends UserResolvedExceptions {
        public ScoreIsNotNumberException(long userId) {
            super(userId, "Score is not number.");
        }
    }

    public static class NoRoleSelectedException extends UserResolvedExceptions {

        public NoRoleSelectedException(long userId) {
            super(userId, "A role must be selected for this operation");
        }
    }

    public static class NoCandidatesSelectedException extends UserResolvedExceptions {

        public NoCandidatesSelectedException(long userId) {
            super(userId, "At least one candidate must be selected for this operation");
        }
    }

    public static class RoleCriteriaLimitReachedException extends UserResolvedExceptions {

        public RoleCriteriaLimitReachedException(long userId, int criteriaLimit) {
            super(userId, STR. "The criteria limit is \{ criteriaLimit } - Please remove existing criteria before adding more." );
        }
    }

    public static class RoleHasNoCriteriaException extends UserResolvedExceptions {

        public RoleHasNoCriteriaException(long userId) {
            super(userId, "There is no criteria selected for this role. Please select at least one criteria.");
        }
    }
}
