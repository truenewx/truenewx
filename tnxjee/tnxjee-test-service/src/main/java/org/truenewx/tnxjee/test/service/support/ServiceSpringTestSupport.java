package org.truenewx.tnxjee.test.service.support;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.test.service.data.DataProviderFactory;
import org.truenewx.tnxjee.test.support.SpringTestSupport;

/**
 * 服务层的Spring上下文环境测试支持
 *
 * @author jianglei
 */
@Transactional(rollbackFor = Throwable.class)
@Rollback
public abstract class ServiceSpringTestSupport extends SpringTestSupport {

    @Autowired
    private DataProviderFactory dataProviderFactory;

    protected <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        return this.dataProviderFactory.getDataList(entityClass);
    }

    protected <T extends Entity> T getData(Class<T> entityClass, int index) {
        List<T> list = getDataList(entityClass);
        return list == null ? null : list.get(index);
    }

    protected <T extends Entity> T getFirstData(Class<T> entityClass) {
        return getData(entityClass, 0);
    }

    @BeforeEach
    public void before() {
        this.dataProviderFactory.init(getEntityClasses());
    }

    @AfterEach
    public void after() {
        this.dataProviderFactory.clear(getEntityClasses());
    }

    protected Class<?>[] getEntityClasses() {
        return null;
    }

    /**
     * 断言业务异常
     *
     * @param executable 执行方法
     * @param errorCode  业务异常错误码
     */
    protected final void assertBusinessException(Executable executable, String errorCode) {
        BusinessException be = Assertions.assertThrows(BusinessException.class, executable);
        Assertions.assertEquals(errorCode, be.getCode());
    }

}
