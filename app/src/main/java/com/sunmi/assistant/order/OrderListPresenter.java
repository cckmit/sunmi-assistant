package com.sunmi.assistant.order;

import android.util.Log;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;
import com.sunmi.assistant.order.model.FilterItem;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class OrderListPresenter extends BasePresenter<OrderListContract.View>
        implements OrderListContract.Presenter {

    private static final String TAG = "OrderListPresenter";

    private SparseArray<FilterItem> mFilterCurrent = new SparseArray<>(4);

    @Override
    public void loadList(long timeStart, long timeEnd) {
        List<FilterItem> amount = new ArrayList<>(2);
        amount.add(new FilterItem(1,
                mView.getContext().getString(R.string.order_amount_descending)));
        amount.add(new FilterItem(0,
                mView.getContext().getString(R.string.order_amount_ascending)));

        List<FilterItem> time = new ArrayList<>(2);
        time.add(new FilterItem(1,
                mView.getContext().getString(R.string.order_time_descending)));
        time.add(new FilterItem(0,
                mView.getContext().getString(R.string.order_time_ascending)));

        mView.updateFilter(0, amount);
        mView.updateFilter(3, time);

        SunmiStoreRemote.get().getOrderPurchaseTypeList(new RetrofitCallback<OrderPayTypeListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderPayTypeListResp data) {
                List<OrderPayTypeListResp.PayType> list = data.getPurchase_type_list();
                List<FilterItem> payType = new ArrayList<>(list.size());
                for (OrderPayTypeListResp.PayType type : list) {
                    payType.add(new FilterItem(type.getId(), type.getName()));
                }
                mView.updateFilter(1, payType);
            }

            @Override
            public void onFail(int code, String msg, OrderPayTypeListResp data) {
                Log.e(TAG, "Get purchase type list FAILED. code=" + code + "; msg=" + msg);
            }
        });

        SunmiStoreRemote.get().getOrderTypeList(new RetrofitCallback<OrderTypeListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderTypeListResp data) {
                List<OrderTypeListResp.OrderType> list = data.getOrder_type_list();
                List<FilterItem> orderType = new ArrayList<>(list.size());
                for (OrderTypeListResp.OrderType type : list) {
                    orderType.add(new FilterItem(type.getId(), type.getName()));
                }
                mView.updateFilter(2, orderType);
            }

            @Override
            public void onFail(int code, String msg, OrderTypeListResp data) {
                Log.e(TAG, "Get order type list FAILED. code=" + code + "; msg=" + msg);
            }
        });

        SunmiStoreRemote.get().getOrderList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                timeStart, timeEnd, -1, -1, null, null,
                0, 20, new RetrofitCallback<OrderListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderListResp data) {
                        mView.setData(data.getOrder_list());
                    }

                    @Override
                    public void onFail(int code, String msg, OrderListResp data) {
                        Log.e(TAG, "Get order list FAILED. code=" + code + "; msg=" + msg);
                    }
                });
    }

    @Override
    public void setFilterCurrent(int filterIndex, FilterItem model) {
        FilterItem current = mFilterCurrent.get(filterIndex);
        if (current == model) {
            return;
        }
        if (current != null) {
            current.setChecked(false);
        }
        model.setChecked(true);
        mFilterCurrent.put(filterIndex, model);
    }

}
