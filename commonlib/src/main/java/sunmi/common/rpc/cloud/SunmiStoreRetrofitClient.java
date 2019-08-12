package sunmi.common.rpc.cloud;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.retrofit.BaseRetrofitClient;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/5/7.
 */
public class SunmiStoreRetrofitClient extends BaseRetrofitClient {

    private static volatile SunmiStoreRetrofitClient instance;

    public static SunmiStoreRetrofitClient getInstance() {
        if (instance == null) {
            synchronized (SunmiStoreRetrofitClient.class) {
                if (instance == null)
                    instance = new SunmiStoreRetrofitClient();
            }
        }
        return instance;
    }

    private SunmiStoreRetrofitClient() {
        init(CommonConfig.SUNMI_STORE_URL, getHeaders());
    }

    public static void createInstance() {
        if (instance != null) {
            instance = null;
        }
        instance = new SunmiStoreRetrofitClient();
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (!TextUtils.isEmpty(SpUtils.getStoreToken())) {
            headers.put("Authorization", "Bearer " + SpUtils.getStoreToken());
        }
        return headers;
    }

}
