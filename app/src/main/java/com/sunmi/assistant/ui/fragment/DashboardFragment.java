package com.sunmi.assistant.ui.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DashboardPresenter;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.ListCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.dashboard.model.Tab;
import com.sunmi.assistant.dashboard.model.Title;
import com.sunmi.assistant.dashboard.type.BarChartCardType;
import com.sunmi.assistant.dashboard.type.DataCardType;
import com.sunmi.assistant.dashboard.type.ListCardType;
import com.sunmi.assistant.dashboard.type.PieChartCardType;
import com.sunmi.assistant.dashboard.type.TabType;
import com.sunmi.assistant.dashboard.type.TitleType;

import org.androidannotations.annotations.AfterViews;
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
        RecyclerView.ItemAnimator animator = mCardList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        updateRadioStateTo(DashboardContract.TIME_SPAN_TODAY);
    }

    private void initAdapter() {
        TitleType titleType = new TitleType();
        TabType tabType = new TabType();
        tabType.addOnViewClickListener(R.id.tv_dashboard_today,
                (adapter, holder, v, model, position) -> {
                    model.timeSpan = DashboardContract.TIME_SPAN_TODAY;
                    adapter.notifyItemChanged(position);
                    mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_TODAY);
                });
        tabType.addOnViewClickListener(R.id.tv_dashboard_week,
                (adapter, holder, v, model, position) -> {
                    model.timeSpan = DashboardContract.TIME_SPAN_WEEK;
                    adapter.notifyItemChanged(position);
                    mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_WEEK);
                });
        tabType.addOnViewClickListener(R.id.tv_dashboard_month,
                (adapter, holder, v, model, position) -> {
                    model.timeSpan = DashboardContract.TIME_SPAN_TODAY;
                    adapter.notifyItemChanged(position);
                    mPresenter.timeSpanSwitchTo(DashboardContract.TIME_SPAN_MONTH);
                });
        DataCardType dataCardType = new DataCardType();
        BarChartCardType barChartCardType = new BarChartCardType();
        barChartCardType.addOnViewClickListener(R.id.tv_dashboard_radio_by_sales,
                (adapter, holder, v, model, position) -> {
                    model.dataSource = DashboardContract.DATA_MODE_SALES;
                    adapter.notifyItemChanged(position);
                });
        barChartCardType.addOnViewClickListener(R.id.tv_dashboard_radio_by_order,
                (adapter, holder, v, model, position) -> {
                    model.dataSource = DashboardContract.DATA_MODE_ORDER;
                    adapter.notifyItemChanged(position);
                });
        PieChartCardType pieChartCardType = new PieChartCardType();
        pieChartCardType.addOnViewClickListener(R.id.tv_dashboard_radio_by_sales,
                (adapter, holder, v, model, position) -> {
                    model.dataSource = DashboardContract.DATA_MODE_SALES;
                    adapter.notifyItemChanged(position);
                });
        pieChartCardType.addOnViewClickListener(R.id.tv_dashboard_radio_by_order,
                (adapter, holder, v, model, position) -> {
                    model.dataSource = DashboardContract.DATA_MODE_ORDER;
                    adapter.notifyItemChanged(position);
                });
        ListCardType listCardType = new ListCardType();

        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(Title.class, titleType);
        mAdapter.register(Tab.class, tabType);
        mAdapter.register(DataCard.class, dataCardType);
        mAdapter.register(BarChartCard.class, barChartCardType);
        mAdapter.register(PieChartCard.class, pieChartCardType);
        mAdapter.register(ListCard.class, listCardType);

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
    public void updateTitle() {
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(0);
        }
    }

    @UiThread
    @Override
    public void updateData(List<?> data) {
        if (mAdapter != null) {
            mAdapter.setData(data);
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
            int position = parent.getChildAdapterPosition(view);
            if (getContext() == null || position < 2) {
                super.getItemOffsets(outRect, view, parent, state);
                return;
            }
            int space = CommonHelper.dp2px(getContext(), 10.0f);
            int spanSize = mAdapter.getItemType(position).getSpanSize();
            if (spanSize == 2) {
                outRect.left = space;
                outRect.right = space;
            } else {
                int posPoint = position - 1;
                boolean isFirst = true;
                while (posPoint >= 2) {
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
