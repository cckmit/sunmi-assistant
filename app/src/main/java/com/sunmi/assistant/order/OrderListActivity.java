package com.sunmi.assistant.order;

import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;

@EActivity(R.layout.order_activity_list)
public class OrderListActivity extends BaseMvpActivity<OrderListPresenter>
        implements OrderListContract.View {

    @AfterViews
    void init() {
        mPresenter = new OrderListPresenter();
        mPresenter.attachView(this);
        mPresenter.loadList();
    }

    @Override
    protected void initView() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updatePayTypeFilter(List<OrderPayTypeListResp.PayType> list) {

    }

    @Override
    public void updateOrderTypeFilter(List<OrderTypeListResp.OrderType> list) {

    }
}
