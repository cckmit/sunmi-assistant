package sunmi.common.rpc.sunmicall;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.commonlibrary.R;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sunmi.common.base.BaseApplication;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.SSLSocketFactoryGenerator;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.OKHttpUtils;
import sunmi.common.utils.ToastUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public abstract class BaseLocalApi extends BaseApi {
    private static final String TAG = "BaseLocalApi";

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        LogCat.e(TAG, "post: sn = " + sn + ", opCode：" + opCode + "，json = " + json);
        postRouter(sn, opCode, json);
    }

    public abstract String getBaseUrl();

    protected SSLSocketFactory getSSLSocketFactory() {
        return new SSLSocketFactoryGenerator().generate();
    }

    public abstract Map<String, String> getHeader();

    public abstract void onFail(ResponseBean res);

    public abstract void onSuccess(String result, String sn);

    /**
     * ap路由器接口
     */
    public void postRouter(final String sn, final int opCode, final String strJson) {
        postRouterTimeout(sn, opCode, strJson, 10);
    }

    /**
     * ap路由器接口
     */
    public void postRouterTimeout(final String sn, final int opCode, final String strJson, long timeout) {
        //Request
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, strJson);//请求json内容
        Request.Builder request = new Request.Builder().url(getBaseUrl()).post(requestBody);
        if (getHeader() != null) {
            for (Map.Entry<String, String> entry : getHeader().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //OkHttpClient
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(getSSLSocketFactory());
        mBuilder.hostnameVerifier(new OKHttpUtils.TrustAllHostnameVerifier());
        mBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
        mBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        mBuilder.writeTimeout(timeout, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = mBuilder.build();

        Call call = okHttpClient.newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogCat.e(TAG, "execute fail: e = " + e);
                if (!(e instanceof ConnectException || e instanceof SSLHandshakeException)) {
                    errorTip(BaseApplication.getContext());
                }
                ResponseBean res = new ResponseBean();
                res.setErrCode(RpcErrorCode.RPC_COMMON_ERROR + "");
                BaseNotification.newInstance().postNotificationName(opCode, res);
                onFail(res);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //注意此处必须new 一个string 不然的话直接调用response.body().string()会崩溃，因为此处流只调用一次然后关闭了
                String result = "";
                if (response.body() != null) {
                    try {
                        result = response.body().string();
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(result)) {
                    LogCat.e(TAG, "execute fail.");
                    ResponseBean res = new ResponseBean();
                    res.setErrCode(RpcErrorCode.RPC_ERR_TIMEOUT + "");
                    res.setDataErrCode(RpcErrorCode.RPC_ERR_TIMEOUT);
                    BaseNotification.newInstance().postNotificationName(opCode, res);
                    onFail(res);
                } else {
                    LogCat.e(TAG, "local execute success: opCode = " + opCode + ", result = " + result);
                    ResponseBean res = new ResponseBean(result);
                    BaseNotification.newInstance().postNotificationName(opCode, res);
                    onSuccess(result, sn);
                }
            }
        });
    }

    private void errorTip(Context context) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtils.toastForShort(context, R.string.network_wifi_low);
        }
    }

}
