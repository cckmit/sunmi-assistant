package sunmi.common.rpc.sunmicall;

import android.content.Context;

import com.commonlibrary.R;

import java.io.IOException;
import java.util.Map;

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
    private static final String TAG = "LocalApApi";

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        LogCat.e(TAG, "post: sn = " + sn + ", opCode：" + opCode + "，json = " + json);
        postRouter(json, opCode, sn);
    }

    public abstract String getBaseUrl();

    public abstract Map<String, String> getHeader();

    public abstract void onFail(ResponseBean res);

    public abstract void onSuccess(String result, String sn);

    /**
     * ap路由器接口
     */
    public void postRouter(final String strJson, final int opCode, final String sn) {
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
        mBuilder.sslSocketFactory(new SSLSocketFactoryGenerator().generate());
        mBuilder.hostnameVerifier(new OKHttpUtils.TrustAllHostnameVerifier());
        OkHttpClient okHttpClient = mBuilder.build();

        Call call = okHttpClient.newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogCat.e(TAG, "execute fail: e = " + e);
                errorTip(BaseApplication.getContext());
                ResponseBean res = new ResponseBean();
                res.setErrCode(RpcErrorCode.WHAT_ERROR + "");
                BaseNotification.newInstance().postNotificationName(opCode, res);
                onFail(res);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //注意此处必须new 一个string 不然的话直接调用response.body().string()会崩溃，因为此处流只调用一次然后关闭了
                String result = response.body().string();
                LogCat.e(TAG, "local execute success: opCode = " + opCode + ", result = " + result);
                onSuccess(result, sn);
            }
        });
    }

    private void errorTip(Context context) {
        ToastUtils.toastForShort(context, NetworkUtils.isNetworkAvailable(context)
                ? R.string.toast_network_Exception : R.string.network_wifi_low);
    }

}
