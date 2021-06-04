package org.truenewx.tnxsample.admin.service;

import java.util.List;

import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.unity.CommandUnityBusinessValidator;
import org.truenewx.tnxjee.service.unity.CommandUnityService;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;

/**
 * 角色服务
 *
 * @author jianglei
 */
public interface RoleService
        extends CommandUnityService<Role, Integer>, CommandUnityBusinessValidator<Role, Integer> {

    List<Role> findAll();

    List<Role> findByName(String name);

    Role move(int id, boolean down);

    QueryResult<Manager> queryManagers(int id, int pageSize, int pageNo);

}
