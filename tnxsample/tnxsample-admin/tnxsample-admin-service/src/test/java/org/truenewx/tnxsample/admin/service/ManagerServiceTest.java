package org.truenewx.tnxsample.admin.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.model.entity.unity.UnityUtil;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.test.service.annotation.TestBusinessException;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;
import org.truenewx.tnxsample.admin.service.model.ManagerCommand;
import org.truenewx.tnxsample.admin.service.test.ServiceTestSupport;

/**
 * ManagerServiceTest
 *
 * @author jianglei
 */
public class ManagerServiceTest extends ServiceTestSupport {
    @Autowired
    private ManagerService service;

    @Test
    @Caption("测试：校验管理员登录")
    public void validateLoginTest() throws BusinessException {
        String username = getData(Manager.class, 1).getUsername();
        String password = EncryptUtil.encryptByMd5("123456");
        this.service.validateLogin(username, password);
    }

    @Test
    @Caption("测试：校验管理员登录-用户名错误")
    @TestBusinessException(ManagerExceptionCodes.USERNAME_OR_PASSWORD_ERROR)
    public void validateLoginTestErrorUsername() throws BusinessException {
        String username = getData(Manager.class, 1).getUsername() + "2";
        String password = EncryptUtil.encryptByMd5("123456");
        this.service.validateLogin(username, password);
        Assert.fail();
    }

    @Test
    @Caption("测试：校验管理员登录-密码错误")
    @TestBusinessException(ManagerExceptionCodes.USERNAME_OR_PASSWORD_ERROR)
    public void validateLoginTestErrorPassword() throws BusinessException {
        String username = getData(Manager.class, 1).getUsername();
        String password = EncryptUtil.encryptByMd5("12345678");
        this.service.validateLogin(username, password);
        Assert.fail();
    }

    @Test
    @Caption("测试：校验管理员登录-禁用")
    @TestBusinessException(ManagerExceptionCodes.DISABLED_MANAGER)
    public void validateLoginTestDisabled() throws BusinessException {
        String username = getData(Manager.class, 2).getUsername();
        String password = EncryptUtil.encryptByMd5("123456");
        this.service.validateLogin(username, password);
        Assert.fail();
    }

    @Test
    @Caption("测试：分页查询一般管理员")
    public void queryGeneralTest() {
        String keyword = "1";
        QueryResult<Manager> qr = this.service.queryGeneral(keyword, 10, 1);
        Assert.assertEquals(3, qr.getPaged().getTotal().intValue());
        List<Manager> records = qr.getRecords();
        Assert.assertEquals(3, records.size());
        records.forEach(manager -> {
            Assert.assertTrue(manager.getFullName().contains(keyword) || manager.getFullName().contains(keyword));
        });
    }

    @Test
    @Caption("测试：修改管理员密码")
    public void updatePasswordTest() {
        Integer managerId = getData(Manager.class, 1).getId();
        String oldPassword = EncryptUtil.encryptByMd5("123456");
        String newPassword = EncryptUtil.encryptByMd5("654321");
        Manager manager = this.service.updatePassword(managerId, oldPassword, newPassword);
        Assert.assertEquals(managerId, manager.getId());
        this.service.validateLogin(manager.getUsername(), newPassword);
    }

    @Test
    @Caption("测试：修改管理员密码-密码错误")
    @TestBusinessException(ManagerExceptionCodes.OLD_PASSWORD_ERROR)
    public void updatePasswordTestErrorPassword() {
        Integer managerId = getData(Manager.class, 1).getId();
        String oldPassword = EncryptUtil.encryptByMd5("12345678");
        String newPassword = EncryptUtil.encryptByMd5("654321");
        this.service.updatePassword(managerId, oldPassword, newPassword);
        Assert.fail();
    }

    @Test
    @Caption("测试：重置管理员密码")
    public void resetPasswordTest() {
        Integer managerId = getData(Manager.class, 1).getId();
        String newPassword = EncryptUtil.encryptByMd5("654321");
        Manager manager = this.service.resetPassword(managerId, newPassword);
        Assert.assertEquals(managerId, manager.getId());
        this.service.validateLogin(manager.getUsername(), newPassword);
    }

