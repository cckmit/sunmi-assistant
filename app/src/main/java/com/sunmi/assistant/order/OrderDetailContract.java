package com.sunmi.assistant.order;

import android.content.Context;

import com.sunmi.assistant.data.response.OrderDetailListResp;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public interface OrderDetailContract {

    interface View extends BaseView {

        Context getContext();

        void updateDetailList(List<OrderDetailListResp.DetailItem> list);

    }

    interface Presenter {

        void loadDetail(int orderId);

    }
}
