package org.truenewx.tnxsample.admin.repo;

import org.truenewx.tnxjee.repo.mongo.repository.MongoUnityRepository;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;

/**
 * 操作日志Repo
 *
 * @author jianglei
 */
public interface ActionLogRepo extends MongoUnityRepository<ActionLog, String>, ActionLogRepox {

}
