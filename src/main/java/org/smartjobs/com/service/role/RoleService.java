package org.smartjobs.com.service.role;

import org.smartjobs.com.dal.RoleDao;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static org.smartjobs.com.service.role.data.CriteriaCategory.EDUCTATION;
import static org.smartjobs.com.service.role.data.CriteriaCategory.EXPERIENCE;

@Service
public class RoleService {

    private final RoleDao roleDao;

    private HashMap<Long, Role> map = new HashMap<>();

    @Autowired
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;

        List<ScoringCriteria> scoringCriteria = List.of(
                new ScoringCriteria("Degree in Computer Science or related field", 10, EDUCTATION, true),
                new ScoringCriteria("Advanced degree (MSc, Ph.D.) in computer science or related field", 5, EDUCTATION, true),
                new ScoringCriteria("Relevant certifications (e.g., AWS Certified Developer, Microsoft Certified: Azure Developer Associate)", 5, EXPERIENCE, true),
                new ScoringCriteria("Proficiency in Java and Python", 10, EXPERIENCE, false),
                new ScoringCriteria("Likely to be familiar with React (or Angular, Vue.js)", 10, EXPERIENCE, false),
                new ScoringCriteria("Understanding of databases and cloud platforms", 10, EXPERIENCE, false),
                new ScoringCriteria("Experience in a Software Engineering role ", 5, EXPERIENCE, false),
                new ScoringCriteria("Demonstrated leadership and/or coaching role", 5, EXPERIENCE, false)
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
