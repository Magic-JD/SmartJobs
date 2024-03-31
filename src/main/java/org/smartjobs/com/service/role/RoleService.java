package org.smartjobs.com.service.role;

import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    public Role getRole() {
        List<ScoringCriteria> scoringCriteria = List.of(
                new ScoringCriteria("Degree in Computer Science or related field", 10),
                new ScoringCriteria("Advanced degree (MSc, Ph.D.) in computer science or related field", 5),
                new ScoringCriteria("Relevant certifications (e.g., AWS Certified Developer, Microsoft Certified: Azure Developer Associate)", 5),
                new ScoringCriteria("Proficiency in Java and Python", 10),
                new ScoringCriteria("Likely to be familiar with React (or Angular, Vue.js)", 10),
                new ScoringCriteria("Understanding of databases and cloud platforms", 20),
                new ScoringCriteria("Relevant consumer tech industry experience 1 point per year", 10),
                new ScoringCriteria("Experience in a Software Engineering role ", 5),
                new ScoringCriteria("Demonstrated leadership and/or coaching role", 5),
                new ScoringCriteria("Notable projects or contributions", 20)
        );
        return new Role("Senior Backend Engineer", scoringCriteria);
    }
}
