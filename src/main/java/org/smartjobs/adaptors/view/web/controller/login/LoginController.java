package org.smartjobs.adaptors.view.web.controller.login;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.core.service.UserRegistration;
import org.smartjobs.core.service.user.validation.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.HX_REDIRECT;
import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserRegistration userService;

    @Autowired
    public LoginController(UserRegistration userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showLogin(Model model) {
        return LOGIN_PAGE;
    }

    @GetMapping(params = {"error"})
    public String showLoginWithError(Model model) {
        model.addAttribute("error", true);
        return LOGIN_PAGE;
    }


    @HxRequest
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto("", "", "");
        model.addAttribute("user", userDto);
        model.addAttribute("errors", Collections.emptyList());
        return REGISTER;
    }

    @HxRequest
    @PostMapping("/registration")
    public String registerUserAccount(Model model,
                                      @ModelAttribute("user") UserDto userDto,
                                      HttpServletResponse response) {
        List<String> errors = userService.registerNewUserAccount(userDto);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return REGISTER;
        }
        response.addHeader(HX_REDIRECT, "/login");
        return EMPTY_FRAGMENT;
    }
}
