package com.sunmi.assistant.order;

import android.util.Log;

import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderDetailListResp;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class OrderDetailPresenter extends BasePresenter<OrderDetailContract.View>
        implements OrderDetailContract.Presenter {

    private static final String TAG = "OrderDetailPresenter";

    @Override
    public void loadDetail(int orderId) {
        SunmiStoreRemote.get().getOrderDetailList(orderId, new RetrofitCallback<OrderDetailListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderDetailListResp data) {
                mView.updateDetailList(data.getDetail_list());
            }

            @Override
            public void onFail(int code, String msg, OrderDetailListResp data) {
                Log.e(TAG, "Get order detail FAILED. code=" + code + "; msg=" + msg);
            }
        });
    }
}
