package org.truenewx.tnxjeex.captcha.core;

import java.util.Map;
import java.util.concurrent.*;

import org.springframework.beans.factory.DisposableBean;
import org.truenewx.tnxjee.core.util.MathUtil;

import com.anji.captcha.service.CaptchaCacheService;

/**
 * 基于RAM的校验码缓存服务
 */
public class RamCaptchaCacheService implements CaptchaCacheService, DisposableBean {

    public static final String TYPE = "ram";

    private ScheduledExecutorService scheduledExecutor;
    private Map<String, Object> cache = new ConcurrentHashMap<>();

    public RamCaptchaCacheService() {
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(1, r ->
                new Thread(r, "thread-captcha-cache-clean"), new ThreadPoolExecutor.CallerRunsPolicy());
        this.scheduledExecutor.scheduleAtFixedRate(this::refresh, 10, 3600, TimeUnit.SECONDS);
    }

    private static String getExpiredTimeKey(String key) {
        return key + "_ExpiredTime";
    }

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        this.cache.put(key, value);
        if (expiresInSeconds > 0) {
            this.cache.put(getExpiredTimeKey(key), System.currentTimeMillis() + expiresInSeconds * 1000); //缓存失效时间
        }
    }

    @Override
    public boolean exists(String key) {
        Long expiredTimeMillis = (Long) this.cache.get(getExpiredTimeKey(key));
        if (expiredTimeMillis == null || expiredTimeMillis == 0L) {
            return false;
        }
        if (expiredTimeMillis < System.currentTimeMillis()) {
            delete(key);
            return false;
        }
        return true;
    }

    @Override
    public void delete(String key) {
        this.cache.remove(key);
        this.cache.remove(getExpiredTimeKey(key));
    }

    @Override
    public String get(String key) {
        if (exists(key)) {
            return (String) this.cache.get(key);
        }
        return null;
    }

    @Override
    public String type() {
        return TYPE;
    }

    public void refresh() {
        for (String key : this.cache.keySet()) {
            exists(key);
        }
    }

    @Override
    public Long increment(String key, long val) {
        Long result = MathUtil.parseLong(get(key)) + val;
        set(key, String.valueOf(result), 0);
        return result;
    }

    @Override
    public void destroy() {
        this.cache.clear();
        this.scheduledExecutor.shutdown();
    }

}
