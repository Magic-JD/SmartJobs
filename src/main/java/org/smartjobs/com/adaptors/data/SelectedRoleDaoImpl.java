package org.smartjobs.com.adaptors.data;

import org.smartjobs.com.adaptors.data.repository.SelectedRoleRepository;
import org.smartjobs.com.adaptors.data.repository.data.SelectedRole;
import org.smartjobs.com.core.dal.SelectedRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SelectedRoleDaoImpl implements SelectedRoleDao {

    private final SelectedRoleRepository repository;

    @Autowired
    public SelectedRoleDaoImpl(SelectedRoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void setSelectedRole(String username, Long roleId) {
        repository.saveAndFlush(repository.findByUsername(username)
                .map(sr -> {
                    sr.setRoleId(roleId);
                    return sr;
                })
                .orElse(SelectedRole.builder().username(username).roleId(roleId).build()));
    }

    @Override
    public Optional<Long> getCurrentlySelectedRole(String username) {
        return repository.findByUsername(username).map(SelectedRole::getRoleId);
    }

    @Override
    public void deleteCurrentlySelectedRole(String username) {
        repository.deleteByUsername(username);
    }
}
