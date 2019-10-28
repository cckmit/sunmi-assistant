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

    /**
     * 小米推送appId
     */
    public static String MI_PUSH_APP_ID = "";

    /**
     * 小米推送appKey
     */
    public static String MI_PUSH_APP_KEY = "";

    @Override
    protected void initDev(Context context, String env) {
        SUNMI_STORE_URL = "https://store.dev.sunmi.com/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";

        MI_PUSH_APP_ID = "2882303761518131246";
        MI_PUSH_APP_KEY = "5601813178246";
    }

    @Override
    protected void initTest(Context context, String env) {
        SUNMI_STORE_URL = "https://store.test.sunmi.com/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";

        MI_PUSH_APP_ID = "2882303761518131246";
        MI_PUSH_APP_KEY = "5601813178246";
    }

    @Override
    protected void initRelease(Context context, String env) {
        SUNMI_STORE_URL = "https://store.sunmi.com/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";

        MI_PUSH_APP_ID = "2882303761518131146";
        MI_PUSH_APP_KEY = "5611813137146";
    }

    @Override
    protected void initUat(Context context, String env) {
        SUNMI_STORE_URL = "https://store.uat.sunmi.com/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";
        MI_PUSH_APP_ID = "2882303761518131146";
        MI_PUSH_APP_KEY = "5611813137146";
    }

}
