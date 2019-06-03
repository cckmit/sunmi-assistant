package sunmi.common.constant;

import android.content.Context;

import sunmi.common.base.BaseConfig;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class CommonConfig extends BaseConfig {

    public static String CLOUD_TOKEN = ""; //sign cloud params
    public static String DES_IV = "";
    public static String DES_KEY = "";
    public static boolean SUPPORT_PRINTER;//是否支持打印机

    @Override
    protected void initDev(Context context, String env) {
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";
    }

    @Override
    protected void initTest(Context context, String env) {
        CLOUD_TOKEN = "Woyouxinxi666";
        DES_IV = "12345678";
        DES_KEY = "wywmxxkj";
    }

    @Override
    protected void initRelease(Context context, String env) {
        CLOUD_TOKEN = "Jihewobox15";
        DES_IV = "98765432";
        DES_KEY = "jihexxkj";
    }

}
