package org.smartjobs.com.controller.roles;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.smartjobs.com.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.smartjobs.com.constants.ThymeleafConstants.ROLE_FRAGMENT;

@Controller
@RequestMapping("/roles")
public class RolesController {


    private final RoleService roleService;

    @Autowired
    public RolesController(RoleService roleService) {
        this.roleService = roleService;
    }

    @HxRequest
    @GetMapping("/display/{roleId}")
    public String displayRole(@PathVariable("roleId") long roleId, Model model) {
        model.addAttribute("role", roleService.getRole());
        return ROLE_FRAGMENT;
    }

}
