package org.truenewx.tnxsample.fss.service;

import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjeex.fss.service.FssServiceTemplateImpl;

/**
 * 文件存储服务实现
 *
 * @author jianglei
 */
@Service
public class FssServiceImpl extends FssServiceTemplateImpl<IntegerUserIdentity>
        implements FssService {

}