    @Test
    @Caption("测试：新增管理员")
    public void addTest() {
        int roleId0 = getData(Role.class, 0).getId();
        int roleId1 = getData(Role.class, 1).getId();
        int[] roleIds = { roleId0, roleId1 };

        ManagerCommand command = new ManagerCommand();
        command.setUsername("new-manager");
        command.setFullName("New Manger");
        command.setPassword(EncryptUtil.encryptByMd5("123456"));
        command.setRoleIds(roleIds);
        Manager manager = this.service.add(command);
        manager = this.service.find(manager.getId());
        Assert.assertEquals(command.getUsername(), manager.getUsername());
        Assert.assertEquals(command.getFullName(), manager.getFullName());
        Collection<Role> roles = manager.getRoles();
        Assert.assertEquals(roleIds.length, roles.size());
        roles.forEach(role -> {
            Assert.assertTrue(ArrayUtils.contains(roleIds, role.getId()));
        });
    }

    @Test
    @Caption("测试：新增管理员-重复的用户名")
    @TestBusinessException(ManagerExceptionCodes.REPEAT_USERNAME)
    public void addTestRepeatUser() {
        String username = getFirstData(Manager.class).getUsername();
        ManagerCommand command = new ManagerCommand();
        command.setUsername(username);
        this.service.add(command);
        Assert.fail();
    }

    @Test
    @Caption("测试：修改管理员")
    public void updateTest() {
        int managerId = getData(Manager.class, 1).getId();
        int roleId0 = getData(Role.class, 0).getId();
        int roleId1 = getData(Role.class, 1).getId();
        int[] roleIds = { roleId0, roleId1 };

        ManagerCommand command = new ManagerCommand();
        command.setFullName("zhangsan");
        command.setRoleIds(roleIds);
        this.service.update(managerId, command);
        Manager manager = this.service.find(managerId);
        Assert.assertEquals(command.getFullName(), manager.getFullName());
        Collection<Role> roles = manager.getRoles();
        Assert.assertEquals(roleIds.length, roles.size());
        roles.forEach(role -> {
            Assert.assertTrue(ArrayUtils.contains(roleIds, role.getId()));
        });
    }

    @Test
    @Caption("测试：禁用/启用管理员")
    public void updateDisabledTest() {
        Manager manager1 = getData(Manager.class, 1);
        boolean disabled1 = manager1.isDisabled();

        this.service.updateDisabled(manager1.getId(), !disabled1);
        Manager manager = this.service.find(manager1.getId());
        Assert.assertEquals(!disabled1, manager.isDisabled());
    }

    @Test
    @Caption("测试：禁用/启用顶级管理员")
    public void reverseDisabledTopTest() {
        Manager manager0 = getData(Manager.class, 0);

        this.service.updateDisabled(manager0.getId(), manager0.isDisabled());
        Manager manager = this.service.find(manager0.getId());
        Assert.assertEquals(manager0.isDisabled(), manager.isDisabled());
    }

    @Test
    @Caption("测试：根据角色计数管理员")
    public void countOfRoleTest() {
        int roleId = getData(Role.class, 0).getId();

        long count = this.service.countOfRole(roleId);
        Assert.assertEquals(1, count);
    }

    @Test
    @Caption("测试：分页查询不属于指定角色的管理员")
    public void queryGeneralOutOfRoleTest() {
        int roleId = getData(Role.class, 0).getId();

        QueryResult<Manager> qr = this.service.queryGeneralOutOfRole(roleId, 10, 1);
        Assert.assertEquals(2, qr.getPaged().getTotal().intValue());
        List<Manager> records = qr.getRecords();
        Assert.assertEquals(2, records.size());
        Assert.assertTrue(!UnityUtil.containsId(records.get(0).getRoles(), roleId));
    }

}
