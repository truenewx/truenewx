package org.truenewx.tnxjee.model;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 命令模型，用于在执行命令型（会改动实体数据的添加/修改/删除）操作时传递命令数据
 *
 * @author jianglei
 */
public interface CommandModel<T extends Entity> extends Model {

}
