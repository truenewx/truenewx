package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

/**
 * 基于命令模型的从属单体服务
 *
 * @author jianglei
 */
public interface CommandOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends OwnedUnityService<T, K, O> {

    /**
     * 添加从属单体
     *
     * @param owner        所属者
     * @param commandModel 存放添加数据的命令模型对象
     * @return 添加的单体
     */
    T add(O owner, CommandModel<T> commandModel);

    /**
     * 修改从属单体<br>
     * 注意：不应修改单体的所属者
     *
     * @param owner        所属者
     * @param id           要修改单体的标识
     * @param commandModel 存放修改数据的命令模型对象
     * @return 修改后的单体
     */
    T update(O owner, K id, CommandModel<T> commandModel);

}
