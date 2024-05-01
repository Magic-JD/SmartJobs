package org.smartjobs.com.service.role;

import org.smartjobs.com.dal.RoleDao;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RoleService {

    private final RoleDao roleDao;


    @Autowired
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public Role getRole(long id) {
        return roleDao.getRoleById(id);
    }

    public List<RoleDisplay> getUserRoles(String username) {
        return roleDao.getUserRoles(username);
    }

    public Role createRole(String name, String userId) {
        var role = new Role(name, Collections.emptyList());
        roleDao.saveRole(userId, name);
        return role;
    }

    public void deleteRole(long roleId) {
        roleDao.delete(roleId);
    }
}
