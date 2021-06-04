package org.truenewx.tnxsample.admin.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.UnityUtil;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.impl.unity.AbstractUnityService;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;
import org.truenewx.tnxsample.admin.repo.ManagerRepo;
import org.truenewx.tnxsample.admin.repo.ManagerRoleRelationRepo;
import org.truenewx.tnxsample.admin.repo.RoleRepo;
import org.truenewx.tnxsample.admin.service.model.RoleCommand;

/**
 * 角色服务
 *
 * @author jianglei
 */
@Service
public class RoleServiceImpl extends AbstractUnityService<Role, Integer> implements RoleService {

    @Autowired
    private RoleRepo repo;
    @Autowired
    private ManagerRepo managerRepo;
    @Autowired
    private ManagerRoleRelationRepo managerRoleRelationRepo;

    @Override
    public List<Role> findAll() {
        return this.repo.findAll();
    }

    @Override
    public List<Role> findByName(String name) {
        return this.repo.findByNameOrderByOrdinal(name);
    }

    @Override
    @WriteTransactional
    public Role move(int id, boolean down) {
        Role role = find(id);
        if (role != null) {
            long ordinal = role.getOrdinal();
            Role other;
            if (down) { // 下移
                other = this.repo.findFirstByOrdinalGreaterThanOrderByOrdinal(ordinal);
            } else { // 上移
                other = this.repo.findFirstByOrdinalLessThanOrderByOrdinalDesc(ordinal);
            }
            // 互换序号
            if (other != null) {
                role.setOrdinal(other.getOrdinal());
                this.repo.save(role);
                other.setOrdinal(ordinal);
                this.repo.save(other);
            }
        }
        return role;
    }

    @Override
    public void validateBusiness(Integer id, CommandModel<Role> commandModel) {
        if (commandModel instanceof RoleCommand) {
            RoleCommand command = (RoleCommand) commandModel;
            String name = command.getName();
            if (StringUtils.isNotBlank(name)) {
                if ((id == null && this.repo.countByName(name) > 0) || (id != null
                        && this.repo.countByNameAndIdNot(name, id) > 0)) {
                    throw new BusinessException(ManagerExceptionCodes.ROLE_REPEAT_NAME, name);
                }
            }
        }
    }

    @Override
    protected Role beforeSave(Integer id, CommandModel<Role> commandModel) {
        if (commandModel instanceof RoleCommand) {
            RoleCommand command = (RoleCommand) commandModel;
            validateBusiness(id, command);

            Role role;
            if (id == null) {
                role = new Role();
                role.setOrdinal(System.currentTimeMillis());
            } else {
                role = load(id);
            }
            role.setName(command.getName());
            role.setRemark(command.getRemark());
            Set<String> permissions = new HashSet<>();
            CollectionUtil.addAll(permissions, command.getPermissions());
            role.setPermissions(permissions);
            Collection<Manager> managers = role.getManagers();
            int[] newManagerIds = command.getManagerIds();
            // 原管理员在新管理员中没有的，说明被移除了
            managers.removeIf(manager -> {
                boolean removing = !ArrayUtils.contains(newManagerIds, manager.getId());
                if (removing) {
                    manager.getRoles().remove(role);
                }
                return removing;
            });
            // 此时管理员清单中现存的均为已包含在新管理员中的，需要添加新加的管理员
            if (newManagerIds != null) {
                for (int managerId : newManagerIds) {
                    if (!UnityUtil.containsId(managers, managerId)) {
                        this.managerRepo.findById(managerId).ifPresent(manager -> {
                            managers.add(manager);
                            manager.getRoles().add(role);
                        });
                    }
                }
            }
            this.repo.save(role);
            return role;
        }
        return null;
    }

    @Override
    public Role delete(Integer id) {
        Role role = find(id);
        if (role != null) {
            // 移除包含的管理员关系
            Collection<Manager> managers = role.getManagers();
            managers.forEach(manager -> {
                manager.getRoles().remove(role);
            });
            this.repo.delete(role);
        }
        return role;
    }

    @Override
    public QueryResult<Manager> queryManagers(int id, int pageSize, int pageNo) {
        return this.managerRoleRelationRepo
                .queryManagersByRoleIdOrderByFullName(id, pageSize, pageNo);
    }

}
