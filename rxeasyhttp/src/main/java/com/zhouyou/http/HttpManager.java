/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhouyou.http;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.zhouyou.http.cache.RxCache;
import com.zhouyou.http.cookie.CookieManger;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.request.CustomRequest;
import com.zhouyou.http.request.DeleteRequest;
import com.zhouyou.http.request.DownloadRequest;
import com.zhouyou.http.request.GetRequest;
import com.zhouyou.http.request.PostRequest;
import com.zhouyou.http.request.PutRequest;
import com.zhouyou.http.utils.HttpLog;
import com.zhouyou.http.utils.RxUtil;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Cache;

/**
 * <p>描述：网络请求入口类</p>
 * 主要功能：</br>
 * 1.全局设置超时时间
 * 2.支持请求错误重试相关参数，包括重试次数、重试延时时间</br>
 * 3.支持缓存支持6种缓存模式、时间、大小、缓存目录</br>
 * 4.支持支持GET、post、delete、put请求</br>
 * 5.支持支持自定义请求</br>
 * 6.支持文件上传、下载</br>
 * 7.支持全局公共请求头</br>
 * 8.支持全局公共参数</br>
 * 9.支持okhttp相关参数，包括拦截器</br>
 * 10.支持Retrofit相关参数</br>
 * 11.支持Cookie管理</br>
 *
 * @author wenlu
 */
public final class HttpManager {
    private static Application sContext;
    /**
     * 参数配置
     */
    private HttpConfig mHttpConfig;
    /**
     * 缓存参数配置
     */
    private HttpCacheConfig mHttpCacheConfig;
    /**
     * 网络客户端
     */
    private HttpClient mHttpClient;
    private Cache mCache = null;                                      //Okhttp缓存对象

    private HttpHeaders mCommonHeaders;                               //全局公共请求头
    private HttpParams mCommonParams;                                 //全局公共请求参数

    private RxCache.Builder rxCacheBuilder;                           //RxCache请求的Builder
    private volatile static HttpManager singleton = null;

    /**
     * Cookie管理
     */
    private CookieManger mCookieJar;


    private HttpManager() {
        if (mHttpConfig == null) {
            mHttpConfig = new HttpConfig();
        }
        if (mHttpCacheConfig == null) {
            mHttpCacheConfig = new HttpCacheConfig();
        }
        mHttpClient = new HttpClient(mHttpConfig);
        buildRxCache();
    }

    private void buildRxCache() {
        rxCacheBuilder = new RxCache.Builder().init(sContext)
                //目前只支持Serializable和Gson缓存其它可以自己扩展
                .diskConverter(mHttpCacheConfig.getConverter())
                .diskDir(mHttpCacheConfig.getCacheDirectory())
                .appVersion(mHttpCacheConfig.getCacheVersion());
    }

    /**
     * 获取网络框架单例
     *
     * @return 框架单例
     */
    public static HttpManager getInstance() {
        testInitialize();
        if (singleton == null) {
            synchronized (HttpManager.class) {
                if (singleton == null) {
                    singleton = new HttpManager();
                }
            }
        }
        return singleton;
    }

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     */
    public static void init(Application app) {
        sContext = app;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    /**
     * 检查是否初始化
     */
    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 HttpManager.init() 初始化！");
        }
    }

    public HttpClient getHttpClient() {
        return mHttpClient;
    }


    public static RxCache getRxCache() {
        return getInstance().rxCacheBuilder.build();
    }


    /**
     * 对外暴露 RxCache,方便自定义
     */
    public static RxCache.Builder getRxCacheBuilder() {
        return getInstance().rxCacheBuilder;
    }

    public HttpManager setHttpConfig(HttpConfig mHttpConfig) {
        this.mHttpConfig = mHttpConfig;
        return this;
    }

    public HttpConfig getHttpConfig() {
        return mHttpConfig;
    }

    public HttpCacheConfig getHttpCacheConfig() {
        return mHttpCacheConfig;
    }

    public HttpManager setHttpCacheConfig(HttpCacheConfig mHttpCacheConfig) {
        this.mHttpCacheConfig = mHttpCacheConfig;
        return this;
    }

    /**
     * 调试模式,默认打开所有的异常调试
     */
    public HttpManager debug(String tag) {
        debug(tag, true);
        return this;
    }

    /**
     * 调试模式,第二个参数表示所有catch住的log是否需要打印<br>
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,
     * 并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    public HttpManager debug(String tag, boolean isPrintException) {
        String tempTag = TextUtils.isEmpty(tag) ? "RxEasyHttp_" : tag;
        HttpLog.customTagPrefix = tempTag;
        HttpLog.allowE = isPrintException;
        HttpLog.allowD = isPrintException;
        HttpLog.allowI = isPrintException;
        HttpLog.allowV = isPrintException;
        return this;
    }


    /**
     * 全局设置OkHttp的缓存,默认是3天
     */
    public HttpManager setHttpCache(Cache cache) {
        this.mCache = cache;
        return this;
    }

    /**
     * 获取OkHttp的缓存<br>
     */
    public Cache getHttpCache() {
        return getInstance().mCache;
    }

    /**
     * 添加全局公共请求参数
     */
    public HttpManager addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) {
            mCommonParams = new HttpParams();
        }
        mCommonParams.put(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求参数
     */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /**
     * 获取全局公共请求头
     */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /**
     * 添加全局公共请求参数
     */
    public HttpManager addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) {
            mCommonHeaders = new HttpHeaders();
        }
        mCommonHeaders.put(commonHeaders);
        return this;
    }


    /**
     * get请求
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * post请求
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }


    /**
     * delete请求
     */
    public static DeleteRequest delete(String url) {

        return new DeleteRequest(url);
    }

    /**
     * 自定义请求
     */
    public static CustomRequest custom() {
        return new CustomRequest();
    }

    public static DownloadRequest downLoad(String url) {
        return new DownloadRequest(url);
    }

    public static PutRequest put(String url) {
        return new PutRequest(url);
    }

    /**
     * 取消订阅
     */
    public static void cancelSubscription(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        getRxCache().clear().compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        HttpLog.i("clearCache success!!!");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        HttpLog.i("clearCache err!!!");
                    }
                });
    }

    /**
     * 移除缓存（key）
     */
    public static void removeCache(String key) {
        getRxCache().remove(key).compose(RxUtil.<Boolean>io_main()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                HttpLog.i("removeCache success!!!");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    public CookieManger getCookieJar() {
        return mCookieJar;
    }

    public HttpManager setmCookieJar(CookieManger mCookieJar) {
        this.mCookieJar = mCookieJar;
        getHttpClient().getOkHttpClientBuilder().cookieJar(mCookieJar);
        return this;
    }
}
