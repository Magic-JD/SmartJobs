package org.smartjobs.com.service.role;

import org.smartjobs.com.dal.RoleDao;
import org.smartjobs.com.service.role.data.CriteriaCategory;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RoleService {

    private final RoleDao roleDao;

    private HashMap<Long, Role> map = new HashMap<>();

    @Autowired
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;

        List<ScoringCriteria> scoringCriteria = List.of(
                new ScoringCriteria(CriteriaCategory.HARD_SKILLS, "Degree in the field : Computer Science", 10, "prompt value")
        );
        map.put(1L, new Role("Senior Backend Engineer", scoringCriteria));
        map.put(123L, new Role("Software Developer", scoringCriteria));
        map.put(456L, new Role("Teacher", scoringCriteria));


    }

    public Role getRole(long id) {
        return map.get(id);
    }

    public List<RoleDisplay> getUserRoles(String username) {
        //return roleDao.getUserRoles(username);
        return List.of(new RoleDisplay(123, "Software Developer"), new RoleDisplay(456, "Teacher"));
    }
}
