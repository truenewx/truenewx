package org.truenewx.tnxsample.admin.repo;

import java.util.List;

import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;

/**
 * 管理员-角色关系Repo扩展
 */
public interface ManagerRoleRelationRepox {

    List<Role> getRolesByManagerId(int managerId);

    QueryResult<Manager> queryManagersByRoleIdOrderByFullName(int roleId, int pageSize, int pageNo);

}
