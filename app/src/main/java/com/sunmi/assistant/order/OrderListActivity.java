package com.sunmi.assistant.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.order.model.FilterItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.view.DropdownMenu;

@SuppressLint("Registered")
@EActivity(R.layout.order_list_activity_list)
public class OrderListActivity extends BaseMvpActivity<OrderListPresenter>
        implements OrderListContract.View {

    @ViewById(R.id.order_list_content)
    ConstraintLayout mContent;

    @ViewById(R.id.order_list_overlay)
    View mOverlay;

    @ViewById(R.id.order_list)
    RecyclerView mOrderList;
    BaseArrayAdapter<OrderListResp.OrderItem> mOrderListAdapter;

    @Extra
    long mTimeStart;
    @Extra
    long mTimeEnd;

    private DropdownAnimation mDropdownAnimator = new DropdownAnimation();
    private List<DropdownMenu> mFilters = new ArrayList<>(4);
    private List<DropdownAdapter> mFilterAdapters = new ArrayList<>(4);
    private int mCurrentShowFilter = -1;

    @AfterViews
    void init() {
        initViews();
        mPresenter = new OrderListPresenter();
        mPresenter.attachView(this);
        mPresenter.loadList(mTimeStart, mTimeEnd);
    }

    private void initViews() {
        mFilters.add(findViewById(R.id.order_filter_amount));
        mFilters.add(findViewById(R.id.order_filter_pay_type));
        mFilters.add(findViewById(R.id.order_filter_order_type));
        mFilters.add(findViewById(R.id.order_filter_time));

        for (int i = 0; i < 4; i++) {
            mFilters.get(i).setTag(i);
        }

        mOverlay.setOnClickListener(v -> {
            if (mCurrentShowFilter != -1) {
                mFilters.get(mCurrentShowFilter).getPopup().dismiss(true);
            }
        });

        CustomPopupHelper helper = new CustomPopupHelper();

        createFilterDropdownMenu(getString(R.string.order_amount_order), helper);
        createFilterDropdownMenu(getString(R.string.order_pay_type), helper);
        createFilterDropdownMenu(getString(R.string.order_transaction_type), helper);
        createFilterDropdownMenu(getString(R.string.order_time_order), helper);

        mOrderListAdapter = new BaseArrayAdapter<>();
        mOrderListAdapter.register(new OrderListItemType());
        mOrderList.setLayoutManager(new LinearLayoutManager(this));
        mOrderList.setAdapter(mOrderListAdapter);
    }

    private void createFilterDropdownMenu(String name, CustomPopupHelper helper) {
        int index = mFilterAdapters.size();
        DropdownMenu menu = mFilters.get(index);
        DropdownAdapter adapter = new DropdownAdapter(this);
        adapter.setInitData(new FilterItem(-1, name));
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
        mFilterAdapters.get(filterIndex).setData(list, -1);
    }

    @Override
    public void setData(List<OrderListResp.OrderItem> list) {
        mOrderListAdapter.setData(list);
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
            if (mContent.indexOfChild(list) == -1) {
                mContent.addView(list, 2);
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
            mDropdownAnimator.startAnimationToShow(false, list, mOverlay);
        }

        @Override
        public void dismiss(View list, boolean animated) {
            mCurrentShowFilter = -1;
            mDropdownAnimator.startAnimationToDismiss(false, list, mOverlay);
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
