package org.smartjobs.com.controller.roles;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.criteria.CriteriaService;
import org.smartjobs.com.service.role.RoleService;
import org.smartjobs.com.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.smartjobs.com.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/roles")
public class RolesController {


    private final RoleService roleService;
    private final AuthService authService;
    private final CriteriaService criteriaService;

    @Autowired
    public RolesController(RoleService roleService, AuthService authService, CriteriaService criteriaService) {
        this.roleService = roleService;
        this.authService = authService;
        this.criteriaService = criteriaService;
    }

    @HxRequest
    @GetMapping("/saved")
    public String savedRoles(Model model) {
        var username = authService.getCurrentUsername();
        model.addAttribute("savedRoles", roleService.getUserRoles(username));
        return SAVED_ROLE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/template")
    public String roleTemplate() {
        return ROLE_TEMPLATE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/display/{roleId}")
    public String displayRole(@PathVariable("roleId") long roleId, Model model) {
        var internalRole = roleService.getRole(roleId);
        return prepareRoleDisplay(model, internalRole);
    }

    @HxRequest
    @DeleteMapping("/delete/{roleId}")
    public String deleteRole(@PathVariable("roleId") long roleId) {
        roleService.deleteRole(roleId);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @PostMapping("/create")
    public String createNewRole(@PathParam("position") String name, HttpServletResponse response, Model model) {
        var username = authService.getCurrentUsername();
        var internalRole = roleService.createRole(name, username);
        response.addHeader("HX-Trigger", "role-changed");
        return prepareRoleDisplay(model, internalRole);
    }

    private String prepareRoleDisplay(Model model, org.smartjobs.com.service.role.data.Role internalRole) {
        var role = convertToControllerRole(internalRole);
        var categoryNames = Arrays.stream(CriteriaCategory.values()).map(CriteriaCategory::toString).sorted().toList();
        model.addAttribute("role", role);
        model.addAttribute("categories", categoryNames);
        return ROLE_FRAGMENT;
    }

    private Role convertToControllerRole(org.smartjobs.com.service.role.data.Role internalRole) {
        return new Role(internalRole.position(),
                internalRole.scoringCriteria().stream()
                        .collect(Collectors.groupingBy(org.smartjobs.com.service.role.data.ScoringCriteria::category))
                        .entrySet().stream()
                        .map(entry -> new Category(entry.getKey().toString(),
                                entry.getValue()
                                        .stream()
                                        .map(sc -> new ScoringCriteria(sc.criteria(), sc.weighting()))
                                        .sorted(Comparator.comparing(ScoringCriteria::description)).toList()))
                        .sorted(Comparator.comparing(Category::name)).toList());
    }

    private record Role(String position, List<Category> categories) {
    }

    private record Category(String name, List<ScoringCriteria> criteria) {
    }

    private record ScoringCriteria(String description, int weight) {
    }

}
