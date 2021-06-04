package org.truenewx.tnxsample.admin.service;

import java.time.Instant;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.crypto.Md5xEncryptor;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.impl.unity.AbstractUnityService;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;
import org.truenewx.tnxsample.admin.repo.ManagerRepo;
import org.truenewx.tnxsample.admin.repo.RoleRepo;
import org.truenewx.tnxsample.admin.service.model.ManagerCommand;
import org.truenewx.tnxsample.admin.service.model.ManagerSelfCommand;

/**
 * 管理员服务实现
 *
 * @author jianglei
 */
@Service
public class ManagerServiceImpl extends AbstractUnityService<Manager, Integer>
        implements ManagerService {

    @Autowired
    private ManagerRepo repo;
    @Autowired
    private RoleRepo roleRepo;
    private Md5xEncryptor encryptor = new Md5xEncryptor(29);

    @Override
    public Manager findWithRoles(Integer id) {
        Manager manager = super.find(id);
        if (manager != null) {
            manager.getRoles().size();
        }
        return manager;
    }

    @Override
    public Manager loadByUsername(String username) {
        Manager manager = this.repo.findFirstByUsername(username);
        if (manager == null) { // 根据用户名找不到管理员
            throw new BusinessException(ManagerExceptionCodes.USERNAME_OR_PASSWORD_ERROR);
        }
        return manager;
    }

    @Override
    public Manager validateLogin(String username, String password) {
        Manager manager = loadByUsername(username);
        if (!isValidPassword(manager, password)) { // 密码错误
            throw new BusinessException(ManagerExceptionCodes.USERNAME_OR_PASSWORD_ERROR);
        }
        if (manager.isDisabled()) { // 管理员被禁用
            throw new BusinessException(ManagerExceptionCodes.DISABLED_MANAGER);
        }
        manager.getAuthorities();
        return manager;
    }

    private boolean isValidPassword(Manager manager, String password) {
        if (password.length() < 32) { // 长度小于32位的密码为原文
            return this.encryptor.validate(manager.getPassword(), password, manager.getId());
        } else { // 否则视为MD5密文
            return this.encryptor
                    .validateByMd5Source(manager.getPassword(), password, manager.getId());
        }
    }

    @Override
    public QueryResult<Manager> queryGeneral(String keyword, int pageSize, int pageNo) {
        return this.repo.queryByKeywordAndTop(keyword, false, pageSize, pageNo);
    }

    @Override
    public Manager updateSelf(int id, ManagerSelfCommand command) {
        Manager manager = find(id);
        if (manager != null) {
            manager.setHeadImageUrl(command.getHeadImageUrl());
            manager.setFullName(command.getFullName());
        }
        return manager;
    }

    @Override
    public Manager updatePassword(int id, String oldPassword, String newPassword) {
        Manager manager = find(id);
        if (manager != null) {
            if (!isValidPassword(manager, oldPassword)) { // 原密码错误
                throw new BusinessException(ManagerExceptionCodes.OLD_PASSWORD_ERROR)
                        .bind("oldPassword");
            }
            String password;
            if (newPassword.length() < 32) { // 长度小于32位的密码为原文
                password = this.encryptor.encrypt(newPassword, manager.getId());
            } else { // 否则视为MD5密文
                password = this.encryptor.encryptByMd5Source(newPassword, manager.getId());
            }
            manager.setPassword(password);
            this.repo.save(manager);
        }
        return manager;
    }

    @Override
    @WriteTransactional
    public Manager resetPassword(int id, String newMd5Password) {
        Manager manager = find(id);
        if (manager != null) {
            manager.setPassword(this.encryptor.encryptByMd5Source(newMd5Password, manager.getId()));
            this.repo.save(manager);
        }
        return manager;
    }

    @Override
    public void validateBusiness(Integer id, CommandModel<Manager> commandModel) {
        if (commandModel instanceof ManagerCommand) {
            ManagerCommand command = (ManagerCommand) commandModel;
            String jobNo = command.getJobNo();
            if (this.repo.countByJobNoAndIdNot(jobNo, id) > 0) {
                throw new BusinessException(ManagerExceptionCodes.REPEAT_JOB_NO, jobNo)
                        .bind("jobNo");
            }
            String username = command.getUsername();
            if (this.repo.countByUsernameAndIdNot(username, id) > 0) {
                throw new BusinessException(ManagerExceptionCodes.REPEAT_USERNAME, username)
                        .bind("username");
            }
        }
    }

    @Override
    public Manager add(CommandModel<Manager> commandModel) {
        validateBusiness(null, commandModel);
        if (commandModel instanceof ManagerCommand) {
            ManagerCommand command = (ManagerCommand) commandModel;
            Manager manager = new Manager();
            manager.setJobNo(command.getJobNo());
            manager.setUsername(command.getUsername());
            manager.setPassword(Strings.ASTERISK); // 密码暂时置为星号
            manager.setFullName(command.getFullName());
            manager.setIndexName(getIndexName(command.getFullName()));
            manager.setCreateTime(Instant.now());
            updateRoles(manager, command.getRoleIds());
            this.repo.save(manager);
            // 有了id之后再用id做密钥进行密码加密
            String encryptedPassword =
                    this.encryptor.encryptByMd5Source(command.getPassword(), manager.getId());
            manager.setPassword(encryptedPassword);
            this.repo.save(manager);
            return manager;
        }
        return null;
    }

    private String getIndexName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return fullName;
        }
        String indexName = StringUtil.toPinyin(fullName);
        indexName += Strings.COMMA + StringUtil.toPinyinAbbr(fullName);
        return indexName;
    }

    private void updateRoles(Manager manager, int[] roleIds) {
        Collection<Role> roles = manager.getRoles();
        roles.clear();
        if (roleIds != null) {
            for (int roleId : roleIds) {
                this.roleRepo.findById(roleId).ifPresent(role -> {
                    roles.add(role);
                    role.getManagers().add(manager);
                    this.roleRepo.save(role);
                });
            }
        }
    }

    @Override
    public Manager update(Integer id, CommandModel<Manager> commandModel) {
        validateBusiness(id, commandModel);
        if (commandModel instanceof ManagerCommand) {
            ManagerCommand command = (ManagerCommand) commandModel;
            Manager manager = load(id);
            manager.setJobNo(command.getJobNo());
            manager.setUsername(command.getUsername());
            manager.setFullName(command.getFullName());
            manager.setIndexName(getIndexName(command.getFullName()));
            updateRoles(manager, command.getRoleIds());
            this.repo.save(manager);
            return manager;
        }
        return null;
    }

    @Override
    @WriteTransactional
    public Manager updateDisabled(int id, boolean disabled) {
        Manager manager = find(id);
        if (manager != null && !manager.isTop()) { // 顶级管理员不能修改禁用项
            manager.setDisabled(disabled);
            this.repo.save(manager);
        }
        return manager;
    }

    @Override
    public long countOfRole(int roleId) {
        return this.repo.countByRoleId(roleId);
    }

    @Override
    public QueryResult<Manager> queryGeneralOutOfRole(int exceptedRoleId, int pageSize,
            int pageNo) {
        return this.repo.queryByRoleIdNotAndTop(exceptedRoleId, false, pageSize, pageNo);
    }

}
