package sunmi.common.rpc.retrofit;

import android.text.TextUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import sunmi.common.base.BaseApplication;
import sunmi.common.base.BaseConfig;
import sunmi.common.rpc.retrofit.logging.Level;
import sunmi.common.rpc.retrofit.logging.LoggingInterceptor;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/2/28.
 */
public class BaseRetrofitClient {
    //超时时间
    private static final int DEFAULT_TIMEOUT = 10;
    //缓存时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;

    private static Retrofit retrofit;
    private static Map<String, Object> interfaceCacheList = new ConcurrentHashMap<>();

    private Cache cache = null;
    private File httpCacheDirectory;

    protected void init(String url, Map<String, String> headers) {
        init(url, headers, DEFAULT_TIMEOUT);
    }

    public void init(String url, Map<String, String> headers, int timeout) {
        interfaceCacheList.clear();
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(BaseApplication.getContext().getCacheDir(), "esl_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
            }
        } catch (Exception e) {
            LogCat.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BaseInterceptor(headers))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(!TextUtils.equals(Utils.getMetaValue(BaseApplication.getContext(),
                                "ENV_DATA", BaseConfig.ENV_RELEASE), BaseConfig.ENV_RELEASE)) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .build()
                )
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ConvertFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        if (!interfaceCacheList.containsKey(service.getName())) {
            interfaceCacheList.put(service.getName(), retrofit.create(service));
        }
        //noinspection unchecked
        return (T) interfaceCacheList.get(service.getName());
//        return retrofit.create(service);
    }

    /**
     * execute your customer API
     */
//    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
//        observable.subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//
//        return null;
//    }

}
