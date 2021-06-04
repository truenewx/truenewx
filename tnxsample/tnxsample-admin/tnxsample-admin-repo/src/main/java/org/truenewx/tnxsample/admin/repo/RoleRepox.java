package org.truenewx.tnxsample.admin.repo;

import java.util.List;
import org.truenewx.tnxsample.admin.model.entity.Role;

public interface RoleRepox {

    List<Role> findByNameOrderByOrdinal(String name);

}
