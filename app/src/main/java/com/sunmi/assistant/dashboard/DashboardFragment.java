package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshItem;
import com.sunmi.assistant.dashboard.ui.RefreshLayout;
import com.sunmi.assistant.dashboard.ui.RefreshViewHolder;
import com.sunmi.ipc.config.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.DropdownMenu;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View, RefreshLayout.RefreshLayoutDelegate {

    private static final int OFFSET_PARALLAX = 50;

    @ViewById(R.id.cl_dashboard_content)
    ConstraintLayout mContent;
    @ViewById(R.id.layout_dashboard_refresh)
    RefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_dashboard_list)
    RecyclerView mCardList;

    @ViewById(R.id.layout_shop_title)
    DropdownMenu mShopMenu;
    private LinearLayout mShopMenuList;
    @ViewById(R.id.view_dashboard_overlay)
    View mOverlay;

    @ViewById(R.id.layout_top_period_tab)
    ViewGroup mTopPeriodTab;
    @ViewById(R.id.tv_dashboard_top_today)
    TextView mTodayView;
    @ViewById(R.id.tv_dashboard_top_week)
    TextView mWeekView;
    @ViewById(R.id.tv_dashboard_top_month)
    TextView mMonthView;

    @ViewById(R.id.group_dashboard_content)
    Group mContentGroup;
    @ViewById(R.id.layout_dashboard_error)
    View mLayoutError;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int mDataSource;
    private BaseArrayAdapter<Object> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RefreshViewHolder mRefreshHeaderHolder;

    private ShopMenuAdapter mShopMenuAdapter;
    private TextView mShopMenuTitle;
    private ImageView mShopMenuTitleArrow;
    private ShopItem mShopMenuItem;

    private int mStatusBarHeight;
    private int mTopBarHeight;
    private int mTopShopMenuHeight;
    private ShopMenuPopupHelper mShopMenuPopupHelper;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        mPresenter.init();
        showLoading();
        mHandler.post(this::initView);
    }

    private void initView() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        initDimens(context);
        initTopBar(context);
        initRefreshLayout(context);
        initRecycler(context);
    }

    private void initDimens(Context context) {
        mStatusBarHeight = Utils.getStatusBarHeight(context);
        mTopBarHeight = (int) context.getResources().getDimension(R.dimen.dp_44) + mStatusBarHeight;
        mTopShopMenuHeight = (int) context.getResources().getDimension(R.dimen.dp_64) + mStatusBarHeight;
    }

    private void initTopBar(Context context) {
        // 初始化设置状态栏
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            StatusBarUtils.setStatusBarFullTransparent(activity);
        }

        // 初始化设置TopBar高度和Padding
        mTopPeriodTab.getLayoutParams().height = mTopBarHeight;
        mTopPeriodTab.setPadding(0, mStatusBarHeight, 0, 0);
        mShopMenu.getLayoutParams().height = mTopShopMenuHeight;
        mShopMenu.setPadding(0, mStatusBarHeight, 0, 0);

        // 初始化设置顶部门店选择下拉列表
        mShopMenuPopupHelper = new ShopMenuPopupHelper(context, mContent, mOverlay);
        mShopMenu.setLayoutManager(new ShopMenuLayoutManager(context));
        mShopMenu.setPopupHelper(mShopMenuPopupHelper);
        mShopMenuAdapter = new ShopMenuAdapter(context);
        mShopMenuAdapter.setOnItemClickListener((adapter, model, position) -> {
            boolean changed = mPresenter.switchShopTo(model);
            if (changed) {
                showLoadingDialog();
                List<ShopItem> shops = adapter.getData();
                shops.remove(position);
                shops.add(0, model);
                adapter.notifyDataSetChanged();
                resetTopView();
            }
        });
        mShopMenu.setAdapter(mShopMenuAdapter);
        mShopMenuTitle = mShopMenuAdapter.getTitle().getView(R.id.dropdown_item_title);
        mShopMenuTitleArrow = mShopMenuAdapter.getTitle().getView(R.id.dropdown_item_arrow);
        mOverlay.setOnClickListener(v -> mShopMenu.getPopup().dismiss(true));
    }

    private void initRefreshLayout(Context context) {
        mRefreshHeaderHolder = new RefreshViewHolder(getContext(), false);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(mRefreshHeaderHolder, mTopShopMenuHeight);
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
        mCardList.addOnScrollListener(new ItemStickyListener());
        mCardList.setAdapter(mAdapter);
    }

    private void showLoading() {
        mContentGroup.setVisibility(View.INVISIBLE);
        showLoadingDialog();
    }

    private void resetTopView() {
        mCardList.addOnScrollListener(new ItemStickyListener());
        StatusBarUtils.setStatusBarFullTransparent(getActivity());
        mShopMenu.setVisibility(View.VISIBLE);
        mShopMenu.setBackgroundResource(R.drawable.dashboard_bg_top);
        mShopMenu.setTranslationY(0);
        mShopMenuTitle.setTextColor(0xFFFFFFFF);
        mShopMenuTitleArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
        mTopPeriodTab.setVisibility(View.INVISIBLE);
        mCardList.scrollToPosition(0);
    }

    @Click(R.id.tv_dashboard_top_today)
    void clickPeriodToday() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    @Click(R.id.tv_dashboard_top_week)
    void clickPeriodWeek() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_WEEK);
    }

    @Click(R.id.tv_dashboard_top_month)
    void clickPeriodMonth() {
        mPresenter.switchPeriodTo(Constants.TIME_PERIOD_MONTH);
    }

    @Click(R.id.btn_refresh)
    void clickReload() {
        showLoadingDialog();
        mPresenter.init();
    }

    @Override
    public void updateTab(int period) {
        mTodayView.setSelected(period == Constants.TIME_PERIOD_TODAY);
        mTodayView.setTypeface(null, period == Constants.TIME_PERIOD_TODAY ? Typeface.BOLD : Typeface.NORMAL);
        mWeekView.setSelected(period == Constants.TIME_PERIOD_WEEK);
        mWeekView.setTypeface(null, period == Constants.TIME_PERIOD_WEEK ? Typeface.BOLD : Typeface.NORMAL);
        mMonthView.setSelected(period == Constants.TIME_PERIOD_MONTH);
        mMonthView.setTypeface(null, period == Constants.TIME_PERIOD_MONTH ? Typeface.BOLD : Typeface.NORMAL);
    }

    @Override
    public void setShopList(List<ShopItem> list) {
        for (ShopItem item : list) {
            if (item.isChecked()) {
                mShopMenuItem = item;
                break;
            }
        }
        mShopMenuAdapter.setData(list);
    }

    @UiThread
    @Override
    public void setCards(List<BaseRefreshItem> data, int dataSource) {
        mContentGroup.setVisibility(View.VISIBLE);
        mLayoutError.setVisibility(View.GONE);
        mDataSource = dataSource;
        if (mAdapter == null || data == null || data.isEmpty()) {
            return;
        }
        data.get(0).setMargin(0, mTopShopMenuHeight, 0, 0);
        List<Object> list = new ArrayList<>(data.size());
        for (int i = 0, size = data.size(); i < size; i++) {
            BaseRefreshItem item = data.get(i);
            item.registerIntoAdapter(mAdapter, i);
            list.add(item.getModel());
        }
        mAdapter.setData(list);
        resetTopView();
        hideLoadingDialog();
    }

    @Override
    public void loadDataFailed() {
        hideLoadingDialog();
        mContentGroup.setVisibility(View.GONE);
        mLayoutError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefreshLayoutBeginRefreshing(RefreshLayout refreshLayout) {
        mPresenter.refresh(true);
        refreshLayout.postDelayed(refreshLayout::endRefreshing, 500);
    }

    @Override
    public boolean onRefreshLayoutBeginLoadingMore(RefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{
                CommonNotifications.companySwitch,
                CommonNotifications.companyNameChanged,
                CommonNotifications.shopSwitched,
                CommonNotifications.shopNameChanged,
                CommonNotifications.importShop,
                CommonNotifications.shopCreate,
                IpcConstants.refreshIpcList
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.companySwitch
                || id == CommonNotifications.companyNameChanged) {
            mShopMenuPopupHelper.setCompanyName(SpUtils.getCompanyName());
            mPresenter.init();
        } else if (id == CommonNotifications.shopSwitched
                || id == CommonNotifications.shopNameChanged
                || id == CommonNotifications.importShop
                || id == CommonNotifications.shopCreate) {
            mPresenter.init();
        } else if (id == IpcConstants.refreshIpcList) {
            mPresenter.reload();
        }
    }

    private void showStickyTop(Context context) {
        StatusBarUtils.setStatusBarColor(getActivity(), StatusBarUtils.TYPE_DARK);
        if (mDataSource != 0) {
            mShopMenu.setTranslationY(-mTopShopMenuHeight);
            mShopMenu.setVisibility(View.INVISIBLE);
            mTopPeriodTab.setVisibility(View.VISIBLE);
        } else {
            mShopMenuTitle.setTextColor(ContextCompat.getColor(context, R.color.color_303540));
            mShopMenuTitleArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            mShopMenu.setBackgroundResource(R.drawable.dashboard_bg_white_with_divider);
        }
    }

    private void hideStickyTop(Context context) {
        StatusBarUtils.setStatusBarFullTransparent(getActivity());
        if (mDataSource != 0) {
            mShopMenu.setVisibility(View.VISIBLE);
            mTopPeriodTab.setVisibility(View.INVISIBLE);
        } else {
            mShopMenuTitle.setTextColor(0xFFFFFFFF);
            mShopMenuTitleArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
            mShopMenu.setBackgroundResource(R.drawable.dashboard_bg_top);
        }
    }

    private class ItemStickyListener extends RecyclerView.OnScrollListener {

        private boolean mIsStickyTop = false;
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
            LogCat.d(TAG, "onScroll=" + position + "; topHeight=" + mTopShopMenuHeight);
            Context context = recyclerView.getContext();
            if (mIsStickyTop && !showSticky(position)) {
                mIsStickyTop = false;
                hideStickyTop(context);
            } else if (!mIsStickyTop && showSticky(position)) {
                mIsStickyTop = true;
                showStickyTop(context);
            }
            if (!showSticky(position) && mDataSource != 0) {
                int offset = Math.min(position - mTopShopMenuHeight, 0);
                mShopMenu.setTranslationY(offset);
                mShopMenuPopupHelper.setOffset(offset);
            }
        }

        private boolean showSticky(int position) {
            return position <= 0;
        }
    }

    private static class ShopMenuLayoutManager extends LinearLayoutManager {

        private ShopMenuLayoutManager(Context context) {
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
                    getChildCount() > 8 ? (int) (itemHeight * 8.5f) : itemHeight * getChildCount());
        }
    }
}
