package com.sunmi.assistant.dashboard.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.ui.refresh.RefreshLayout;
import com.sunmi.assistant.dashboard.ui.refresh.RefreshViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-10-11
 */
@EFragment(R.layout.dashboard_fragment_list)
public class CustomerFragment extends BaseMvpFragment<CustomerPresenter>
        implements CustomerContract.View, RefreshLayout.RefreshLayoutDelegate {

    @ViewById(R.id.layout_dashboard_refresh)
    RefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_dashboard_list)
    RecyclerView mCardList;

    private DashboardContract.View mParent;

    private BaseArrayAdapter<Object> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemStickyListener mStickyListener;
    private RefreshViewHolder mRefreshHeaderHolder;

    @AfterViews
    void init() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Fragment parent = getParentFragment();
        if (!(parent instanceof DashboardContract.View)) {
            LogCat.e(TAG, "Parent is not DashboardFragment. CustomerFragment must be used in dashboard.");
            return;
        }
        mParent = (DashboardContract.View) parent;
        mPresenter = new CustomerPresenter(mParent.getPresenter());
        mPresenter.attachView(this);
        initRefreshLayout(context);
        initRecycler(context);
        mPresenter.load();
    }

    private void initRefreshLayout(Context context) {
        mRefreshHeaderHolder = new RefreshViewHolder(getContext(), false);
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
        mPresenter.refresh(true);
        layout.postDelayed(layout::endRefreshing, 500);
    }

    @Override
    public boolean onRefreshLayoutBeginLoadingMore(RefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void removeFrequencyCard() {
        List<Object> data = mAdapter.getData();
        int from = data.size() - 2;
        data.remove(from);
        data.remove(from);
        mAdapter.notifyItemRangeRemoved(from, 2);
    }

    @Override
    public void addFrequencyCard(List<BaseRefreshCard> data) {
        if (mAdapter == null || data == null || data.isEmpty()) {
            return;
        }
        List<Object> list = new ArrayList<>(data.size());
        int position = mAdapter.getItemCount();
        for (BaseRefreshCard card : data) {
            card.registerIntoAdapter(mAdapter, position);
            list.add(card.getModel());
            position++;
        }
        mAdapter.add(list);
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
