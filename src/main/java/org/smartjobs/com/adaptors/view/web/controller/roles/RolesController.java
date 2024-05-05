package org.smartjobs.com.adaptors.view.web.controller.roles;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.com.core.service.auth.AuthService;
import org.smartjobs.com.core.service.criteria.CriteriaService;
import org.smartjobs.com.core.service.role.RoleService;
import org.smartjobs.com.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.smartjobs.com.adaptors.view.web.constants.EventConstants.*;
import static org.smartjobs.com.adaptors.view.web.constants.HtmxConstants.HX_TRIGGER;
import static org.smartjobs.com.adaptors.view.web.constants.ThymeleafConstants.*;

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
        model.addAttribute("currentlySelected", roleService.getCurrentlySelectedRole(username).orElse(0L));
        return SAVED_ROLE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/template")
    public String roleTemplate() {
        return ROLE_TEMPLATE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/display/{roleId}")
    public String displayRole(@PathVariable("roleId") long roleId, HttpServletResponse response, Model model) {
        authService.getCurrentUsername();
        roleService.setCurrentlySelectedRole(authService.getCurrentUsername(), roleId);
        response.addHeader(HX_TRIGGER, ROLE_CHANGED);
        var internalRole = roleService.getRole(roleId);
        return prepareRoleDisplay(model, internalRole);
    }

    @HxRequest
    @GetMapping("/display")
    public String displayCurrentlySelectedRole(Model model) {
        String username = authService.getCurrentUsername();
        Optional<Long> currentlySelectedRole = roleService.getCurrentlySelectedRole(username);
        return currentlySelectedRole.map(roleId -> {
            var internalRole = roleService.getRole(roleId);
            return prepareRoleDisplay(model, internalRole);
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @DeleteMapping("/delete/{roleId}")
    public String deleteRole(@PathVariable("roleId") long roleId, HttpServletResponse response) {
        var username = authService.getCurrentUsername();
        roleService.getCurrentlySelectedRole(username)
                .filter(current -> current.equals(roleId))
                .ifPresent(_ -> roleService.deleteCurrentlySelectedRole(username));
        roleService.deleteRole(roleId);
        response.addHeader(HX_TRIGGER, ROLE_DELETED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @PostMapping("/create")
    public String createNewRole(@PathParam("position") String name, HttpServletResponse response, Model model) {
        var username = authService.getCurrentUsername();
        var internalRole = roleService.createRole(name, username);
        response.addHeader(HX_TRIGGER, ROLE_CHANGED);
        return prepareRoleDisplay(model, internalRole);
    }

    @HxRequest
    @GetMapping("/criteria/{category}")
    public String criteriaForCategory(@PathVariable("category") String category, Model model) {
        var criteria = criteriaService.getScoringCriteriaForCategory(CriteriaCategory.getFromName(category));
        model.addAttribute("criteria", criteria);
        return CATEGORY_CRITERIA_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/criteria/{criteriaId}")
    public String deleteCriteria(@PathVariable("criteriaId") Long criteriaId) {
        String username = authService.getCurrentUsername();
        Long roleId = roleService.getCurrentlySelectedRole(username).orElseThrow();
        roleService.removeCriteriaFromRole(roleId, criteriaId);
        criteriaService.deleteUserCriteria(criteriaId);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/select/{criteriaId}")
    public String selectCriteria(@PathVariable("criteriaId") long criteriaId, Model model) {
        var criteria = criteriaService.getCriteriaById(criteriaId);
        model.addAttribute("criteria", criteria);
        return SELECT_CRITERIA_FRAGMENT;
    }

    @HxRequest
    @PostMapping("/save/{criteriaId}")
    public String saveCriteria(@PathVariable("criteriaId") long criteriaId,
                               @PathParam("value") String value,
                               @PathParam("score") String score,
                               HttpServletResponse response) {
        var criteria = criteriaService.createUserCriteria(criteriaId, value, score);
        var username = authService.getCurrentUsername();
        Long roleId = roleService.getCurrentlySelectedRole(username).orElseThrow();
        roleService.addCriteriaToRole(roleId, criteria.id());
        response.addHeader(HX_TRIGGER, ROLE_UPDATED);
        return EMPTY_FRAGMENT;
    }

    private String prepareRoleDisplay(Model model, org.smartjobs.com.core.entities.Role internalRole) {
        var role = convertToControllerRole(internalRole);
        var categoryNames = Arrays.stream(CriteriaCategory.values()).map(CriteriaCategory::toString).sorted().toList();
        model.addAttribute("role", role);
        model.addAttribute("categories", categoryNames);
        return ROLE_FRAGMENT;
    }

    private Role convertToControllerRole(org.smartjobs.com.core.entities.Role internalRole) {
        CriteriaCategory[] values = CriteriaCategory.values();
        return new Role(
                internalRole.id(),
                internalRole.position(),
                Arrays.stream(values).map(cc ->
                                new Category(
                                        cc.toString(),
                                        internalRole.scoringCriteria()
                                                .stream()
                                                .filter(crit -> crit.category().equals(cc))
                                                .map(crit -> new ScoringCriteria(crit.id(), crit.criteria(), crit.weighting()))
                                                .toList()))
                        .toList());

    }

    private record Role(long id, String position, List<Category> categories) {
    }

    private record Category(String name, List<ScoringCriteria> criteria) {
    }

    private record ScoringCriteria(long id, String description, int weight) {
    }

}
