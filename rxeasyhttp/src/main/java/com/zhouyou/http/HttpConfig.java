package com.zhouyou.http;

import com.zhouyou.http.https.HttpsUtils;
import com.zhouyou.http.utils.Utils;

import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;

/**
 * @author wenlu
 * @desc
 * @date 2018/8/14 15:34
 */
public class HttpConfig {
    /**
     * 默认的超时时间
     */
    public static final int DEFAULT_MILLISECONDS = 60000;
    /**
     * 默认重试次数
     */
    private static final int DEFAULT_RETRY_COUNT = 3;
    /**
     * 默认重试叠加时间
     */
    private static final int DEFAULT_RETRY_INCREASEDELAY = 0;
    /**
     * 默认重试延时
     */
    private static final int DEFAULT_RETRY_DELAY = 500;

    /**
     * 全局BaseUrl
     */
    private String mBaseUrl;
    /**
     * 重试次数默认3次
     */
    private int mRetryCount = DEFAULT_RETRY_COUNT;
    /**
     * 读取超时时间
     */
    private long mReadTimeOut = DEFAULT_MILLISECONDS;
    /**
     * 写入超时时间
     */
    private long mWriteTimeout = DEFAULT_MILLISECONDS;
    /**
     * 连接超时时间
     */
    private long mConnectTimeout = DEFAULT_MILLISECONDS;
    /**
     * 延迟xxms重试
     */
    private int mRetryDelay = DEFAULT_RETRY_DELAY;
    /**
     * 叠加延迟
     */
    private int mRetryIncreaseDelay = DEFAULT_RETRY_INCREASEDELAY;

    /**
     * 证书
     */
    private HttpsUtils.SSLParams mSslParams;
    /**
     * 全局访问规则
     */
    private HostnameVerifier mHostnameVerifier = new DefaultHostnameVerifier();


    /**
     * 全局设置baseurl
     */
    public HttpConfig setBaseUrl(String baseUrl) {
        mBaseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
        return this;
    }

    /**
     * 获取全局baseurl
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * 全局读取超时时间
     */
    public HttpConfig setReadTimeOut(long readTimeOut) {
        mReadTimeOut = readTimeOut;
        return this;
    }

    /**
     * 全局读取超时时间
     */
    public long getReadTimeOut() {
        return mReadTimeOut;
    }

    /**
     * 全局写入超时时间
     */
    public HttpConfig setWriteTimeOut(long writeTimeout) {
        mWriteTimeout = writeTimeout;
        return this;
    }

    /**
     * 全局写入超时时间
     */
    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    /**
     * 全局连接超时时间
     */
    public HttpConfig setConnectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout;
        return this;
    }

    /**
     * 全局连接超时时间
     */
    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    /**
     * 超时重试次数
     */
    public HttpConfig setRetryCount(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount must > 0");
        }
        mRetryCount = retryCount;
        return this;
    }

    /**
     * 超时重试次数
     */
    public int getRetryCount() {
        return mRetryCount;
    }

    /**
     * 超时重试延迟时间
     */
    public HttpConfig setRetryDelay(int retryDelay) {
        if (retryDelay < 0) {
            throw new IllegalArgumentException("retryDelay must > 0");
        }
        mRetryDelay = retryDelay;
        return this;
    }

    /**
     * 超时重试延迟时间
     */
    public int getRetryDelay() {
        return mRetryDelay;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public HttpConfig setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0) {
            throw new IllegalArgumentException("retryIncreaseDelay must > 0");
        }
        mRetryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public int getRetryIncreaseDelay() {
        return mRetryIncreaseDelay;
    }

    /**
     * https的全局自签名证书
     */
    public HttpConfig setCertificates(InputStream... certificates) {
        mSslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        return this;
    }

    /**
     * https双向认证证书
     */
    public HttpConfig setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        mSslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        return this;
    }

    public HttpsUtils.SSLParams getmSslParams() {
        return mSslParams;
    }

    /**
     * https的全局访问规则
     */
    public HttpConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = Utils.checkNotNull(hostnameVerifier, "hostnameVerifier == null");
        return this;
    }

    /**
     * https的全局访问规则
     */
    public HostnameVerifier getmHostnameVerifier() {
        return mHostnameVerifier;
    }

}
