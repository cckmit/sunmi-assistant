package com.sunmi.assistant.config;

import android.content.Context;
import android.text.TextUtils;

import com.sunmi.apmanager.config.ApConfig;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.DBUtils;
import com.sunmi.apmanager.utils.FileHelper;
import com.sunmi.apmanager.utils.SpUtils;
import com.sunmi.sunmiservice.SunmiServiceConfig;
import com.tencent.bugly.crashreport.CrashReport;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.UnknownException;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class BootLoader {
    private Context context;
//    private RefWatcher refWatcher;

    public BootLoader(Context context) {
        this.context = context;
    }

    public void init() {
        String env = Utils.getMetaValue(context, "ENV_DATA", ApConfig.ENV_TEST);
        new CommonConfig().init(context, env);
        new ApConfig().init(context, env);
        new SunmiServiceConfig().init(context, env);

        LogCat.init(!TextUtils.equals(env, ApConfig.ENV_RELEASE));//log 开关

        //file
        FileHelper.getInstance();
        //初始化创建数据库
        DBUtils.initDb(context);
        captureUnknownException();
//        refWatcher = LeakCanary.install(context);
        //bugly
        CrashReport.initCrashReport(context, ApConfig.BUGLY_ID, true);
        CrashReport.setUserId(SpUtils.getUID());
//trustAllCerts
        handleSSLHandshake();
    }

    //异常日志捕获
    private void captureUnknownException() {
        UnknownException mUnknownException = UnknownException.getInstance();
        mUnknownException.init(context, AppConfig.isLogSave);
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication application = (MyApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    /**
     * Glide加载https部分失败，设置信任证书
     */
    private void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS"); // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

}
