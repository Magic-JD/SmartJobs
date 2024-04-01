package org.smartjobs.com.service.role;

import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    public Role getRole() {
        List<ScoringCriteria> scoringCriteria = List.of(
                new ScoringCriteria("Degree in Computer Science or related field", 10, true),
                new ScoringCriteria("Advanced degree (MSc, Ph.D.) in computer science or related field", 5, true),
                new ScoringCriteria("Relevant certifications (e.g., AWS Certified Developer, Microsoft Certified: Azure Developer Associate)", 5, true),
                new ScoringCriteria("Proficiency in Java and Python", 10, false),
                new ScoringCriteria("Likely to be familiar with React (or Angular, Vue.js)", 10, false),
                new ScoringCriteria("Understanding of databases and cloud platforms", 10, false),
                new ScoringCriteria("Experience in a Software Engineering role ", 5, false),
                new ScoringCriteria("Demonstrated leadership and/or coaching role", 5, false)
        );
        return new Role("Senior Backend Engineer", scoringCriteria);
    }
}
