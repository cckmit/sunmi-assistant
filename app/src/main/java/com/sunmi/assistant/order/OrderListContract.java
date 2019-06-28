package com.sunmi.assistant.order;

import android.content.Context;

import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;

import java.util.List;

import sunmi.common.base.BaseView;

public interface OrderListContract {

    interface View extends BaseView {

        Context getContext();

        void updatePayTypeFilter(List<OrderPayTypeListResp.PayType> list);

        void updateOrderTypeFilter(List<OrderTypeListResp.OrderType> list);

    }

    interface Presenter {

        void loadList();

    }
}
