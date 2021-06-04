package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

/**
 * 通过命令模型传递数据的从属单体业务逻辑校验器<br/>
 * 字段格式校验由格式校验框架完成，本接口的实现仅负责通过读取持久化数据验证字段数据的业务逻辑合法性
 *
 * @author jianglei
 */
public interface CommandOwnedUnityBusinessValidator<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable> {

    /**
     * 验证指定id的指定单体数据的业务逻辑合法性
     *
     * @param owner 所属者
     * @param id    单体标识
     * @param model 命令模型数据
     */
    // 方法名中含有Business字样，是为了凸显验证业务逻辑而不是格式，同时也减少与其它方法重名的可能性
    void validateBusiness(O owner, K id, CommandModel<T> commandModel);

}
