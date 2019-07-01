package com.sunmi.assistant.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.response.OrderDetailListResp;
import com.sunmi.assistant.data.response.OrderListResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;

/**
 * @author yinhui
 * @since 2019-07-01
 */
@SuppressLint("Registered")
@EActivity(R.layout.order_activity_detail)
public class OrderDetailActivity extends BaseMvpActivity<OrderDetailPresenter>
        implements OrderDetailContract.View {

    @Extra
    OrderListResp.OrderItem mOrderItem;

    @ViewById(R.id.order_detail_amount)
    TextView mTvAmount;
    @ViewById(R.id.order_id)
    TextView mTvOrderId;
    @ViewById(R.id.order_time)
    TextView mTvTime;
    @ViewById(R.id.order_type)
    TextView mTvType;
    @ViewById(R.id.order_detail_list)
    ListView mDetailList;
    private DetailListAdapter mDetailListAdapter;

    @AfterViews
    void init() {
        initViews();
        mPresenter = new OrderDetailPresenter();
        mPresenter.attachView(this);
        mPresenter.loadDetail(mOrderItem.getId());
    }

    private void initViews() {
        mTvAmount.setText(String.valueOf(mOrderItem.getAmount()));
        mTvOrderId.setText(String.valueOf(mOrderItem.getId()));
        mTvTime.setText(String.valueOf(mOrderItem.getPurchase_time()));
        mTvType.setText(mOrderItem.getPurchase_type());
        mDetailListAdapter = new DetailListAdapter(this);
        mDetailList.setAdapter(mDetailListAdapter);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateDetailList(List<OrderDetailListResp.DetailItem> list) {
        mDetailListAdapter.setDatas(list);
        mDetailListAdapter.notifyDataSetChanged();
    }

    private static class DetailListAdapter extends CommonAdapter<OrderDetailListResp.DetailItem> {

        private DetailListAdapter(Context context) {
            super(context, R.layout.order_detail_list_item);
        }

        @Override
        public void convert(ViewHolder holder, OrderDetailListResp.DetailItem item) {
            TextView name = holder.getView(R.id.order_detail_name);
            TextView count = holder.getView(R.id.order_detail_count);
            name.setText(item.getName());
            count.setText(String.valueOf(item.getQuantity()));
        }
    }
}
