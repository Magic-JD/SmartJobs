package org.smartjobs.com.exception.message;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;

public class ExceptionMessage {

    private ExceptionMessage() {
        //Empty Constructor to prevent instantiation.
    }

    public static String createUserErrorMessageToDisplayForUser(String message, HttpServletResponse response, Model model) {
        model.addAttribute("errorMessage", message);
        response.addHeader("HX-Retarget", "#error");
        response.addHeader("HX-Reswap", "outerHTML");
        response.setStatus(200);
        return "error-box";
    }
}
