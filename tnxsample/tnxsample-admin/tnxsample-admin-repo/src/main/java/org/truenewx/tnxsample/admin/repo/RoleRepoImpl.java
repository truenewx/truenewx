package org.truenewx.tnxsample.admin.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.repo.jpa.support.JpaUnityRepoxSupport;
import org.truenewx.tnxsample.admin.model.entity.Role;

@Repository
public class RoleRepoImpl extends JpaUnityRepoxSupport<Role, Integer> implements RoleRepox {

    @Override
    public List<Role> findByNameOrderByOrdinal(String name) {
        String oql = "from Role";
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(name)) {
            oql += " where name like :name";
            params.put("name", Strings.PERCENT + name + Strings.PERCENT);
        }
        oql += " order by ordinal";
        return getAccessTemplate().list(oql, params);
    }

}
