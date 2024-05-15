package org.smartjobs.adaptors.view.web.exception.message;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;

import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.*;
import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.ERROR_BOX;
import static org.springframework.http.HttpStatus.OK;

public class ExceptionMessage {

    private ExceptionMessage() {
        //Empty Constructor to prevent instantiation.
    }

    public static String createUserErrorMessageToDisplayForUser(String message, HttpServletResponse response, Model model) {
        model.addAttribute("errorMessage", message);
        response.addHeader(HX_RETARGET, "#error");
        response.addHeader(HX_RESWAP, OUTER_HTML);
        response.setStatus(OK.value());
        return ERROR_BOX;
    }
}
