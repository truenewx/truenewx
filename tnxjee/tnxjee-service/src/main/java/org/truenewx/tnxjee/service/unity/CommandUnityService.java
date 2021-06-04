package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 基于命令模型的单体服务
 *
 * @author jianglei
 */
public interface CommandUnityService<T extends Unity<K>, K extends Serializable> extends UnityService<T, K> {

    /**
     * 添加单体
     *
     * @param commandModel 存放添加数据的命令模型对象
     * @return 添加的单体
     */
    T add(CommandModel<T> commandModel);

    /**
     * 修改单体
     *
     * @param id           要修改单体的标识
     * @param commandModel 存放修改数据的命令模型对象
     * @return 修改后的单体
     */
    T update(K id, CommandModel<T> commandModel);

}
