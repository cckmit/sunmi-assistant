package sunmi.common.rpc.http;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sunmi.common.rpc.RpcConfig;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SecurityUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/1/24.
 */
public class BaseHttpApi {
    private static final String TAG = "HttpCall";
    //net connect time
    public static final int CONN_TIMEOUT = 1000 * 10;

    /**
     * okHttp post
     *
     * @param url      地址
     * @param map      传递参数
     * @param callback 回调处理
     */
    public static void post(String url, Map<String, String> map, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.post()
                .url(url).params(map).build()//request Call
                .execute(callback);
    }

    /**
     * okHttp post
     *
     * @param url      地址
     * @param map      传递参数
     * @param callback 回调处理
     */
    protected static void postWithFile(String url, Map<String, String> map, String paramName,
                                       String fileName, File file, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.post()
                .url(url).params(map).addFile(paramName, fileName, file)
                .build()//request Call
                .connTimeOut(CONN_TIMEOUT)
                .readTimeOut(CONN_TIMEOUT)
                .execute(callback);
    }

    /**
     * 参数加签
     */
    protected static Map<String, String> getSignedParams(String params) {
        Map<String, String> map = new HashMap<>();
        try {
            String timeStamp = DateTimeUtils.currentTimeSecond() + "";
            String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String isEncrypted = "0";
            String sign = SecurityUtils.md5(params + isEncrypted +
                    timeStamp + randomNum + SecurityUtils.md5(RpcConfig.CLOUD_TOKEN));
            map.put("timeStamp", timeStamp);
            map.put("randomNum", randomNum);
            map.put("isEncrypted", isEncrypted);
            map.put("params", params);
            map.put("sign", sign);
            map.put("lang", "zh");
        } catch (Exception e) {
            LogCat.e(TAG, "getSignedParams error,", e);
        }
        return map;
    }

    /**
     * HashMap 参数加签
     */
    protected static HashMap<String, String> getHashMapSignedParams(String params) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String timeStamp = DateTimeUtils.currentTimeSecond() + "";
            String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String isEncrypted = "0";
            String sign = SecurityUtils.md5(params + isEncrypted +
                    timeStamp + randomNum + SecurityUtils.md5(RpcConfig.CLOUD_TOKEN));
            map.put("timeStamp", timeStamp);
            map.put("randomNum", randomNum);
            map.put("isEncrypted", isEncrypted);
            map.put("params", params);
            map.put("sign", sign);
            map.put("lang", "zh");
        } catch (Exception e) {
            LogCat.e(TAG, "getSignedParams error,", e);
        }
        return map;
    }

}
