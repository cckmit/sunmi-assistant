package sunmi.common.rpc.retrofit;

import android.support.annotation.NonNull;

import com.commonlibrary.R;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sunmi.common.base.BaseApplication;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.ToastUtils;

/**
 * Description:
 * Created by bruce on 2019/2/27.
 */
public abstract class RetrofitCallback<T> implements Callback<BaseResponse<T>> {
    @Override
    public void onResponse(@NonNull Call<BaseResponse<T>> call,
                           @NonNull Response<BaseResponse<T>> response) {
        if (response.body() != null) {//http的Status Code = 200
            responseBodyHandle(response.body());
        } else if (response.code() == RpcErrorCode.HTTP_RESP_TOKEN_ERR
                || response.code() == RpcErrorCode.HTTP_RESP_TOKEN_EXPIRE) {//token错误或失效
//            SpUtils.logout();//todo
//            ActivityUtils.gotoLoginActivity("");
        } else if (response.code() == RpcErrorCode.HTTP_RESP_FORBID) {//请求被拒
            ToastUtils.toastForShort(BaseApplication.getContext(), R.string.tip_forbid_by_server);
        } else if (response.errorBody() != null) {
            errorResponseHandle(response.errorBody());
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
        onFail(0, t.getMessage(), null);
    }

    public abstract void onSuccess(int code, String msg, T data);

    public abstract void onFail(int code, String msg, T data);

}
