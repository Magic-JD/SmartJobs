package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.SelectedRoleRepository;
import org.smartjobs.com.dal.repository.data.SelectedRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SelectedRoleDao {

    private final SelectedRoleRepository repository;

    @Autowired
    public SelectedRoleDao(SelectedRoleRepository repository) {
        this.repository = repository;
    }

    public void setSelectedRole(String username, Long roleId) {
        repository.saveAndFlush(repository.findByUsername(username)
                .map(sr -> {
                    sr.setRoleId(roleId);
                    return sr;
                })
                .orElse(SelectedRole.builder().username(username).roleId(roleId).build()));
    }

    public Optional<Long> getCurrentlySelectedRole(String username) {
        return Optional.ofNullable(repository.findByUsername(username).map(SelectedRole::getRoleId).orElseThrow(RuntimeException::new));
    }

}
