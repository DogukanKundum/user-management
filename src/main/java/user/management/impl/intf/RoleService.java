package user.management.impl.intf;

import user.management.model.Role;

public interface RoleService {
    Role findByName(String name);
}
