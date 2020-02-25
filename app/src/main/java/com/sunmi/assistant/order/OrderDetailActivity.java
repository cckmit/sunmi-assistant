package com.sunmi.assistant.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.assistant.data.response.OrderDetailListResp;
import com.sunmi.assistant.order.model.OrderInfo;

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
    OrderInfo mOrderInfo;

    @ViewById(R.id.order_detail_amount)
    TextView mTvAmount;
    @ViewById(R.id.order_detail_state)
    TextView mTvState;
    @ViewById(R.id.order_no)
    TextView mTvOrderNo;
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
        mPresenter.loadDetail(mOrderInfo.getId());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        mTvAmount.setText(getResources().getString(R.string.order_amount, mOrderInfo.getAmount()));
        mTvState.setText(mOrderInfo.isOrderNormal() ?
                getResources().getString(R.string.order_success) : getResources().getString(R.string.order_refunds));
        mTvOrderNo.setText(String.valueOf(mOrderInfo.getNo()));
        mTvTime.setText(Utils.getDateTime(mOrderInfo.getPurchaseTime()));
        mTvType.setText(mOrderInfo.getPurchaseType());
        mDetailListAdapter = new DetailListAdapter(this);
        mDetailList.setDividerHeight(0);
        mDetailList.setAdapter(mDetailListAdapter);
        mDetailList.setOnTouchListener((v, event) -> {
            //  Action will not be forwarded
            return event.getAction() == MotionEvent.ACTION_MOVE;
        });
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
