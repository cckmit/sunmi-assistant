package sunmi.common.constant;

import android.content.Context;

import sunmi.common.base.BaseConfig;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class CommonConfig extends BaseConfig {

    public static String SUNMI_STORE_URL = ""; //商米store地址
    public static String CLOUD_TOKEN = ""; //sign cloud params
    public static String DES_IV = "";
    public static String DES_KEY = "";
    public static boolean SUPPORT_PRINTER = true;//是否支持打印机

    //bugly
    public static String BUGLY_ID = "";

    /**
     * 小米推送appId
     */
    public static String MI_PUSH_APP_ID = "";

    /**
     * 小米推送appKey
     */
    public static String MI_PUSH_APP_KEY = "";

    public static String SERVICE_H5_URL = "";

    public static String SUNMI_H5_URL = "";

    @Override
    protected void initDev(Context context, String env) {
        SUNMI_STORE_URL = "https://store.dev.sunmi.com/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";

        BUGLY_ID = "3c7a8a198f";
        MI_PUSH_APP_ID = "2882303761518131246";
        MI_PUSH_APP_KEY = "5601813178246";

        SERVICE_H5_URL = "http://172.16.1.137:8080/index.html#/";
        SUNMI_H5_URL = "http://test.h5.sunmi.com/";
    }

    @Override
    protected void initTest(Context context, String env) {
        SUNMI_STORE_URL = "https://store.test.sunmi.com/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";

        BUGLY_ID = "a999228f6b";
        MI_PUSH_APP_ID = "2882303761518131246";
        MI_PUSH_APP_KEY = "5601813178246";

        SERVICE_H5_URL = "https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/h5/sunmi-assistant/index.html#/";
        SUNMI_H5_URL = "http://test.h5.sunmi.com/";
    }

    @Override
    protected void initRelease(Context context, String env) {
        SUNMI_STORE_URL = "https://store.sunmi.com/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";

        BUGLY_ID = "5329ac0432";
        MI_PUSH_APP_ID = "2882303761518131146";
        MI_PUSH_APP_KEY = "5611813137146";

        SERVICE_H5_URL = "https://wifi.cdn.sunmi.com/H5/Sunmi-Assistant/index.html#/";

        SUNMI_H5_URL = "https://h5.sunmi.com/";
    }

    @Override
    protected void initUat(Context context, String env) {
        SUNMI_STORE_URL = "https://store.uat.sunmi.com/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";

        BUGLY_ID = "5329ac0432";
        MI_PUSH_APP_ID = "2882303761518131146";
        MI_PUSH_APP_KEY = "5611813137146";

        SERVICE_H5_URL = "https://wifi.cdn.sunmi.com/UAT/H5/Sunmi-Assistant/index.html#/";
        SUNMI_H5_URL = "http://uat.h5.sunmi.com/";
    }

}
