package cwj.androidfilemanage.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.danikula.videocache.HttpProxyCacheServer;
import com.zhouyou.http.HttpManager;
import com.zhouyou.http.HttpCacheConfig;
import com.zhouyou.http.HttpConfig;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.utils.HttpLog;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import cwj.androidfilemanage.bean.DaoMaster;
import cwj.androidfilemanage.bean.DaoSession;
import cwj.androidfilemanage.constant.AppConstant;
import cwj.androidfilemanage.interceptor.CustomSignInterceptor;
import cwj.androidfilemanage.utils.SystemInfoUtils;

/**
 *
 * @author wenlu
 * @date 2017/3/20
 */

public class BaseApplication extends Application {
    private static DaoSession daoSession;
    private static Application app = null;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //配置数据库
        setupDatabase();

        //这里涉及到安全我把url去掉了，demo都是调试通的
        String Url = "http://www.xxx.com";


        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.put("User-Agent", SystemInfoUtils.getUserAgent(this, AppConstant.APPID));
        //设置请求参数
        HttpParams params = new HttpParams();
        params.put("appId", AppConstant.APPID);

        HttpConfig httpConfig = new HttpConfig()
                .setBaseUrl(Url)
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 1000)
                .setConnectTimeout(60 * 1000)
                //默认网络不好自动重试3次
                .setRetryCount(3)
                //每次延时500ms重试
                .setRetryDelay(500)
                //每次延时叠加500ms
                .setRetryIncreaseDelay(500)
                //信任所有证书
                .setCertificates()
                //全局访问规则
                .setHostnameVerifier(new UnSafeHostnameVerifier(Url));
        HttpCacheConfig httpCacheConfig = new HttpCacheConfig()
                //默认缓存使用序列化转化
                .setCacheDiskConverter(new SerializableDiskConverter())
                //设置缓存大小为50M
                .setCacheMaxSize(50 * 1024 * 1024)
                //缓存版本为1
                .setCacheVersion(1);

        HttpManager.init(this);
        HttpManager.getInstance()
                .debug("RxEasyHttp", true)
                .setHttpConfig(httpConfig)
                .setHttpCacheConfig(httpCacheConfig)
                //设置全局公共头
                .addCommonHeaders(headers)
                //设置全局公共参数
                .addCommonParams(params)
                //添加参数签名拦截器
                .getHttpClient().getOkHttpClientBuilder().addInterceptor(new CustomSignInterceptor());
        //.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
        //.addInterceptor(new HeTInterceptor());//处理自己业务的拦截器


    }

    public class UnSafeHostnameVerifier implements HostnameVerifier {
        private String host;

        public UnSafeHostnameVerifier(String host) {
            this.host = host;
            HttpLog.i("###############　UnSafeHostnameVerifier " + host);
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            HttpLog.i("############### verify " + hostname + " " + this.host);
            return this.host != null && !"".equals(this.host) && this.host.contains(hostname);
        }
    }


    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "file.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    /**
     * 获取Application的Context
     **/
    public static Context getAppContext() {
        if (app == null) {
            return null;
        }
        return app.getApplicationContext();
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
