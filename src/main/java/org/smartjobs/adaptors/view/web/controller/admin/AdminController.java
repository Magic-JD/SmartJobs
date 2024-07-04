package org.smartjobs.adaptors.view.web.controller.admin;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.service.CouponService;
import org.smartjobs.core.service.coupon.dto.EmailSendingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final CouponService couponService;


    @Autowired
    public AdminController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/console/landing")
    public String adminCosole(@AuthenticationPrincipal User user, Model model) {
        return "admin/admin";
    }

    @HxRequest
    @PostMapping("/coupon/issue")
    public String scoreAllCandidates(@AuthenticationPrincipal User user, @PathParam("emails") String emails, Model model) {
        List<EmailSendingResult> emailSendingResults = couponService.issueCouponsFor(Arrays.stream(emails.split(",")).map(String::trim).toList());
        model.addAttribute("results", emailSendingResults);
        return "admin/output";
    }
}