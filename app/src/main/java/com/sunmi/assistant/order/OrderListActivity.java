package com.sunmi.assistant.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.view.DropdownMenu;

@SuppressLint("Registered")
@EActivity(R.layout.order_activity_list)
public class OrderListActivity extends BaseMvpActivity<OrderListPresenter>
        implements OrderListContract.View {

    @ViewById(R.id.order_list_content)
    ConstraintLayout mContent;

    @ViewById(R.id.order_list_empty)
    TextView mOrderListEmpty;

    @ViewById(R.id.order_list_overlay)
    View mOverlay;

    @ViewById(R.id.order_list)
    RecyclerView mOrderList;
    BaseArrayAdapter<OrderInfo> mOrderListAdapter;

    @Extra
    long mTimeStart;
    @Extra
    long mTimeEnd;
    @Extra
    int mInitOrderType;

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
            return;
        }
        mPresenter.loadList(mTimeStart, mTimeEnd, mInitOrderType);
    }

    private void initViews() {
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
        mOrderListAdapter.register(orderItem);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));
        mOrderList.setAdapter(mOrderListAdapter);
        mOrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mLastPosition;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (visibleItemCount > 0 && mLastPosition >= totalItemCount - 1) {
                        mPresenter.loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (!(layoutManager instanceof LinearLayoutManager)) {
                    return;
                }
                mLastPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
        });
    }

    private void createFilterDropdownMenu(CustomPopupHelper helper) {
        int index = mFilterAdapters.size();
        DropdownMenu menu = mFilters.get(index);
        DropdownAdapter adapter = new DropdownAdapter(this);
        adapter.setOnItemClickListener(new OnFilterItemClickListener(index));
        menu.setLayoutManager(new FilterMenuLayoutManager(this));
        menu.setPopupHelper(helper);
        menu.setAdapter(adapter);
        mFilterAdapters.add(adapter);
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
    }

    @Override
    public void setData(List<OrderInfo> list) {
        if (list == null) {
            mOrderListEmpty.setVisibility(View.GONE);
            list = new ArrayList<>();
        } else if (list.isEmpty()) {
            mOrderListEmpty.setVisibility(View.VISIBLE);
        } else {
            mOrderListEmpty.setVisibility(View.GONE);
        }
        mOrderListAdapter.setData(list);
    }

    @Override
    public void addData(List<OrderInfo> list) {
        mOrderListAdapter.add(list);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentShowFilter != -1) {
            mFilters.get(mCurrentShowFilter).getPopup().dismiss(true);
        } else {
            super.onBackPressed();
        }
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
        public void initMenu(View list) {
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
            con.constrainHeight(list.getId(), ConstraintSet.WRAP_CONTENT);
            con.constrainWidth(list.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.applyTo(mContent);
            list.measure(0, 0);
        }

        @Override
        public void show(View list, boolean animated) {
            if (mCurrentShowFilter != -1) {
                mFilters.get(mCurrentShowFilter).getPopup().dismiss(false);
            }
            mCurrentShowFilter = (int) list.getTag();
            mDropdownAnimator.startAnimationToShow(animated, list, mOverlay);
        }

        @Override
        public void dismiss(View list, boolean animated) {
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
            if (getChildCount() > 7) {
                View firstChildView = recycler.getViewForPosition(0);
                measureChild(firstChildView, widthSpec, heightSpec);
                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), firstChildView.getMeasuredHeight() * 7);
            } else {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }
        }
    }

}
