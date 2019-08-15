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

    @Override
    protected void initDev(Context context, String env) {
        SUNMI_STORE_URL = "http://store.dev.sunmi.com/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";
    }

    @Override
    protected void initTest(Context context, String env) {
        SUNMI_STORE_URL = "http://test-store.sunmi.com:30301/";
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";
    }

    @Override
    protected void initRelease(Context context, String env) {
        SUNMI_STORE_URL = "https://store.sunmi.com:443/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";
    }

    @Override
    protected void initUat(Context context, String env) {
        SUNMI_STORE_URL = "https://uat-store.sunmi.com:443/";
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";
    }

}
