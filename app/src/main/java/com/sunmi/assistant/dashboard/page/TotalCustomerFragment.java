package com.sunmi.assistant.dashboard.page;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.datelibrary.bean.DateType;
import com.datelibrary.view.DatePicker;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.ui.refresh.RefreshLayout;
import com.sunmi.assistant.dashboard.ui.refresh.RefreshViewHolder;
import com.sunmi.assistant.dashboard.util.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.Interval;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.BottomDialog;

/**
 * @author yinhui
 * @date 2019-10-11
 */
@EFragment(R.layout.dashboard_fragment_list)
public class TotalCustomerFragment extends BaseMvpFragment<TotalCustomerPresenter>
        implements TotalCustomerContract.View, RefreshLayout.RefreshLayoutDelegate {

    @ViewById(R.id.layout_dashboard_refresh)
    RefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_dashboard_list)
    RecyclerView mCardList;

    private DashboardContract.View mParent;

    private BaseArrayAdapter<Object> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemStickyListener mStickyListener;
    private RefreshViewHolder mRefreshHeaderHolder;

    private Dialog mDialogTimeDay;
    private DatePicker dayPicker;
    private Dialog mDialogTimeWeek;
    private DatePicker weekPicker;
    private Dialog mDialogTimeMonth;
    private DatePicker monthPicker;

    @AfterViews
    void init() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Fragment parent = getParentFragment();
        if (!(parent instanceof DashboardContract.View)) {
            LogCat.e(TAG, "Parent is not DashboardFragment. RealtimeFragment must be used in dashboard.");
            return;
        }
        mParent = (DashboardContract.View) parent;
        mPresenter = new TotalCustomerPresenter(mParent.getPresenter());
        mPresenter.attachView(this);
        initRefreshLayout(context);
        initRecycler(context);
        mPresenter.init();
    }

    private void initRefreshLayout(Context context) {
        mRefreshHeaderHolder = new RefreshViewHolder(getContext(), false);
        mRefreshHeaderHolder.setRefreshingText(getString(R.string.str_refresh_loading));
        mRefreshHeaderHolder.setPullDownRefreshText(getString(R.string.str_refresh_pull));
        mRefreshHeaderHolder.setReleaseRefreshText(getString(R.string.str_refresh_release));
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(mRefreshHeaderHolder, mParent.getHeaderHeight());
        mRefreshLayout.setPullDownRefreshEnable(true);
        mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void initRecycler(Context context) {
        RecyclerView.ItemAnimator animator = mCardList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new BaseArrayAdapter<>();
        mLayoutManager = new LinearLayoutManager(getContext());
        mCardList.setLayoutManager(mLayoutManager);
        mStickyListener = new ItemStickyListener();
        mCardList.addOnScrollListener(mStickyListener);
        mCardList.setAdapter(mAdapter);
    }

    @Override
    public int getPerspective() {
        return CommonConstants.PERSPECTIVE_TOTAL;
    }

    @Override
    public void updateTab(int period) {
        mParent.updateTab(mPresenter.getType(), period);
    }

    @Override
    public void setCards(List<BaseRefreshCard> data) {
        mStickyListener.reset();
        if (mAdapter == null || data == null || data.isEmpty()) {
            return;
        }
        data.get(0).getModel().setMargin(0, mParent.getHeaderHeight(), 0, 0);
        List<Object> list = new ArrayList<>(data.size());
        int position = 0;
        for (BaseRefreshCard card : data) {
            card.registerIntoAdapter(mAdapter, position);
            list.add(card.getModel());
            position++;
        }
        mAdapter.setData(list);
        mCardList.scrollToPosition(0);
        mParent.resetTop();
    }

    @Override
    public void scrollToTop() {
        mCardList.smoothScrollToPosition(0);
    }

    @Override
    public void onRefreshLayoutBeginRefreshing(RefreshLayout layout) {
        mPresenter.pullToRefresh(true);
        layout.postDelayed(layout::endRefreshing, 500);
    }

    @Override
    public boolean onRefreshLayoutBeginLoadingMore(RefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void showTimeDialog(int period, long periodTime) {
        int margin = (int) getResources().getDimension(R.dimen.dp_20);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, margin, 0, margin);
        if (period == Constants.TIME_PERIOD_DAY) {
            if (mDialogTimeDay == null) {
                dayPicker = new DatePicker(getActivity(), DateType.TYPE_YMD);
                mDialogTimeDay = new BottomDialog.Builder(getActivity())
                        .setTitle(R.string.str_select_time)
                        .setCancelButton(R.string.sm_cancel)
                        .setOkButton(R.string.str_confirm, (dialog, which) -> {
                            long start = dayPicker.getSelectDate().getTime();
                            Interval interval = new Interval(start, start + 3600000 * 24);
                            mPresenter.setPeriod(mPresenter.getPeriod(), interval);
                        })
                        .setContent(dayPicker, layoutParams)
                        .create();
            }
            dayPicker.setStartDate(new Date(periodTime));
            dayPicker.init();
            mDialogTimeDay.show();
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            if (mDialogTimeWeek == null) {
                weekPicker = new DatePicker(getActivity(), DateType.TYPE_YW);
                mDialogTimeWeek = new BottomDialog.Builder(getActivity())
                        .setTitle(R.string.str_select_time)
                        .setCancelButton(R.string.sm_cancel)
                        .setOkButton(R.string.str_confirm, (dialog, which) -> {
                            long start = weekPicker.getSelectDate().getTime();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(start);
                            calendar.add(Calendar.DATE, 7);
                            Interval interval = new Interval(start, calendar.getTimeInMillis());
                            mPresenter.setPeriod(mPresenter.getPeriod(), interval);
                        })
                        .setContent(weekPicker, layoutParams)
                        .create();
            }
            weekPicker.setStartDate(new Date(periodTime));
            weekPicker.init();
            mDialogTimeWeek.show();
        } else if (period == Constants.TIME_PERIOD_MONTH) {
            if (mDialogTimeMonth == null) {
                monthPicker = new DatePicker(getActivity(), DateType.TYPE_YM);
                mDialogTimeMonth = new BottomDialog.Builder(getActivity())
                        .setTitle(R.string.str_select_time)
                        .setCancelButton(R.string.sm_cancel)
                        .setOkButton(R.string.str_confirm, (dialog, which) -> {
                            long start = monthPicker.getSelectDate().getTime();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(start);
                            calendar.add(Calendar.MONTH, 1);
                            Interval interval = new Interval(start, calendar.getTimeInMillis());
                            mPresenter.setPeriod(mPresenter.getPeriod(), interval);
                        })
                        .setContent(monthPicker, layoutParams)
                        .create();
            }
            monthPicker.setStartDate(new Date(periodTime));
            monthPicker.init();
            mDialogTimeMonth.show();
        }
    }

    private class ItemStickyListener extends RecyclerView.OnScrollListener {

        private View topBar = null;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getChildCount() == 0) {
                return;
            }
            int position = -1;
            if (topBar == null) {
                topBar = recyclerView.getChildAt(0);
            }
            if (topBar != null) {
                int[] coordinate = new int[2];
                topBar.getLocationInWindow(coordinate);
                position = coordinate[1];
            }
            Context context = recyclerView.getContext();
            mParent.updateTopPosition(position);
        }

        private void reset() {
            topBar = null;
        }

    }
}
