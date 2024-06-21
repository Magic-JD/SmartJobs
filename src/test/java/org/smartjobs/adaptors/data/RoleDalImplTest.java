package org.smartjobs.adaptors.data;

import jakarta.persistence.Tuple;
import org.hibernate.sql.results.internal.TupleImpl;
import org.hibernate.sql.results.internal.TupleMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.*;
import org.smartjobs.adaptors.data.repository.data.Role;
import org.smartjobs.core.entities.UserScoringCriteria;
import org.smartjobs.core.ports.dal.RoleDal;
import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleDalImplTest {

    public static final Role DATABASE_ROLE = new Role(ROLE_ID, USER_ID, POSITION);
    public static final List<Role> DATABASE_ROLE_LIST = List.of(DATABASE_ROLE);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final RoleCriteriaRepository roleCriteriaRepository = mock(RoleCriteriaRepository.class);
    private final UserCriteriaRepository userCriteriaRepository = mock(UserCriteriaRepository.class);
    private final DefinedScoringCriteriaRepository definedScoringCriteriaRepository = mock(DefinedScoringCriteriaRepository.class);
    private final SelectedRoleRepository selectedRoleRepository = mock(SelectedRoleRepository.class);

    private final RoleDal roleDal = new RoleDalImpl(roleRepository, roleCriteriaRepository, userCriteriaRepository, definedScoringCriteriaRepository, selectedRoleRepository);


    private final ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);

    @Test
    void testGetUserRolesReturnsTheCorrectRolesForTheUser() {
        when(roleRepository.findByUserId(USER_ID)).thenReturn(DATABASE_ROLE_LIST);
        assertEquals(ROLE_DISPLAY_LIST, roleDal.getUserRoles(USER_ID));
    }

    @Test
    void testSaveRoleReturnsTheIdOfTheSavedRole() {
        when(roleRepository.saveAndFlush(roleArgumentCaptor.capture())).thenReturn(DATABASE_ROLE);
        long roleId = roleDal.saveRole(USER_ID, POSITION);
        assertEquals(ROLE_ID, roleId);
        Role role = roleArgumentCaptor.getValue();
        assertEquals(USER_ID, role.getUserId());
        assertEquals(POSITION, role.getPosition());
    }

    @Test
    void testGetRoleByIdReturnsTheCorrectRoleById() {
        Tuple tuple1 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_SCORE, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, false, (long) MAX_SCORE_VALUE, false}
        );
        Tuple tuple2 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_PASS, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, true, (long) MAX_SCORE_VALUE, false}
        );
        when(roleCriteriaRepository.findAllCriteriaByRoleId(ROLE_ID)).thenReturn(List.of(tuple1, tuple2));
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(DATABASE_ROLE));
        assertEquals(ROLE, roleDal.getRoleById(ROLE_ID));
    }

    @Test
    void testGetRoleByIdReturnsTheCorrectRoleByIdWithTheCorrectStringInterpolation() {
        Tuple tuple1 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{"Check for X", "this", USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), "Description of", false, (long) MAX_SCORE_VALUE, true}
        );
        when(roleCriteriaRepository.findAllCriteriaByRoleId(ROLE_ID)).thenReturn(List.of(tuple1));
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(DATABASE_ROLE));
        org.smartjobs.core.entities.Role role = roleDal.getRoleById(ROLE_ID);
        UserScoringCriteria userScoringCriteria = role.userScoringCriteria().getFirst();
        assertEquals("Check for this", userScoringCriteria.scoringGuide());
        assertEquals("Description of: this", userScoringCriteria.criteriaDescription());
    }

    @Test
    void delete() {
    }

    @Test
    void removeUserCriteriaFromRole() {
    }

    @Test
    void setSelectedRole() {
    }

    @Test
    void getCurrentlySelectedRoleById() {
    }

    @Test
    void deleteCurrentlySelectedRole() {
    }

    @Test
    void getCurrentlySelectedRole() {
    }

    @Test
    void createNewUserCriteriaForRole() {
    }

    @Test
    void countCriteriaForRole() {
    }

    @Test
    void getAllDefinedScoringCriteria() {
    }
}