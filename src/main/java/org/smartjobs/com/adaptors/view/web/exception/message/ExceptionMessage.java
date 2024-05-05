package org.smartjobs.com.adaptors.view.web.exception.message;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.smartjobs.com.adaptors.view.web.constants.HtmxConstants.HX_RESWAP;
import static org.smartjobs.com.adaptors.view.web.constants.HtmxConstants.HX_RETARGET;

public class ExceptionMessage {

    private ExceptionMessage() {
        //Empty Constructor to prevent instantiation.
    }

    public static String createUserErrorMessageToDisplayForUser(String message, HttpServletResponse response, Model model) {
        model.addAttribute("errorMessage", message);
        response.addHeader(HX_RETARGET, "#error");
        response.addHeader(HX_RESWAP, "outerHTML");
        response.setStatus(HttpStatus.OK.value());
        return "error-box";
    }
}
