package com.sunmi.assistant.dashboard;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpFragment;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.Utils;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final int POSITION_TOP_TITLE_AND_BAR = 2;
    private static final int BIG_CARD_SPAN = 2;

    @ViewById(R.id.layout_dashboard_refresh)
    BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_dashboard_card_list)
    RecyclerView mCardList;
    @ViewById(R.id.layout_dashboard_sticky_tab)
    ViewGroup mStickyTab;
    @ViewById(R.id.tv_dashboard_today)
    TextView mTabToday;
    @ViewById(R.id.tv_dashboard_week)
    TextView mTabWeek;
    @ViewById(R.id.tv_dashboard_month)
    TextView mTabMonth;

    private BaseArrayAdapter<Object> mAdapter;
    private GridLayoutManager mLayoutManager;

    private int mStatusBarHeight;
    private int mStatusGap;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        initView();
        initAdapter();
        mPresenter.loadConfig();
        mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_TODAY);
    }

    private void initView() {
        mStatusBarHeight = Utils.getStatusBarHeight(mStickyTab.getContext());
        mStatusGap = (int) mStickyTab.getContext().getResources().getDimension(R.dimen.dp_4);
        int topPadding = mStatusBarHeight + (int) mStickyTab.getContext().getResources().getDimension(R.dimen.dp_8);
        mStickyTab.setPaddingRelative(0, topPadding, 0, 0);
        RecyclerView.ItemAnimator animator = mCardList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(getContext(), false);
        View refreshHeaderView = refreshViewHolder.getRefreshHeaderView();
        refreshHeaderView.setPadding(0, mStatusBarHeight, 0, 0);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.setPullDownRefreshEnable(true);
        mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void initAdapter() {
        mAdapter = new BaseArrayAdapter<>();
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemType(position).getSpanSize();
            }
        });
        mCardList.setLayoutManager(mLayoutManager);
        mCardList.addOnScrollListener(new ItemStickyListener());
        mCardList.addItemDecoration(new ItemSpaceDecoration());
        mCardList.setAdapter(mAdapter);
    }

    @Click(R.id.tv_dashboard_today)
    void clickPeriodToday() {
        mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_TODAY);
    }

    @Click(R.id.tv_dashboard_week)
    void clickPeriodWeek() {
        mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_WEEK);
    }

    @Click(R.id.tv_dashboard_month)
    void clickPeriodMonth() {
        mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_MONTH);
    }

    @Override
    public void updateStickyTab(int period) {
        mTabToday.setSelected(period == DashboardContract.TIME_PERIOD_TODAY);
        mTabWeek.setSelected(period == DashboardContract.TIME_PERIOD_WEEK);
        mTabMonth.setSelected(period == DashboardContract.TIME_PERIOD_MONTH);
    }

    @UiThread
    @Override
    public void initData(List<BaseRefreshCard> data) {
        if (mAdapter == null && data == null) {
            return;
        }
        List<Object> list = new ArrayList<>(data.size());
        for (int i = 0, size = data.size(); i < size; i++) {
            BaseRefreshCard item = data.get(i);
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
            int position = -1;
            if (topBar == null) {
                topBar = recyclerView.getChildAt(1);
            }
            if (topBar != null) {
                int[] coordinate = new int[2];
                topBar.getLocationInWindow(coordinate);
                position = coordinate[1];
            }
            if (position < mStatusBarHeight - mStatusGap) {
                mStickyTab.setVisibility(View.VISIBLE);
            } else {
                mStickyTab.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ItemSpaceDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (getContext() == null || position < POSITION_TOP_TITLE_AND_BAR) {
                super.getItemOffsets(outRect, view, parent, state);
                return;
            }
            int space = CommonHelper.dp2px(getContext(), 10.0f);
            int spanSize = mAdapter.getItemType(position).getSpanSize();
            if (spanSize == BIG_CARD_SPAN) {
                outRect.left = space;
                outRect.right = space;
            } else {
                int posPoint = position - 1;
                boolean isFirst = true;
                while (posPoint >= POSITION_TOP_TITLE_AND_BAR) {
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
