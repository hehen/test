package com.zhouyou.http;

import com.zhouyou.http.cache.converter.IDiskConverter;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.utils.Utils;

import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author wenlu
 * @desc
 * @date 2018/8/14 15:34
 */
public class HttpCacheConfig {

    /**
     * 缓存过期时间，默认永久缓存
     */
    public static final int DEFAULT_CACHE_NEVER_EXPIRE = -1;
    /**
     * 缓存时间
     */
    private long mCacheTime = -1;
    /**
     * 缓存的转换器
     */
    private IDiskConverter mConverter = new SerializableDiskConverter();
    /**
     * 缓存目录
     */
    private File mCacheDirectory;
    /**
     * 缓存大小
     */
    private long mCacheMaxSize;
    /**
     * 缓存版本
     */
    private int mCacheVersion;
    /**
     * 缓存类型
     */
    private CacheMode mCacheMode = CacheMode.NO_CACHE;



    /**
     * 全局的缓存模式
     */
    public HttpCacheConfig setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /**
     * 获取全局的缓存模式
     */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /**
     * 全局的缓存过期时间
     */
    public HttpCacheConfig setCacheTime(long cacheTime) {
        if (cacheTime <= -1) {
            cacheTime = DEFAULT_CACHE_NEVER_EXPIRE;
        }
        mCacheTime = cacheTime;
        return this;
    }

    /**
     * 获取全局的缓存过期时间
     */
    public long getCacheTime() {
        return mCacheTime;
    }


    public HttpCacheConfig setCacheDiskConverter(IDiskConverter converter) {
        Utils.checkNotNull(converter, "converter == null");
        mConverter = converter;
        return this;
    }

    /**
     * 获取缓存的转换器
     */
    public IDiskConverter getConverter() {
        return mConverter;
    }

    /**
     * 全局的缓存大小,默认50M
     */
    public HttpCacheConfig setCacheMaxSize(long maxSize) {
        mCacheMaxSize = maxSize;
        return this;
    }

    /**
     * 获取全局的缓存大小
     */
    public long getCacheMaxSize() {
        return mCacheMaxSize;
    }

    /**
     * 全局设置缓存的路径，默认是应用包下面的缓存
     */
    public HttpCacheConfig setCacheDirectory(File directory) {
        mCacheDirectory = Utils.checkNotNull(directory, "directory == null");
        return this;
    }

    /**
     * 获取缓存的路劲
     */
    public File getCacheDirectory() {
        return mCacheDirectory;
    }

    /**
     * 全局设置缓存的版本，默认为1，缓存的版本号
     */
    public HttpCacheConfig setCacheVersion(int cacheVersion) {
        if (cacheVersion < 0) {
            throw new IllegalArgumentException("cacheVersion must > 0");
        }
        mCacheVersion = cacheVersion;
        return this;
    }

    /**
     * 获取缓存的版本
     */
    public int getCacheVersion() {
        return mCacheVersion;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
