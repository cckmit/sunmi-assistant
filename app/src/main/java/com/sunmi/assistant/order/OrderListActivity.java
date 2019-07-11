package com.sunmi.assistant.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.order.model.FilterItem;
import com.sunmi.assistant.order.model.OrderInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.view.DropdownMenu;

@SuppressLint("Registered")
@EActivity(R.layout.order_activity_list)
public class OrderListActivity extends BaseMvpActivity<OrderListPresenter>
        implements OrderListContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.order_list_content)
    ConstraintLayout mContent;

    @ViewById(R.id.order_list_empty)
    TextView mOrderListEmpty;
    @ViewById(R.id.tv_order_list_no_network)
    TextView mNetworkError;
    @ViewById(R.id.btn_order_list_no_network_refresh)
    Button mNetworkRefresh;

    @ViewById(R.id.order_line_decoration)
    View mLineDecoration;
    @ViewById(R.id.order_list_overlay)
    View mOverlay;

    @ViewById(R.id.order_list_refresh)
    BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.order_list)
    RecyclerView mOrderList;
    BaseArrayAdapter<Object> mOrderListAdapter;

    @Extra
    long mTimeStart;
    @Extra
    long mTimeEnd;
    @Extra
    int mInitOrderType;
    boolean isEmptyShow = false;

    private DropdownAnimation mDropdownAnimator = new DropdownAnimation();
    private List<DropdownMenu> mFilters = new ArrayList<>(3);
    private List<DropdownAdapter> mFilterAdapters = new ArrayList<>(3);
    private int mCurrentShowFilter = -1;

    @AfterViews
    void init() {
        initViews();
        mPresenter = new OrderListPresenter();
        mPresenter.attachView(this);
        if (!NetworkUtils.isNetworkAvailable(this)) {
            shortTip(R.string.toast_networkIsExceptional);
            setSwitchContentVisible(false);
        } else {
            setSwitchContentVisible(true);
            mPresenter.loadList(mTimeStart, mTimeEnd, mInitOrderType);
        }
    }

    private void initViews() {
        mNetworkRefresh.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(OrderListActivity.this)) {
                shortTip(R.string.toast_networkIsExceptional);
                setSwitchContentVisible(false);
            } else {
                setSwitchContentVisible(true);
                mPresenter.loadList(mTimeStart, mTimeEnd, mInitOrderType);
            }
        });

        mFilters.add(findViewById(R.id.order_filter_sort));
        mFilters.add(findViewById(R.id.order_filter_pay_type));
        mFilters.add(findViewById(R.id.order_filter_order_type));

        for (int i = 0; i < 3; i++) {
            mFilters.get(i).setTag(i);
        }

        mOverlay.setOnClickListener(v -> {
            if (mCurrentShowFilter != -1) {
                mFilters.get(mCurrentShowFilter).getPopup().dismiss(true);
            }
        });

        CustomPopupHelper helper = new CustomPopupHelper();

        createFilterDropdownMenu(helper);
        createFilterDropdownMenu(helper);
        createFilterDropdownMenu(helper);

        mOrderListAdapter = new BaseArrayAdapter<>();
        OrderListItemType orderItem = new OrderListItemType();
        orderItem.setOnItemClickListener((adapter, holder, model, position) ->
                OrderDetailActivity_.intent(this).mOrderInfo(model).start());
        mOrderListAdapter.register(OrderInfo.class, orderItem);
        mOrderListAdapter.register(Object.class, new OrderListEmptyType());
        mOrderList.setLayoutManager(new LinearLayoutManager(this));
        mOrderList.setAdapter(mOrderListAdapter);
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.setPullDownRefreshEnable(false);
        mRefreshLayout.setIsShowLoadingMoreView(true);
    }

    private void createFilterDropdownMenu(CustomPopupHelper helper) {
        int index = mFilterAdapters.size();
        DropdownMenu menu = mFilters.get(index);
        DropdownAdapter adapter = new DropdownAdapter(this);
        menu.setLayoutManager(new FilterMenuLayoutManager(this));
        menu.setPopupHelper(helper);
        menu.setAdapter(adapter);
        mFilterAdapters.add(adapter);
    }

    private void setSwitchContentVisible(boolean visible) {
        mNetworkError.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        mNetworkRefresh.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        mLineDecoration.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mRefreshLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        for (DropdownMenu filter : mFilters) {
            filter.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateFilter(int filterIndex, List<FilterItem> list) {
        int selection = -1;
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).isChecked()) {
                selection = i;
            }
        }
        mFilterAdapters.get(filterIndex).setData(list, selection);
        mFilterAdapters.get(filterIndex).setOnItemClickListener(new OnFilterItemClickListener(filterIndex));
    }

    @Override
    public void setData(List<OrderInfo> list) {
        if (list == null) {
            mOrderListEmpty.setVisibility(View.GONE);
            isEmptyShow = true;
            list = new ArrayList<>();
        } else if (list.isEmpty()) {
            isEmptyShow = true;
            mOrderListEmpty.setVisibility(View.VISIBLE);
        } else {
            isEmptyShow = false;
            mOrderListEmpty.setVisibility(View.GONE);
        }
        mOrderListAdapter.setData(list);
    }

    @Override
    public void addData(List<OrderInfo> list) {
        mOrderListAdapter.add(list);
        if (mRefreshLayout != null) {
            mRefreshLayout.endLoadingMore();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentShowFilter != -1) {
            mFilters.get(mCurrentShowFilter).getPopup().dismiss(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            shortTip(R.string.toast_networkIsExceptional);
            return false;
        }
        boolean hasMore = mPresenter.loadMore();
        if (!hasMore && !isEmptyShow) {
            mOrderListAdapter.add(new Object());
            isEmptyShow = true;
        }
        return hasMore;
    }

    private class OnFilterItemClickListener implements DropdownMenu.OnItemClickListener<FilterItem> {

        private int mFilterIndex;

        private OnFilterItemClickListener(int index) {
            this.mFilterIndex = index;
        }

        @Override
        public void onItemSelected(DropdownMenu.BaseAdapter<FilterItem> adapter, FilterItem model, int position) {
            mPresenter.setFilterCurrent(mFilterIndex, model);
            adapter.notifyDataSetChanged();
        }
    }

    private class CustomPopupHelper implements DropdownMenu.PopupHelper {

        @Override
        public void initMenu(RecyclerView list) {
            if (list.getAdapter() == null || list.getAdapter().getItemCount() == 0) {
                return;
            }
            // Add view into ConstraintLayout.
            int index = mContent.indexOfChild(mOverlay) + 1;
            if (mContent.indexOfChild(list) == -1) {
                mContent.addView(list, index);
            }
            // Init constraint set of menu list in ConstraintLayout.
            ConstraintSet con = new ConstraintSet();
            con.clone(mContent);
            con.connect(list.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            con.connect(list.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            con.connect(list.getId(), ConstraintSet.TOP, R.id.order_line_decoration, ConstraintSet.BOTTOM, 0);
            con.constrainHeight(list.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.constrainWidth(list.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.applyTo(mContent);
            list.measure(0, 0);
        }

        @Override
        public void show(RecyclerView list, boolean animated) {
            if (mCurrentShowFilter != -1) {
                mFilters.get(mCurrentShowFilter).getPopup().dismiss(false);
            }
            mCurrentShowFilter = (int) list.getTag();
            mDropdownAnimator.startAnimationToShow(animated, list, mOverlay);
        }

        @Override
        public void dismiss(RecyclerView list, boolean animated) {
            mCurrentShowFilter = -1;
            mDropdownAnimator.startAnimationToDismiss(animated, list, mOverlay);
        }
    }

    private static class FilterMenuLayoutManager extends LinearLayoutManager {

        private FilterMenuLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
            if (getChildCount() == 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }
            View firstChildView = recycler.getViewForPosition(0);
            measureChild(firstChildView, widthSpec, heightSpec);
            int itemHeight = firstChildView.getMeasuredHeight();
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec),
                    getChildCount() > 9 ? itemHeight * 9 : itemHeight * getChildCount());
        }
    }

}
