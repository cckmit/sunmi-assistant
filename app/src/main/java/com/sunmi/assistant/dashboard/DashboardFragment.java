package com.sunmi.assistant.dashboard;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.DropdownMenu;
import sunmi.common.view.NestedCustomScrollView;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.cl_dashboard_content)
    ConstraintLayout mContent;
    @ViewById(R.id.sv_outer_scroll)
    NestedCustomScrollView mScrollView;
    @ViewById(R.id.cl_container)
    ConstraintLayout mContainer;
    @ViewById(R.id.layout_dashboard_refresh)
    BGARefreshLayout mRefreshLayout;

    @ViewById(R.id.layout_shop_title)
    DropdownMenu mShopMenu;
    @ViewById(R.id.rv_dashboard_list)
    RecyclerView mCardList;
    @ViewById(R.id.view_top_mask)
    View mTopMask;

    @ViewsById({R.id.tv_dashboard_today, R.id.tv_dashboard_top_today})
    List<TextView> mTodayViews;
    @ViewsById({R.id.tv_dashboard_week, R.id.tv_dashboard_top_week})
    List<TextView> mWeekViews;
    @ViewsById({R.id.tv_dashboard_month, R.id.tv_dashboard_top_month})
    List<TextView> mMonthViews;


    private BaseArrayAdapter<Object> mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int mStatusBarHeight;
    private int mNavBarHeight;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        initView();
        mPresenter.init();
    }

    private void initView() {
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isDestroyed()) {
            return;
        }

        StatusBarUtils.setStatusBarFullTransparent(activity);
        mStatusBarHeight = Utils.getStatusBarHeight(activity);
        mNavBarHeight = 0;
        initScrollView();
        initRefreshLayout();
        initRecycler();
    }

    private void initScrollView() {
//        mScrollView.setNestedPriority(true, true);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(getContext(), false);
        View refreshHeaderView = refreshViewHolder.getRefreshHeaderView();
        refreshHeaderView.setPadding(0, mStatusBarHeight, 0, 0);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.setPullDownRefreshEnable(true);
        mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void initRecycler() {
        RecyclerView.ItemAnimator animator = mCardList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new BaseArrayAdapter<>();
        mLayoutManager = new LinearLayoutManager(getContext());
        mCardList.setLayoutManager(mLayoutManager);
        mCardList.addOnScrollListener(new ItemStickyListener());
        mCardList.setAdapter(mAdapter);
    }

    private void layoutRefreshLayout() {
        ViewGroup.LayoutParams lp = mRefreshLayout.getLayoutParams();
        lp.width = mContent.getMeasuredWidth();
        lp.height = mContent.getMeasuredHeight();
        mRefreshLayout.requestLayout();
        mScrollView.scrollTo(0, 0);
    }

    @Click({R.id.tv_dashboard_today, R.id.tv_dashboard_top_today})
    void clickPeriodToday() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    @Click({R.id.tv_dashboard_week, R.id.tv_dashboard_top_week})
    void clickPeriodWeek() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_WEEK);
    }

    @Click({R.id.tv_dashboard_month, R.id.tv_dashboard_top_month})
    void clickPeriodMonth() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_MONTH);
    }

    @Override
    public void updateTab(int period) {
        for (TextView v : mTodayViews) {
            v.setSelected(period == Constants.TIME_PERIOD_TODAY);
        }
        for (TextView v : mWeekViews) {
            v.setSelected(period == Constants.TIME_PERIOD_WEEK);
        }
        for (TextView v : mMonthViews) {
            v.setSelected(period == Constants.TIME_PERIOD_MONTH);
        }
    }

    @UiThread
    @Override
    public void initData(List<BaseRefreshItem> data) {
        if (mAdapter == null && data == null) {
            return;
        }
        layoutRefreshLayout();
        List<Object> list = new ArrayList<>(data.size());
        for (int i = 0, size = data.size(); i < size; i++) {
            BaseRefreshItem item = data.get(i);
            item.registerIntoAdapter(mAdapter, i);
            list.add(item.getModel());
        }
        mAdapter.setData(list);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.refresh();
        refreshLayout.postDelayed(refreshLayout::endRefreshing, 500);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{
                CommonNotifications.shopSwitched,
                CommonNotifications.shopNameChanged,
                CommonNotifications.companyNameChanged,
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.shopSwitched) {
            mPresenter.switchShopTo(SpUtils.getShopId());
        } else if (id == CommonNotifications.shopNameChanged
                || id == CommonNotifications.companyNameChanged) {
            mPresenter.refresh(0);
        }
    }

    private class ItemStickyListener extends RecyclerView.OnScrollListener {

        private View topBar = null;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//            int position = -1;
//            if (topBar == null) {
//                topBar = recyclerView.getChildAt(1);
//            }
//            if (topBar != null) {
//                int[] coordinate = new int[2];
//                topBar.getLocationInWindow(coordinate);
//                position = coordinate[1];
//            }
//            if (position < mStatusBarHeight - mStatusGap) {
//                mStickyTab.setVisibility(View.VISIBLE);
//            } else {
//                mStickyTab.setVisibility(View.INVISIBLE);
//            }
        }
    }

}
