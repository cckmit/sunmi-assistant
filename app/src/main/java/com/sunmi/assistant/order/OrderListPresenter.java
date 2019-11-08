package com.sunmi.assistant.order;

import android.util.Log;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;
import com.sunmi.assistant.order.model.OrderInfo;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.FilterItem;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.NetworkUtils;
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
    private int mTotalCount = 0;
    private int mCurrentCount = 0;
    private int mCompanyId;
    private int mShopId;

    private String mDefaultFilterAllName;
    private String mDefaultOrderTypeFilterName;
    private String mDefaultPayTypeFilterName;

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
        mDefaultFilterAllName = mView.getContext().getString(R.string.order_filter_all);
        mDefaultOrderTypeFilterName = mView.getContext().getString(R.string.order_filter_order_type);
        mDefaultPayTypeFilterName = mView.getContext().getString(R.string.order_filter_pay_type);

        List<FilterItem> order = new ArrayList<>(2);
        order.add(new FilterItem(-1,
                mView.getContext().getString(R.string.order_filter_sort)));
        order.add(new FilterItem(1,
                mView.getContext().getString(R.string.order_amount_descending)));
        order.add(new FilterItem(0,
                mView.getContext().getString(R.string.order_amount_ascending)));
        order.add(new FilterItem(11,
                mView.getContext().getString(R.string.order_time_descending)));
        order.add(new FilterItem(10,
                mView.getContext().getString(R.string.order_time_ascending)));
        order.get(0).setChecked(true);

        mFilterCurrent.put(0, order.get(0));
        mView.updateFilter(0, order);

        PaymentApi.get().getOrderPurchaseTypeList(mCompanyId, mShopId, new RetrofitCallback<OrderPayTypeListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderPayTypeListResp data) {
                List<OrderPayTypeListResp.PayType> list = data.getPurchase_type_list();
                List<FilterItem> payType = new ArrayList<>(list.size());
                FilterItem first = new FilterItem(-1, mDefaultPayTypeFilterName, mDefaultFilterAllName);
                first.setChecked(true);
                mFilterCurrent.put(1, first);
                payType.add(first);
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

        PaymentApi.get().getOrderTypeList(mCompanyId, mShopId, new RetrofitCallback<OrderTypeListResp>() {
            @Override
            public void onSuccess(int code, String msg, OrderTypeListResp data) {
                List<OrderTypeListResp.OrderType> list = data.getOrder_type_list();
                List<FilterItem> orderType = new ArrayList<>(list.size());
                FilterItem first = new FilterItem(-1, mDefaultOrderTypeFilterName, mDefaultFilterAllName);
                first.setChecked(true);
                mFilterCurrent.put(2, first);
                orderType.add(first);
                for (OrderTypeListResp.OrderType type : list) {
                    mOrderType.put(type.getId(), type.getTag());
                    FilterItem item = new FilterItem(type.getId(), type.getName());
                    if (mInitOrderType != OrderInfo.ORDER_TYPE_ALL) {
                        Integer typeIndex = OrderInfo.ORDER_TYPE_MAP.get(type.getTag());
                        if (typeIndex != null && mInitOrderType == typeIndex) {
                            first.setChecked(false);
                            item.setChecked(true);
                            mFilterCurrent.put(2, item);
                            mFilterOrderType.clear();
                            if (item.getId() != -1) {
                                mFilterOrderType.add(item.getId());
                            }
                        }
                    }
                    orderType.add(item);
                }
                if (isViewAttached()) {
                    mView.updateFilter(2, orderType);
                    loadData(true);
                }
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
                if (model.getId() != -1) {
                    mFilterPayType.add(model.getId());
                }
                break;
            case 2:
                mFilterOrderType.clear();
                if (model.getId() != -1) {
                    mFilterOrderType.add(model.getId());
                }
                break;
            default:
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
    public boolean loadMore() {
        if (mCurrentCount >= mTotalCount) {
            return false;
        } else {
            loadData(false);
            return true;
        }
    }

    private void loadData(boolean refresh) {
        if (!isViewAttached()) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(mView.getContext())) {
            mView.shortTip(R.string.toast_networkIsExceptional);
            mView.setData(null);
            return;
        }
        if (refresh) {
            mCurrentPage = PAGE_INIT;
        } else {
            mCurrentPage++;
        }
        PaymentApi.get().getOrderList(mCompanyId, mShopId,
                mTimeStart / 1000, mTimeEnd / 1000, mFilterAmountSort, mFilterTimeSort,
                mFilterOrderType, mFilterPayType, mCurrentPage, PAGE_SIZE,
                new RetrofitCallback<OrderListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderListResp data) {
                        if (!isViewAttached()) {
                            return;
                        }
                        mTotalCount = data.getTotal_count();
                        List<OrderInfo> list = buildOrderList(data);
                        if (refresh) {
                            mView.setData(list);
                            mCurrentCount = list.size();
                        } else {
                            mView.addData(list);
                            mCurrentCount += list.size();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, OrderListResp data) {
                        Log.e(TAG, "Get order list FAILED. code=" + code + "; msg=" + msg);
                        if (!isViewAttached()) {
                            return;
                        }
                        mView.shortTip(R.string.toast_networkIsExceptional);
                        if (refresh) {
                            mView.setData(null);
                        }
                    }
                });
    }

    private List<OrderInfo> buildOrderList(OrderListResp data) {
        List<OrderInfo> list = new ArrayList<>(data.getOrder_list().size());
        for (OrderListResp.OrderItem item : data.getOrder_list()) {
            Integer order = OrderInfo.ORDER_TYPE_MAP.get(mOrderType.get(item.getOrder_type_id()));
            int orderType = order == null ? OrderInfo.ORDER_TYPE_NORMAL : order;

            float rawAmount = item.getAmount();
            float amount = orderType == OrderInfo.ORDER_TYPE_NORMAL ?
                    Math.abs(rawAmount) : -1 * Math.abs(rawAmount);
            list.add(new OrderInfo(item.getId(), item.getOrder_no(), amount, orderType,
                    item.getPurchase_type(), item.getPurchase_time() * 1000));
        }
        return list;
    }

}
