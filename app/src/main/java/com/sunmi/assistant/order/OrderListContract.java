package com.sunmi.assistant.order;

import android.content.Context;

import com.sunmi.assistant.order.model.FilterItem;
import com.sunmi.assistant.order.model.OrderInfo;

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

        void setData(List<OrderInfo> list);

        void addData(List<OrderInfo> list);
    }

    interface Presenter {

        void loadList(long timeStart, long timeEnd, int initState);

        void setFilterCurrent(int filterIndex, FilterItem model);

        void loadMore();
    }
}
