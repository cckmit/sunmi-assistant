package com.sunmi.assistant.order;

import android.util.Log;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;
import com.sunmi.assistant.order.model.FilterItem;
import com.sunmi.assistant.order.model.OrderInfo;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class OrderListPresenter extends BasePresenter<OrderListContract.View>
        implements OrderListContract.Presenter {

    private static final String TAG = "OrderListPresenter";

    private static final int PAGE_INIT = 1;
    private static final int PAGE_SIZE = 20;

    private SparseArray<FilterItem> mFilterCurrent = new SparseArray<>(3);
    private int mFilterAmountSort = -1;
    private int mFilterTimeSort = -1;
    private List<Integer> mFilterPayType = new ArrayList<>(1);
    private List<Integer> mFilterOrderType = new ArrayList<>(1);

    private SparseArray<String> mOrderType = new SparseArray<>(2);

    private long mTimeStart;
    private long mTimeEnd;
    private int mInitOrderType;

    private int mCurrentPage = PAGE_INIT;
    private int mCompanyId;
    private int mShopId;

    @Override
    public void loadList(long timeStart, long timeEnd, int initOrderType) {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        mTimeStart = timeStart;
        mTimeEnd = timeEnd;
        mInitOrderType = initOrderType;

        if (!isViewAttached()) {
            return;
        }

        List<FilterItem> order = new ArrayList<>(2);
        order.add(new FilterItem(1,
                mView.getContext().getString(R.string.order_amount_descending)));
        order.add(new FilterItem(0,
                mView.getContext().getString(R.string.order_amount_ascending)));
        order.add(new FilterItem(11,
                mView.getContext().getString(R.string.order_time_descending)));
        order.add(new FilterItem(10,
                mView.getContext().getString(R.string.order_time_ascending)));

        mView.updateFilter(0, order);

        SunmiStoreRemote.get().getOrderPurchaseTypeList(new RetrofitCallback<OrderPayTypeListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderPayTypeListResp data) {
                List<OrderPayTypeListResp.PayType> list = data.getPurchase_type_list();
                List<FilterItem> payType = new ArrayList<>(list.size());
                for (OrderPayTypeListResp.PayType type : list) {
                    payType.add(new FilterItem(type.getId(), type.getName()));
                }
                if (isViewAttached()) {
                    mView.updateFilter(1, payType);
                }
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
                    mOrderType.put(type.getId(), type.getTag());
                    FilterItem item = new FilterItem(type.getId(), type.getName());
                    if (mInitOrderType != OrderInfo.ORDER_TYPE_ALL) {
                        if ((mInitOrderType == OrderInfo.ORDER_TYPE_NORMAL
                                && CommonConstants.ORDER_TYPE_NORMAL.equals(type.getTag()))
                                || (mInitOrderType == OrderInfo.ORDER_TYPE_REFUNDS
                                && CommonConstants.ORDER_TYPE_REFUNDS.equals(type.getTag()))) {
                            item.setChecked(true);
                        }
                    }
                    orderType.add(item);
                }
                if (isViewAttached()) {
                    mView.updateFilter(2, orderType);
                }
                loadData(true);
            }

            @Override
            public void onFail(int code, String msg, OrderTypeListResp data) {
                Log.e(TAG, "Get order type list FAILED. code=" + code + "; msg=" + msg);
            }
        });
    }

    @Override
    public void setFilterCurrent(int filterIndex, FilterItem model) {
        // Update order list
        switch (filterIndex) {
            case 0:
                if (model.getId() < 10) {
                    mFilterAmountSort = model.getId();
                    mFilterTimeSort = -1;
                } else {
                    mFilterAmountSort = -1;
                    mFilterTimeSort = model.getId() - 10;
                }
                break;
            case 1:
                mFilterPayType.clear();
                mFilterPayType.add(model.getId());
                break;
            case 2:
                mFilterOrderType.clear();
                mFilterOrderType.add(model.getId());
                break;
        }
        loadData(true);
        // Update model data & update dropdown menu item view.
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

    @Override
    public void loadMore() {
        loadData(false);
    }

    private void loadData(boolean refresh) {
        if (refresh) {
            mCurrentPage = PAGE_INIT;
        } else {
            mCurrentPage++;
        }
        SunmiStoreRemote.get().getOrderList(mCompanyId, mShopId,
                mTimeStart, mTimeEnd, mFilterAmountSort, mFilterTimeSort,
                mFilterOrderType, mFilterPayType, mCurrentPage, PAGE_SIZE,
                new RetrofitCallback<OrderListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderListResp data) {
                        if (!isViewAttached()) {
                            return;
                        }
                        List<OrderInfo> list = buildOrderList(data);
                        if (refresh) {
                            mView.setData(list);
                        } else {
                            mView.addData(list);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, OrderListResp data) {
                        Log.e(TAG, "Get order list FAILED. code=" + code + "; msg=" + msg);
                        if (isViewAttached() && refresh) {
                            mView.setData(null);
                        }
                    }
                });
    }

    private List<OrderInfo> buildOrderList(OrderListResp data) {
        List<OrderInfo> list = new ArrayList<>(data.getOrder_list().size());
        for (OrderListResp.OrderItem item : data.getOrder_list()) {
            int orderType = CommonConstants.ORDER_TYPE_NORMAL.equals(
                    mOrderType.get(item.getOrder_type_id())) ?
                    OrderInfo.ORDER_TYPE_NORMAL : OrderInfo.ORDER_TYPE_REFUNDS;
            float rawAmount = item.getAmount();
            float amount = orderType == OrderInfo.ORDER_TYPE_NORMAL ?
                    Math.abs(rawAmount) : -1 * Math.abs(rawAmount);
            list.add(new OrderInfo(item.getId(), amount, orderType,
                    item.getPurchase_type(), item.getPurchase_time() * 1000));
        }
        return list;
    }

}
