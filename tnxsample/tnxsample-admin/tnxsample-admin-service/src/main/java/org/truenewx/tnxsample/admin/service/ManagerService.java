package org.truenewx.tnxsample.admin.service;

import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.unity.CommandUnityBusinessValidator;
import org.truenewx.tnxjee.service.unity.CommandUnityService;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.service.model.ManagerSelfCommand;

/**
 * 管理员服务
 *
 * @author jianglei
 */
public interface ManagerService extends CommandUnityService<Manager, Integer>,
        CommandUnityBusinessValidator<Manager, Integer> {

    Manager findWithRoles(Integer id);

    Manager loadByUsername(String username);

    Manager validateLogin(String username, String password);

    QueryResult<Manager> queryGeneral(String keyword, int pageSize, int pageNo);

    Manager updateSelf(int id, ManagerSelfCommand command);

    Manager updatePassword(int id, String oldPassword, String newPassword);

    Manager resetPassword(int id, String newMd5Password);

    Manager updateDisabled(int id, boolean disabled);

    long countOfRole(int roleId);

    QueryResult<Manager> queryGeneralOutOfRole(int exceptedRoleId, int pageSize, int pageNo);

}
