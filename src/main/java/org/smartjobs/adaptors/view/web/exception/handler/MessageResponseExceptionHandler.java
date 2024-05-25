package org.smartjobs.adaptors.view.web.exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.adaptors.view.web.constants.ThymeleafConstants;
import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.ERROR_BOX;

@ControllerAdvice
public class MessageResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private final SseService sseService;

    private final TemplateEngine templateEngine;

    public MessageResponseExceptionHandler(SseService sseService, TemplateEngine templateEngine) {
        this.sseService = sseService;
        this.templateEngine = templateEngine;
    }


    @ExceptionHandler(value = NotEnoughCreditException.class)
    protected String handleNotEnoughCreditException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "You do not have enough credits to perform that action", response);
    }

    @ExceptionHandler(value = IncorrectAuthenticationException.class)
    protected String handleIncorrectAuthenticationException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "There is an issue with your login. Please log out and log in again.", response);
    }


    @ExceptionHandler(value = NoValueProvidedException.class)
    protected String handleNoValueProvidedException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "You must provide a value for the criteriaDescription.", response);
    }

    @ExceptionHandler(value = NoScoreProvidedException.class)
    protected String handleNoScoreProvidedException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "You must provide a score for the criteriaDescription.", response);
    }

    @ExceptionHandler(value = ScoreIsNotNumberException.class)
    protected String handleScoreNotNumberException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "The score must be a number.", response);
    }

    @ExceptionHandler(value = NoRoleSelectedException.class)
    protected String handleNoRoleSelectedException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "This operation requires a selected role.", response);
    }

    @ExceptionHandler(value = NoCandidatesSelectedException.class)
    protected String handleNoCandidatesSelectedException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "This operation requires at least one selected candidate.", response);
    }

    @ExceptionHandler(value = RoleCriteriaLimitReachedException.class)
    protected String handleRoleCriteriaLimitReachedException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), ex.getMessage(), response);
    }

    @ExceptionHandler(value = RoleHasNoCriteriaException.class)
    protected String handleRoleHasNoCriteriaException(UserResolvedExceptions ex, HttpServletResponse response, Model model) {
        return handleError(ex.getUserId(), "There is no criteria selected for this role. Please select at least one criteria.", response);
    }

    private String handleError(long userId, String message, HttpServletResponse response) {
        Context context = new Context();
        context.setVariable("message", message);
        String errorBoxHtml = templateEngine.process(ERROR_BOX, context);
        sseService.send(userId, "message", errorBoxHtml);
        response.setStatus(422);
        return ThymeleafConstants.EMPTY_FRAGMENT;
    }
}
