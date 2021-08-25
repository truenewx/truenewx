package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 通过简单单体传递数据的单体业务逻辑校验器<br>
 * 字段格式校验由格式校验框架完成，本接口的实现仅负责通过读取持久化数据验证字段数据的业务逻辑合法性
 *
 * @author jianglei
 */
public interface SimpleUnityBusinessValidator<T extends Unity<K>, K extends Serializable> {

    /**
     * 验证指定id的指定单体数据的业务逻辑合法性
     *
     * @param id    单体标识
     * @param unity 单体数据，不是从数据库读取的单体，不包含id
     */
    // 方法名中含有Business字样，是为了凸显验证业务逻辑而不是格式，同时也减少与其它方法重名的可能性
    void validateBusiness(K id, T unity);

}
