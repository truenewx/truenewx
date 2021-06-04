package org.truenewx.tnxsample.admin.repo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.jpa.support.JpaRelationRepoxSupport;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.ManagerRoleRelation;
import org.truenewx.tnxsample.admin.model.entity.Role;

/**
 * 管理员-角色关系Repo扩展实现
 */
@Repository
public class ManagerRoleRelationRepoImpl extends JpaRelationRepoxSupport<ManagerRoleRelation, Integer, Integer>
        implements ManagerRoleRelationRepox {

    @Override
    protected Binate<String, String> getIdProperty() {
        return new Binary<>("id.managerId", "id.roleId");
    }

    @Override
    public List<Role> getRolesByManagerId(int managerId) {
        String oql = "select r.role from ManagerRoleRelation r where r.id.managerId=:managerId";
        return getAccessTemplate().list(oql, "managerId", managerId);
    }

    @Override
    public QueryResult<Manager> queryManagersByRoleIdOrderByFullName(int roleId, int pageSize, int pageNo) {
        String oql = "from ManagerRoleRelation r where r.id.roleId=:roleId";
        Map<String, Object> params = Map.of("roleId", roleId);
        FieldOrder order = new FieldOrder("r.manager.fullName", false);
        QueryResult<ManagerRoleRelation> qr = query(oql, params, pageSize, pageNo, order);
        return qr.map(ManagerRoleRelation::getManager);
    }

}
