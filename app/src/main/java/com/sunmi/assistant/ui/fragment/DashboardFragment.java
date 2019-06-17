package com.sunmi.assistant.ui.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DashboardPresenter;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.DashboardConfig;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.dashboard.type.BarChartCardType;
import com.sunmi.assistant.dashboard.type.DataCardType;
import com.sunmi.assistant.dashboard.type.PieChartCardType;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.utils.CommonHelper;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View {

    @ViewById(R.id.tv_dashboard_company_name)
    TextView mCompanyName;
    @ViewById(R.id.tv_dashboard_store_name)
    TextView mStoreName;

    @ViewById(R.id.tv_dashboard_today)
    TextView mTimeSpanToday;
    @ViewById(R.id.tv_dashboard_week)
    TextView mTimeSpanWeek;
    @ViewById(R.id.tv_dashboard_month)
    TextView mTimeSpanMonth;

    @ViewById(R.id.rv_dashboard_card_list)
    RecyclerView mCardList;
    private BaseArrayAdapter<Object> mAdapter;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        initView();
        initAdapter();
        mPresenter.loadConfig();
        mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_TODAY);
    }

    private void initView() {
        updateRadioStateTo(DashboardContract.TIME_SPAN_TODAY);
    }

    private void initAdapter() {
        DataCardType dataCardType = new DataCardType();
        BarChartCardType barChartCardType = new BarChartCardType();
        PieChartCardType pieChartCardType = new PieChartCardType();

        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(DataCard.class, dataCardType);
        mAdapter.register(BarChartCard.class, barChartCardType);
        mAdapter.register(PieChartCard.class, pieChartCardType);

        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemType(position).getSpanSize();
            }
        });
        mCardList.setLayoutManager(layout);
        mCardList.addItemDecoration(new ItemSpaceDecoration());
        mCardList.setAdapter(mAdapter);
    }

    @Override
    @UiThread
    public void updateInfo(DashboardConfig config) {
        mCompanyName.setText(config.companyName);
        mStoreName.setText(config.storeName);
    }

    @Override
    public void updateData(List<Object> data) {
        if (mAdapter != null) {
            mAdapter.setData(data);
        }
    }

    @Click(R.id.tv_dashboard_today)
    void switchToToday() {
        mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_TODAY);
        updateRadioStateTo(DashboardContract.TIME_SPAN_TODAY);
    }

    @Click(R.id.tv_dashboard_week)
    void switchToWeek() {
        mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_WEEK);
        updateRadioStateTo(DashboardContract.TIME_SPAN_WEEK);
    }

    @Click(R.id.tv_dashboard_month)
    void switchToMonth() {
        mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_MONTH);
        updateRadioStateTo(DashboardContract.TIME_SPAN_MONTH);
    }

    private void updateRadioStateTo(int timeSpan) {
        if (timeSpan == DashboardContract.TIME_SPAN_TODAY) {
            mTimeSpanToday.setSelected(true);
            mTimeSpanWeek.setSelected(false);
            mTimeSpanMonth.setSelected(false);
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            mTimeSpanToday.setSelected(false);
            mTimeSpanWeek.setSelected(true);
            mTimeSpanMonth.setSelected(false);
        } else {
            mTimeSpanToday.setSelected(false);
            mTimeSpanWeek.setSelected(false);
            mTimeSpanMonth.setSelected(true);
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.storeNameChanged};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationConstant.storeNameChanged) {
            // TODO: Update store name.
        }
    }

    private class ItemSpaceDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (getContext() == null) {
                super.getItemOffsets(outRect, view, parent, state);
                return;
            }
            int space = CommonHelper.dp2px(getContext(), 10.0f);
            int position = parent.getChildAdapterPosition(view);
            int spanSize = mAdapter.getItemType(position).getSpanSize();
            if (spanSize == 2) {
                outRect.left = space;
                outRect.right = space;
            } else {
                int posPoint = position - 1;
                boolean isFirst = true;
                while (posPoint >= 0) {
                    if (mAdapter.getItemType(posPoint).getSpanSize() == 1) {
                        isFirst = !isFirst;
                    } else {
                        break;
                    }
                    posPoint--;
                }
                if (isFirst) {
                    outRect.left = space;
                    outRect.right = space / 2;
                } else {
                    outRect.left = space / 2;
                    outRect.right = space;
                }
            }
            outRect.top = space;
            outRect.bottom = position == mAdapter.getData().size() - 1 ? space : 0;
        }
    }

}
