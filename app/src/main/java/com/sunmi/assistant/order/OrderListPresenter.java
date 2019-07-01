package com.sunmi.assistant.order;

import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.order.model.FilterItem;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class OrderListPresenter extends BasePresenter<OrderListContract.View>
        implements OrderListContract.Presenter {

    private SparseArray<FilterItem> mFilterCurrent = new SparseArray<>(4);

    @Override
    public void loadList() {
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
