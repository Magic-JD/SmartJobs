package org.smartjobs.com.core.dal;

import java.util.Optional;

public interface SelectedRoleDao {
    void setSelectedRole(String username, Long roleId);

    Optional<Long> getCurrentlySelectedRole(String username);

    void deleteCurrentlySelectedRole(String username);
}
