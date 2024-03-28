package org.smartjobs.com.exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.com.exception.categories.UserResolvedExceptions.IncorrectAuthenticationException;
import org.smartjobs.com.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.smartjobs.com.exception.message.ExceptionMessage.createUserErrorMessageToDisplayForUser;

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

}
