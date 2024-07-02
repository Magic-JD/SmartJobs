package org.smartjobs.adaptors.view.web.controller.login;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.core.service.UserRegistration;
import org.smartjobs.core.service.user.validation.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
                                      @ModelAttribute("user") UserDto userDto) {
        List<String> errors = userService.validateUser(userDto);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("user", userDto);
            return REGISTER;
        }
        return AWAIT_EMAIL;
    }

    @GetMapping("/verify/{uuid}")
    public String verifyAccount(Model model, HttpServletResponse response, @PathVariable String uuid) throws IOException {
        if (userService.createUser(uuid)) {
            response.sendRedirect("/login");
            return EMPTY_FRAGMENT;
        }
        response.sendRedirect("/error");
        return EMPTY_FRAGMENT;
    }
}
