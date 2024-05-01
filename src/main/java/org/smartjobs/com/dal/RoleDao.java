package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.RoleRepository;
import org.smartjobs.com.dal.repository.data.Role;
import org.smartjobs.com.exception.categories.ApplicationExceptions.IncorrectIdForRoleRetrievalException;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RoleDao {

    private final RoleRepository repository;

    @Autowired
    public RoleDao(RoleRepository repository) {
        this.repository = repository;
    }

    public List<RoleDisplay> getUserRoles(String username) {
        return repository.findByUserId(username).stream()
                .map(role -> new RoleDisplay(role.getId(), role.getPosition()))
                .toList();
    }

    public void saveRole(String userId, String name) {
        repository.saveAndFlush(Role.builder().userId(userId).position(name).build());
    }

    public org.smartjobs.com.service.role.data.Role getRoleById(long id) {
        return repository.findById(id)
                .map(role -> new org.smartjobs.com.service.role.data.Role(role.getPosition(), Collections.emptyList()))
                .orElseThrow(() -> new IncorrectIdForRoleRetrievalException(id));
    }

    public void delete(long roleId) {
        repository.deleteById(roleId);
    }
}
