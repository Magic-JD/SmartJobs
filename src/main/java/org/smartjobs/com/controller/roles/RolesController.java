package org.smartjobs.com.controller.roles;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.smartjobs.com.constants.ThymeleafConstants.ROLE_FRAGMENT;
import static org.smartjobs.com.constants.ThymeleafConstants.SAVED_ROLE_FRAGMENT;

@Controller
@RequestMapping("/roles")
public class RolesController {


    private final RoleService roleService;
    private final AuthService authService;

    @Autowired
    public RolesController(RoleService roleService, AuthService authService) {
        this.roleService = roleService;
        this.authService = authService;
    }

    @HxRequest
    @GetMapping("/saved")
    public String savedRoles(Model model) {
        var username = authService.getCurrentUsername();
        model.addAttribute("savedRoles", roleService.getUserRoles(username));
        return SAVED_ROLE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/display/{roleId}")
    public String displayRole(@PathVariable("roleId") long roleId, Model model) {
        var internalRole = roleService.getRole(roleId);
        var role = new Role(internalRole.position(),
                internalRole.scoringCriteria().stream()
                        .collect(Collectors.groupingBy(org.smartjobs.com.service.role.data.ScoringCriteria::category))
                        .entrySet().stream()
                        .map(entry -> new Category(formatToCase(entry.getKey().name()),
                                entry.getValue()
                                        .stream()
                                        .map(sc -> new ScoringCriteria(sc.description(), sc.weight()))
                                        .sorted(Comparator.comparing(ScoringCriteria::description)).toList()))
                        .sorted(Comparator.comparing(Category::name)).toList());
        model.addAttribute("role", role);
        return ROLE_FRAGMENT;
    }

    private String formatToCase(String word) {
        return word.charAt(0) + word.substring(1).toLowerCase();
    }

    private record Role(String position, List<Category> categories) {
    }

    private record Category(String name, List<ScoringCriteria> criteria) {
    }

    private record ScoringCriteria(String description, int weight) {
    }

}
