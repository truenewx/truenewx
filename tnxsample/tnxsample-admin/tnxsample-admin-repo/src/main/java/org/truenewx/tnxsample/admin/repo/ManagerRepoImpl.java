package org.truenewx.tnxsample.admin.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.jpa.support.JpaUnityRepoxSupport;
import org.truenewx.tnxsample.admin.model.entity.Manager;

/**
 * 管理员Repo实现
 *
 * @author jianglei
 */
@Repository
public class ManagerRepoImpl extends JpaUnityRepoxSupport<Manager, Integer> implements ManagerRepox {

    @Override
    public long countByRoleId(int roleId) {
        String ql = "select count(*) from ManagerRoleRelation r where r.id.roleId = :roleId";
        return getAccessTemplate().count(ql, "roleId", roleId);
    }

    @Override
    public List<Integer> getIdsByUsernameOrFullName(String keyword) {
        String ql = "select id from Manager where username like :keyword or fullName like :keyword";
        return getAccessTemplate().list(ql, "keyword", Strings.PERCENT + keyword + Strings.PERCENT);
    }

    @Override
    public QueryResult<Manager> queryByKeywordAndTop(String keyword, Boolean top, int pageSize,
            int pageNo) {
        StringBuffer ql = new StringBuffer("from Manager where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(keyword)) {
            ql.append(" and (jobNo like :keyword or username like :keyword")
                    .append(" or fullName like :keyword or indexName like :keyword)");
            params.put("keyword", Strings.PERCENT + keyword + Strings.PERCENT);
        }
        if (top != null) {
            ql.append(" and top=:top");
            params.put("top", top);
        }
        FieldOrder order = new FieldOrder("jobNo", false);
        return query(ql, params, pageSize, pageNo, order);
    }

    @Override
    public QueryResult<Manager> queryByRoleIdNotAndTop(int roleIdNot, Boolean top, int pageSize,
            int pageNo) {
        StringBuffer ql = new StringBuffer("from Manager m where m.id not in ")
                .append("(select r.manager.id from ManagerRoleRelation r where r.id.roleId=:roleIdNot)");
        Map<String, Object> params = new HashMap<>();
        params.put("roleIdNot", roleIdNot);
        if (top != null) {
            ql.append(" and top=:top");
            params.put("top", top);
        }
        FieldOrder order = new FieldOrder("m.username", false);
        return query(ql, params, pageSize, pageNo, order);
    }

    @Override
    public long countByJobNoAndIdNot(String jobNo, Integer idNot) {
        String ql = "select count(*) from Manager where jobNo=:jobNo";
        Map<String, Object> params = new HashMap<>();
        params.put("jobNo", jobNo);
        if (idNot != null) {
            ql += " and id<>:idNot";
            params.put("idNot", idNot);
        }
        return getAccessTemplate().count(ql, params);
    }

    @Override
    public long countByUsernameAndIdNot(String username, Integer idNot) {
        String ql = "select count(*) from Manager where username=:username";
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        if (idNot != null) {
            ql += " and id<>:idNot";
            params.put("idNot", idNot);
        }
        return getAccessTemplate().count(ql, params);
    }

}
