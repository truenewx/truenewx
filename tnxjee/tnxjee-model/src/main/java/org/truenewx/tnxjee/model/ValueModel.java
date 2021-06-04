package org.truenewx.tnxjee.model;

/**
 * 值模型。没有唯一标识属性，没有单独映射的数据库表，数据存放在所属实体映射的数据库表中<br/>
 * 实现类需覆写{@link Object#hashCode()}和
 * {@link Object#equals(Object)} ，以便更好地区分不同实例
 *
 * @author jianglei
 */
public interface ValueModel extends DomainModel {
    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
