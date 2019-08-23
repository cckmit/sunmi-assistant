package sunmi.common.rpc.http;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SecurityUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/1/24.
 */
public class BaseHttpApi {
    public static final String TAG = "HttpCall";
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
     * @param timeout  超时时间 s
     * @param callback 回调处理
     */
    public static void post(String url, Map<String, String> map, int timeout, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.post()
                .url(url)
                .params(map).build()//request Call
                .connTimeOut(timeout * 1000)
                .readTimeOut(timeout * 1000)
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
                    timeStamp + randomNum + SecurityUtils.md5(CommonConfig.CLOUD_TOKEN));
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
                    timeStamp + randomNum + SecurityUtils.md5(CommonConfig.CLOUD_TOKEN));
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
