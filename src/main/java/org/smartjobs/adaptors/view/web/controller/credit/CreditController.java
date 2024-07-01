package org.smartjobs.adaptors.view.web.controller.credit;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.IncorrectCodeForTrialException;
import org.smartjobs.core.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/credit")
public class CreditController {


    private final CreditService creditService;

    @Autowired
    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @HxRequest
    @PostMapping("/trial")
    public String applyTrialCredits(@AuthenticationPrincipal User user, @RequestParam String code, Model model) {
        long userId = user.getId();
        if (false) { //Need to implement the database check
            creditService.credit(userId, 250);
            return "credit/applied";
        }
        throw new IncorrectCodeForTrialException(userId, code);
    }
}
