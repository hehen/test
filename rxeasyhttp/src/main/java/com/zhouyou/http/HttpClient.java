package com.zhouyou.http;

import com.zhouyou.http.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author wenlu
 * @desc
 * @date 2018/8/15 13:23
 */
public class HttpClient {
    private volatile static Retrofit gRetrofit = null;
    /**
     * OkHttp请求的客户端
     */
    private OkHttpClient.Builder okHttpClientBuilder;
    /**
     * Retrofit请求Builder
     */
    private Retrofit.Builder retrofitBuilder;
    /**
     * 参数配置
     */
    private HttpConfig mHttpConfig;
    /**
     * 缓存参数配置
     */
    private HttpCacheConfig mHttpCacheConfig;
    private OkHttpClient mOkHttpClient;


    /**
     * 获取网络框架单例
     *
     * @return 框架单例
     */
    public static Retrofit getInstance() {
        if (gRetrofit == null) {
            synchronized (HttpClient.class) {
                if (gRetrofit == null) {
                    gRetrofit = HttpManager.getInstance().getHttpClient().buildRetrofit();
                }
            }
        }
        return gRetrofit;
    }

    public HttpClient(HttpConfig httpConfig) {
        mHttpConfig = httpConfig;

        okHttpClientBuilder = new OkHttpClient.Builder();
        retrofitBuilder = new Retrofit.Builder();
    }

    private OkHttpClient buildOkHttp() {
        //TODO SSL默认允许所有连接
        okHttpClientBuilder.hostnameVerifier(mHttpConfig.getmHostnameVerifier());
        okHttpClientBuilder.connectTimeout(mHttpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(mHttpConfig.getReadTimeOut(), TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(mHttpConfig.getWriteTimeout(), TimeUnit.MILLISECONDS);
        if (mHttpConfig.getmSslParams() != null) {
            okHttpClientBuilder.sslSocketFactory(mHttpConfig.getmSslParams().sSLSocketFactory, mHttpConfig.getmSslParams().trustManager);
        }
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(this.getClass().getName());
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }
        return okHttpClientBuilder.build();
    }

    private Retrofit buildRetrofit() {
        mOkHttpClient = buildOkHttp();
        retrofitBuilder.client(mOkHttpClient);
        //增加RxJava2CallAdapterFactory
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofitBuilder.build();
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return okHttpClientBuilder;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = buildOkHttp();
        }
        return mOkHttpClient;
    }
}
