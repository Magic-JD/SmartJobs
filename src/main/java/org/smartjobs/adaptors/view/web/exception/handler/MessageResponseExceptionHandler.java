package org.smartjobs.adaptors.view.web.exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.smartjobs.adaptors.view.web.exception.message.ExceptionMessage.createUserErrorMessageToDisplayForUser;

@ControllerAdvice
public class MessageResponseExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = NotEnoughCreditException.class)
    protected String handleNotEnoughCreditException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("You do not have enough credits to perform that action", response, model);
    }

    @ExceptionHandler(value = IncorrectAuthenticationException.class)
    protected String handleIncorrectAuthenticationException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("There is an issue with your login. Please log out and log in again.", response, model);
    }


    @ExceptionHandler(value = NoValueProvidedException.class)
    protected String handleNoValueProvidedException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("You must provide a value for the name.", response, model);
    }

    @ExceptionHandler(value = NoScoreProvidedException.class)
    protected String handleNoScoreProvidedException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("You must provide a score for the name.", response, model);
    }

    @ExceptionHandler(value = ScoreIsNotNumberException.class)
    protected String handleScoreNotNumberException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("The score must be a number.", response, model);
    }

    @ExceptionHandler(value = NoRoleSelectedException.class)
    protected String handleNoRoleSelectedException(HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser("This operation requires a selected role.", response, model);
    }

    @ExceptionHandler(value = RoleCriteriaLimitReachedException.class)
    protected String handleRoleCriteriaLimitReachedException(RuntimeException ex, HttpServletResponse response, Model model) {
        return createUserErrorMessageToDisplayForUser(ex.getMessage(), response, model);
    }
}
