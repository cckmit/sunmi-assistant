package com.sunmi.assistant.order;

import android.content.Context;

import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.order.model.FilterItem;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public interface OrderListContract {

    interface View extends BaseView {

        Context getContext();

        void updateFilter(int filterIndex, List<FilterItem> list);

        void setData(List<OrderListResp.OrderItem> list);

        void addData(List<OrderListResp.OrderItem> list);
    }

    interface Presenter {

        void loadList(long timeStart, long timeEnd);

        void setFilterCurrent(int filterIndex, FilterItem model);

        void loadMore();
    }
}
