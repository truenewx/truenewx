package org.truenewx.tnxsample.admin.model.entity;

import java.util.Objects;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.entity.relation.AbstractRelation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;

/**
 * 管理员-角色的关系
 *
 * @author jianglei
 */
@Caption("管理员-角色的关系")
public class ManagerRoleRelation extends AbstractRelation<Integer, Integer> {

    public static class Key implements RelationKey<Integer, Integer> {

        private static final long serialVersionUID = -7839201529321883683L;

        private int managerId;
        private int roleId;

        public int getManagerId() {
            return this.managerId;
        }

        public void setManagerId(int managerId) {
            this.managerId = managerId;
        }

        public int getRoleId() {
            return this.roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        @Override
        public Integer getLeft() {
            return this.managerId;
        }

        @Override
        public Integer getRight() {
            return this.roleId;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Key key = (Key) other;
            return this.managerId == key.managerId && this.roleId == key.roleId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.managerId, this.roleId);
        }
    }

    private Key id;
    private Manager manager;
    private Role role;

    @SuppressWarnings("unchecked")
    @Override
    public Key getId() {
        return this.id;
    }

    protected void setId(Key id) {
        this.id = id;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
