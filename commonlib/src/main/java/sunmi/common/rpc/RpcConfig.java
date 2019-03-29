package sunmi.common.rpc;

import android.content.Context;

import sunmi.common.base.BaseConfig;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class RpcConfig extends BaseConfig {

    public static String CLOUD_TOKEN = ""; //sign cloud params

    @Override
    protected void initDev(Context context, String env) {
        CLOUD_TOKEN = "Jihewobox15";
    }

    @Override
    protected void initTest(Context context, String env) {
        CLOUD_TOKEN = "Woyouxinxi666";
    }

    @Override
    protected void initRelease(Context context, String env) {
        CLOUD_TOKEN = "Jihewobox15";
    }

}
