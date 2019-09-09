package com.sunmi.assistant.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sunmi.apmanager.config.ApConfig;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.cloudprinter.config.PrinterConfig;
import com.sunmi.sunmiservice.SunmiServiceConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.stat.StatConfig;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.DBUtils;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.SharedManager;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.UnknownException;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class BootLoader {
    private Context context;

    public BootLoader(Context context) {
        this.context = context;
    }

    public void init() {
        String env = Utils.getMetaValue(context, "ENV_DATA", ApConfig.ENV_TEST);
        if (!TextUtils.isEmpty(SharedManager.getValue(context, "TOKEN"))
                && !TextUtils.isEmpty(SpUtils.getSsoToken())
                && TextUtils.isEmpty(SpUtils.getStoreToken())) {
            SpUtils.setStoreToken(SpUtils.getSsoToken());
            SpUtils.setSsoToken(SharedManager.getValue(context, "TOKEN"));
            SharedManager.clearValue(context, "TOKEN");
        }
        new CommonConfig().init(context, env);
        new ApConfig().init(context, env);
        new SunmiServiceConfig().init(context, env);
        new PrinterConfig().init(context, env);

        LogCat.init(!TextUtils.equals(env, ApConfig.ENV_RELEASE));//log 开关

        //file
        FileHelper.getInstance();
        //初始化创建数据库
        DBUtils.initDb(context);
        captureUnknownException();
        //腾讯移动分析
        StatConfig.setDebugEnable(!TextUtils.equals(Utils.getMetaValue(context,
                "ENV_DATA", ApConfig.ENV_TEST), ApConfig.ENV_RELEASE));
        //内存监测
//        LeakCanary.install((Application) context);
        //bugly
        //trustAllCerts
        handleSSLHandshake();
        CrashReport.initCrashReport(context, ApConfig.BUGLY_ID, true);
        if (!TextUtils.isEmpty(SpUtils.getUID())) {
            CrashReport.setUserId(SpUtils.getUID());
        }
        initMiPush(context);
    }

    //异常日志捕获
    private void captureUnknownException() {
        UnknownException mUnknownException = UnknownException.getInstance();
        mUnknownException.init(context, AppConfig.isLogSave);
    }

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

    private void initMiPush(Context context) {
        MiPushClient.registerPush(context, CommonConfig.MI_PUSH_APP_ID, CommonConfig.MI_PUSH_APP_KEY);
        if (!TextUtils.isEmpty(SpUtils.getUID()))
            MiPushClient.setAlias(context, SpUtils.getUID(), null);
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d("mipush", content, t);
            }

            @Override
            public void log(String content) {
                Log.d("mipush", content);
            }
        };
        Logger.setLogger(context, newLogger);
    }

}
