package sunmi.common.rpc.retrofit;

import android.support.annotation.NonNull;

import com.commonlibrary.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sunmi.common.base.BaseApplication;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.ToastUtils;

/**
 * Description:
 * Created by bruce on 2019/2/27.
 */
public abstract class RetrofitCallback<T> implements Callback<BaseResponse<T>> {
    @Override
    public void onResponse(@NonNull Call<BaseResponse<T>> call,
                           @NonNull Response<BaseResponse<T>> response) {
        if (response.code() == RpcErrorCode.HTTP_RESP_FORBID) {//请求被拒
            ToastUtils.toastForShort(BaseApplication.getContext(), R.string.tip_forbid_by_server);
        } else if (response.code() == RpcErrorCode.HTTP_RESP_UNKNOWN_REQUEST) {
            if (response.errorBody() != null) {
                try {
                    BaseResponse errorResp = new Gson()
                            .fromJson(response.errorBody().string(), BaseResponse.class);
                    if (RpcErrorCode.HTTP_INVALID_TOKEN == errorResp.getCode()
                            || RpcErrorCode.HTTP_EXPIRE_TOKEN == errorResp.getCode()
                            || RpcErrorCode.HTTP_JWT_TOKEN_EXPIRED == errorResp.getCode()) {
                        CommonHelper.logout();
                        GotoActivityUtils.gotoLoginActivity(BaseApplication.getContext(), "1");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (response.body() != null) {
                responseBodyHandle(response.body());
            } else if (response.errorBody() != null) {
                errorResponseHandle(response.errorBody());
            }
        }
    }

    private void errorResponseHandle(ResponseBody errorResponse) {//errorResponse
        try {
            BaseResponse errorResp = new Gson().fromJson(
                    errorResponse.string(), BaseResponse.class);
            if (errorResp != null) {
                onFail(errorResp.getCode(), errorResp.getMsg(), null);
            } else {
                onFail(0, errorResponse.string(), null);
            }
        } catch (Exception e) {
            onFail(0, "", null);
            e.printStackTrace();
        }
    }

    private void responseBodyHandle(BaseResponse<T> response) {
        if (response.getCode() == 1) {
            onSuccess(response.getCode(), response.getMsg(), response.getData());
        } else {
            onFail(response.getCode(), response.getMsg(), response.getData());
        }
    }

    @Override
    public void onFailure(@NonNull Call<BaseResponse<T>> call, @NonNull Throwable t) {
        int errCode = RpcErrorCode.RPC_COMMON_ERROR;
        if (t instanceof UnknownHostException || t instanceof TimeoutException) {
            errCode = RpcErrorCode.RPC_ERR_TIMEOUT;
        }
        onFail(errCode, t.getMessage(), null);
    }

    public abstract void onSuccess(int code, String msg, T data);

    public abstract void onFail(int code, String msg, T data);

}
