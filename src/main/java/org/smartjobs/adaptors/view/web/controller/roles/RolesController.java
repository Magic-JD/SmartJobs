package org.smartjobs.adaptors.view.web.controller.roles;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.service.CriteriaService;
import org.smartjobs.core.service.RoleService;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.smartjobs.adaptors.view.web.constants.EventConstants.*;
import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.HX_TRIGGER;
import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/roles")
public class RolesController {


    private final RoleService roleService;
    private final CriteriaService criteriaService;

    @Autowired
    public RolesController(RoleService roleService, CriteriaService criteriaService) {
        this.roleService = roleService;
        this.criteriaService = criteriaService;
    }

    @HxRequest
    @GetMapping("/saved")
    public String savedRoles(@AuthenticationPrincipal User user, Model model) {
        var userId = user.getId();
        model.addAttribute("savedRoles", roleService.getUserRoles(userId));
        model.addAttribute("currentlySelected", roleService.getCurrentlySelectedRoleId(userId).orElse(0L));
        return SAVED_ROLE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/template")
    public String roleTemplate() {
        return ROLE_TEMPLATE_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/display/{roleId}")
    public String displayRole(@AuthenticationPrincipal User user, @PathVariable("roleId") long roleId, HttpServletResponse response, Model model) {
        roleService.setCurrentlySelectedRole(user.getId(), roleId);
        response.addHeader(HX_TRIGGER, ROLE_CHANGED);
        var internalRole = roleService.getRole(roleId);
        return prepareRoleDisplay(model, internalRole);
    }

    @HxRequest
    @GetMapping("/display")
    public String displayCurrentlySelectedRole(@AuthenticationPrincipal User user, Model model) {
        Optional<Long> currentlySelectedRole = roleService.getCurrentlySelectedRoleId(user.getId());
        return currentlySelectedRole.map(roleId -> {
            var internalRole = roleService.getRole(roleId);
            return prepareRoleDisplay(model, internalRole);
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @DeleteMapping("/delete/{roleId}")
    public String deleteRole(@AuthenticationPrincipal User user, @PathVariable("roleId") long roleId, HttpServletResponse response) {
        var userId = user.getId();
        roleService.getCurrentlySelectedRoleId(userId)
                .filter(current -> current.equals(roleId))
                .ifPresent(_ -> roleService.deleteCurrentlySelectedRole(userId));
        roleService.deleteRole(userId, roleId);
        response.addHeader(HX_TRIGGER, ROLE_DELETED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @PostMapping("/create")
    public String createNewRole(@AuthenticationPrincipal User user, @PathParam("position") String name, HttpServletResponse response, Model model) {
        var userId = user.getId();
        var internalRole = roleService.createRole(name, userId);
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
    public String deleteCriteria(@AuthenticationPrincipal User user, @PathVariable("criteriaId") Long criteriaId) {
        Long roleId = roleService.getCurrentlySelectedRoleId(user.getId()).orElseThrow();
        roleService.removeCriteriaFromRole(roleId, criteriaId);
        criteriaService.deleteUserCriteria(criteriaId);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/select/{criteriaId}")
    public String selectCriteria(@PathVariable("criteriaId") long criteriaId, Model model) {
        var criteria = criteriaService.getCriteriaById(criteriaId);
        model.addAttribute("criteria", criteria);
        model.addAttribute("placeholderText", criteria.inputExample().orElse(""));
        return SELECT_CRITERIA_FRAGMENT;
    }

    @HxRequest
    @PostMapping("/save/{criteriaId}")
    public String saveCriteria(@AuthenticationPrincipal User user, @PathVariable("criteriaId") long criteriaId,
                               @PathParam("value") String value,
                               @PathParam("score") String score,
                               HttpServletResponse response) {
        var criteria = criteriaService.createUserCriteria(criteriaId, value, score);
        var roleId = roleService.getCurrentlySelectedRoleId(user.getId()).orElseThrow();
        roleService.addCriteriaToRole(roleId, criteria.id());
        response.addHeader(HX_TRIGGER, ROLE_UPDATED);
        return EMPTY_FRAGMENT;
    }

    private String prepareRoleDisplay(Model model, org.smartjobs.core.entities.Role internalRole) {
        var role = convertToControllerRole(internalRole);
        var categoryNames = Arrays.stream(CriteriaCategory.values()).map(CriteriaCategory::toString).sorted().toList();
        model.addAttribute("role", role);
        model.addAttribute("categories", categoryNames);
        return ROLE_FRAGMENT;
    }

    private Role convertToControllerRole(org.smartjobs.core.entities.Role internalRole) {
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
                                                .map(crit -> new ScoringCriteria(crit.id(), crit.name(), crit.weighting()))
                                                .toList()))
                        .toList());

    }

    public record Role(long id, String position, List<Category> categories) {
    }

    public record Category(String name, List<ScoringCriteria> criteria) {
    }

    public record ScoringCriteria(long id, String description, int weight) {
    }

}
