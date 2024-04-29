package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.RoleRepository;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleDao {

    private final RoleRepository repository;

    @Autowired
    public RoleDao(RoleRepository repository) {
        this.repository = repository;
    }

    public List<RoleDisplay> getUserRoles(String username) {
        return repository.findByUsername(username).stream()
                .map(role -> new RoleDisplay(role.getId(), role.getName()))
                .toList();
    }
}
