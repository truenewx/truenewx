package org.truenewx.tnxsample.admin.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.truenewx.tnxsample.admin.model.entity.ManagerRoleRelation;

/**
 * 管理员-角色关系Repo
 */
public interface ManagerRoleRelationRepo
        extends JpaRepository<ManagerRoleRelation, ManagerRoleRelation.Key>, ManagerRoleRelationRepox {

}
