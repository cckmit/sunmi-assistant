package com.sunmi.assistant.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.ui.ScrollableViewPager;
import com.sunmi.assistant.dashboard.ui.ShopMenuAdapter;
import com.sunmi.assistant.dashboard.ui.ShopMenuAnim;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.sunmiservice.cloud.WebViewCloudServiceActivity_;
import com.xiaojinzi.component.impl.Router;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.FilterItem;
import sunmi.common.router.IpcApi;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.tablayout.CommonTabLayout;
import sunmi.common.view.tablayout.listener.CustomTabEntity;
import sunmi.common.view.tablayout.listener.OnTabSelectListener;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View {

    @ViewById(R.id.cl_dashboard_content)
    ConstraintLayout mContent;

    @ViewById(R.id.view_dashboard_top_mask)
    View mBgTopWhiteMask;
    @ViewById(R.id.view_tab_divider)
    View mTabDivider;

    @ViewById(R.id.pager_dashboard_pager)
    ScrollableViewPager mPager;
    @ViewById(R.id.tab_dashboard_pager)
    CommonTabLayout mPageTab;

    @ViewById(R.id.layout_dashboard_tab)
    FrameLayout mTopPageTab;
    @ViewById(R.id.layout_shop_title)
    DropdownMenuNew mTopShopMenu;

    @ViewById(R.id.layout_top_period_tab)
    ViewGroup mTopStickyPeriodTab;
    @ViewById(R.id.tv_dashboard_top_today)
    TextView mTodayView;
    @ViewById(R.id.tv_dashboard_top_yesterday)
    TextView mYesterdayView;
    @ViewById(R.id.tv_dashboard_top_week)
    TextView mWeekView;
    @ViewById(R.id.tv_dashboard_top_month)
    TextView mMonthView;

    @ViewById(R.id.group_dashboard_content)
    Group mContentGroup;
    @ViewById(R.id.layout_dashboard_no_fs_tip)
    View mNoFsTip;
    @ViewById(R.id.layout_dashboard_error)
    View mLayoutError;

    @ViewById(R.id.ll_floating)
    LinearLayout llFloating;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private PageAdapter pageAdapter;
    private PageListener pageListener;
    private ShopMenuAdapter mShopMenuAdapter;
    private ShopMenuAnim mShopMenuAnim;

    private int mStatusBarHeight;
    private int mTopStickyPeriodHeight;
    private int mTopShopMenuHeight;
    private int mTopPageTabHeight;
    private int mTopRadiusHeight;
    private int mTopHeaderHeight;

    private int mPerspective;

    private boolean mHasInit = false;
    private boolean mHasData = false;
    private boolean mIsStickyPeriodTop = false;
    private boolean mIsStickyShopMenu = false;

    private int colorTop;
    private int colorOrange;
    private int colorWhite;
    private int colorWhite60a;
    private int colorTextMain;
    private int colorTextCaption;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        showLoadingDialog();
        initView();
        mPresenter.init();
    }

    private void initView() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        initDimens(context);
        initViewPager(context);
        initTopBar(context);
    }

    private void initDimens(Context context) {
        mStatusBarHeight = sunmi.common.utils.Utils.getStatusBarHeight(context);
        mTopStickyPeriodHeight = (int) context.getResources().getDimension(R.dimen.dp_44);
        mTopShopMenuHeight = (int) context.getResources().getDimension(R.dimen.dp_54);
        mTopPageTabHeight = (int) context.getResources().getDimension(R.dimen.dp_44);
        mTopRadiusHeight = (int) context.getResources().getDimension(R.dimen.dp_16);
        mTopHeaderHeight = mTopShopMenuHeight + mTopPageTabHeight + mStatusBarHeight;
        colorTop = ContextCompat.getColor(context, R.color.text_main);
        colorOrange = ContextCompat.getColor(context, R.color.common_orange);
        colorWhite = ContextCompat.getColor(context, R.color.c_white);
        colorWhite60a = ContextCompat.getColor(context, R.color.white_60a);
        colorTextMain = ContextCompat.getColor(context, R.color.text_main);
        colorTextCaption = ContextCompat.getColor(context, R.color.text_caption);
    }

    private void initTopBar(Context context) {
        // 初始化设置状态栏
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            StatusBarUtils.setStatusBarFullTransparent(activity);
        }

        // 初始化设置TopBar高度和Padding
        mTopStickyPeriodTab.getLayoutParams().height = mTopStickyPeriodHeight + mStatusBarHeight;
        mTopStickyPeriodTab.setPadding(0, mStatusBarHeight, 0, 0);
        mTopShopMenu.getLayoutParams().height = mTopShopMenuHeight + mStatusBarHeight;
        mTopShopMenu.setPadding(0, mStatusBarHeight, 0, 0);
        mBgTopWhiteMask.getLayoutParams().height = mTopShopMenuHeight + mStatusBarHeight;

        // 初始化设置顶部门店选择下拉列表
        mShopMenuAdapter = new ShopMenuAdapter(context);
        mShopMenuAnim = new ShopMenuAnim();
        mTopShopMenu.setAdapter(mShopMenuAdapter);
        mTopShopMenu.setAnim(mShopMenuAnim);
        mShopMenuAdapter.init();

    }

    private void initViewPager(Context context) {
        pageAdapter = new PageAdapter(getChildFragmentManager());
        pageListener = new PageListener();
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(pageAdapter);
        mPager.addOnPageChangeListener(pageListener);

        mPageTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    public void updateStatusBar() {
        resetTop();
        mPresenter.scrollToTop();
        if (mTopShopMenu.isShowing()) {
            mTopShopMenu.dismiss(false);
        }
    }

    private void showContent() {
        updateStickyPeriodTab(getActivity(), mIsStickyPeriodTop, false);
        mContentGroup.setVisibility(View.VISIBLE);
        mLayoutError.setVisibility(View.GONE);
        if (mTopShopMenu.isShowing()) {
            mTopShopMenu.dismiss(true);
        }
        hideLoadingDialog();
    }

    private void showError() {
        StatusBarUtils.setStatusBarFullTransparent(getActivity());
        mContentGroup.setVisibility(View.INVISIBLE);
        mTopStickyPeriodTab.setVisibility(View.INVISIBLE);
        mLayoutError.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    @Click(R.id.tv_dashboard_top_today)
    void clickPeriodToday() {
        mPresenter.switchPeriod(Constants.TIME_PERIOD_DAY, Utils.getPeriodTimestamp(Constants.TIME_PERIOD_DAY, 0));
    }

    @Click(R.id.tv_dashboard_top_yesterday)
    void clickPeriodYesterday() {
        mPresenter.switchPeriod(Constants.TIME_PERIOD_DAY, Utils.getPeriodTimestamp(Constants.TIME_PERIOD_DAY, -1));
    }

    @Click(R.id.tv_dashboard_top_week)
    void clickPeriodWeek() {
        mPresenter.switchPeriod(Constants.TIME_PERIOD_WEEK, Utils.getPeriodTimestamp(Constants.TIME_PERIOD_WEEK, 0));
    }

    @Click(R.id.tv_dashboard_top_month)
    void clickPeriodMonth() {
        mPresenter.switchPeriod(Constants.TIME_PERIOD_MONTH, Utils.getPeriodTimestamp(Constants.TIME_PERIOD_MONTH, 0));
    }

    @Click(R.id.btn_dashboard_tip_add_fs)
    void clickAddFs() {
        Router.withApi(IpcApi.class).goToIpcStartConfig(getContext(), CommonConstants.TYPE_IPC_FS, CommonConstants.CONFIG_IPC_FROM_COMMON);
    }

    @Click(R.id.btn_refresh)
    void clickReload() {
        showLoadingDialog();
        mPresenter.load(Constants.FLAG_ALL_MASK, false, false, true);
    }

    @Click(R.id.iv_close)
    void clickClose() {
        llFloating.setVisibility(View.GONE);
        mPresenter.closeFloatingAd();
    }

    @Click(R.id.btn_floating)
    void clickFloating() {
        if (isFastClick(500)) {
            return;
        }
        WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConstants.H5_CLOUD_STORAGE)
                .params(WebViewParamsUtils.getCloudStorageParams(new ArrayList<>(), "")).start();
    }

    @Override
    public PageContract.ParentPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public int getHeaderHeight() {
        return mTopHeaderHeight;
    }

    @Override
    public void setShopList(List<FilterItem> list) {
        mShopMenuAdapter.setData(list);
    }

    @Override
    public void switchPerspective(int perspective) {
        this.mPerspective = perspective;
        resetTop();
        mShopMenuAdapter.switchPerspective(perspective);
    }

    @Override
    public void setPages(List<PageHost> pages, int perspective) {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>(pages.size());
        for (PageHost page : pages) {
            tabs.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return getString(page.getTitle());
                }

                @Override
                public int getTabSelectedIcon() {
                    return page.getIcon();
                }

                @Override
                public int getTabUnselectedIcon() {
                    return page.getIcon();
                }
            });
        }
        pageListener.setPages(pages);
        pageAdapter.setPages(pages, perspective);
        mPager.setCurrentItem(0);
        if (mPageTab.getTabCount() != 0) {
            mPageTab.setCurrentTab(0);
        }
        mPageTab.setTabData(tabs);
    }

    @Override
    public void setCondition(DashboardCondition condition) {
        mHasInit = true;
        mHasData = condition.hasSaas || condition.hasFs;
        mNoFsTip.setVisibility(!condition.hasFs && condition.hasCustomer && !condition.hasFloating ?
                View.VISIBLE : View.INVISIBLE);
        showContent();
    }

    @Override
    public void updateTab(int pageType, int period) {
        if (pageType != mPresenter.getPageType()) {
            return;
        }
        if (pageType == Constants.PAGE_OVERVIEW) {
            mTodayView.setVisibility(View.VISIBLE);
            mYesterdayView.setVisibility(View.GONE);
        } else {
            mTodayView.setVisibility(View.GONE);
            mYesterdayView.setVisibility(View.VISIBLE);
        }
        mTodayView.setSelected(period == Constants.TIME_PERIOD_DAY);
        mTodayView.setTypeface(null, period == Constants.TIME_PERIOD_DAY ? Typeface.BOLD : Typeface.NORMAL);
        mYesterdayView.setSelected(period == Constants.TIME_PERIOD_DAY);
        mYesterdayView.setTypeface(null, period == Constants.TIME_PERIOD_DAY ? Typeface.BOLD : Typeface.NORMAL);
        mWeekView.setSelected(period == Constants.TIME_PERIOD_WEEK);
        mWeekView.setTypeface(null, period == Constants.TIME_PERIOD_WEEK ? Typeface.BOLD : Typeface.NORMAL);
        mMonthView.setSelected(period == Constants.TIME_PERIOD_MONTH);
        mMonthView.setTypeface(null, period == Constants.TIME_PERIOD_MONTH ? Typeface.BOLD : Typeface.NORMAL);
    }

    @Override
    public void updateTopPosition(int position) {
        if (mPerspective == CommonConstants.PERSPECTIVE_TOTAL) {
            int offset;
            if (position > mTopHeaderHeight) {
                offset = 0;
            } else if (position <= mTopPageTabHeight + mStatusBarHeight) {
                offset = -mTopShopMenuHeight;
            } else {
                offset = position - mTopHeaderHeight;
            }
            float fraction = (float) -offset / mTopShopMenuHeight;
            mTopShopMenu.setTranslationY(offset);
            mTopPageTab.setTranslationY(offset);
            mBgTopWhiteMask.setTranslationY(offset);
            mBgTopWhiteMask.setAlpha(fraction);
            mTabDivider.setAlpha(fraction);
            mTopPageTab.setBackgroundColor(Utils.getGradientColor(colorTextMain, colorWhite, fraction));
            mPageTab.setTextSelectColor(Utils.getGradientColor(colorWhite, colorTextMain, fraction));
            mPageTab.setTextUnselectColor(Utils.getGradientColor(colorWhite60a, colorTextCaption, fraction));
            mPageTab.setIndicatorColor(Utils.getGradientColor(colorWhite, colorOrange, fraction));
            mShopMenuAnim.setOffset(offset);
        } else {
            int offset = Math.min(position - mTopHeaderHeight, 0);
            mTopShopMenu.setTranslationY(offset);
            mShopMenuAnim.setOffset(offset);
            mTopPageTab.setTranslationY(offset);
            if (position > 0 && mIsStickyPeriodTop) {
                updateStickyPeriodTab(getActivity(), false, true);
            } else if (position <= 0 && !mIsStickyPeriodTop) {
                updateStickyPeriodTab(getActivity(), true, true);
            }
        }
    }

    @Override
    public void resetTop() {
        mTopShopMenu.setTranslationY(0);
        mTopPageTab.setTranslationY(0);
        mTopPageTab.setBackgroundColor(colorTextMain);
        mPageTab.setTextSelectColor(colorWhite);
        mPageTab.setTextUnselectColor(colorWhite60a);
        mPageTab.setIndicatorColor(colorWhite);
        mBgTopWhiteMask.setTranslationY(0);
        mBgTopWhiteMask.setAlpha(0);
        mTabDivider.setAlpha(0);
        mShopMenuAnim.setOffset(0);
        updateStickyPeriodTab(getActivity(), false, false);
    }

    @Override
    public void loadDataFailed() {
        if (mHasInit) {
            hideLoadingDialog();
            shortTip(R.string.toast_network_error);
        } else {
            showError();
        }
    }

    @UiThread
    @Override
    public void updateFloating(boolean showFloating) {
        if (showFloating) {
            llFloating.setVisibility(View.VISIBLE);
        } else {
            llFloating.setVisibility(View.GONE);
        }
    }

    private void updateStickyPeriodTab(Activity activity, boolean isShow, boolean animated) {
        if (activity == null) {
            return;
        }
        if (isShow) {
            StatusBarUtils.setStatusBarColor(activity, StatusBarUtils.TYPE_DARK);
            mTopShopMenu.setVisibility(View.INVISIBLE);
            mTopPageTab.setVisibility(View.INVISIBLE);
            mTopStickyPeriodTab.setVisibility(View.VISIBLE);
            mPager.setScrollable(false);
            mIsStickyPeriodTop = true;
        } else {
            StatusBarUtils.setStatusBarFullTransparent(activity);
            mTopShopMenu.setVisibility(View.VISIBLE);
            mTopPageTab.setVisibility(View.VISIBLE);
            mTopStickyPeriodTab.setVisibility(View.INVISIBLE);
            mPager.setScrollable(true);
            mIsStickyPeriodTop = false;
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{
                CommonNotifications.netConnected,
                CommonNotifications.companySwitch,
                CommonNotifications.companyNameChanged,
                CommonNotifications.shopSwitched,
                CommonNotifications.shopNameChanged,
                CommonNotifications.importShop,
                CommonNotifications.shopCreate,
                CommonNotifications.shopSaasDock,
                IpcConstants.refreshIpcList,
                CommonNotifications.cloudStorageChange,
                CommonNotifications.perspectiveSwitch
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.netConnected) {
            mPresenter.load(Constants.FLAG_ALL_MASK, true, true, true);
        } else if (id == CommonNotifications.companySwitch) {
            mHasInit = false;
            mShopMenuAdapter.setCompanyName(SpUtils.getCompanyName());
            mPresenter.load(Constants.FLAG_ALL_MASK, true, true, true);
        } else if (id == CommonNotifications.companyNameChanged) {
            mShopMenuAdapter.setCompanyName(SpUtils.getCompanyName());
        } else if (id == CommonNotifications.shopSwitched) {
            mHasInit = false;
            mPresenter.load(Constants.FLAG_SAAS | Constants.FLAG_FS | Constants.FLAG_CUSTOMER | Constants.FLAG_BUNDLED_LIST,
                    true, true, true);
        } else if (id == CommonNotifications.shopNameChanged
                || id == CommonNotifications.importShop
                || id == CommonNotifications.shopCreate) {
            mPresenter.load(Constants.FLAG_SHOP, true, true, true);
        } else if (id == CommonNotifications.shopSaasDock) {
            mPresenter.load(Constants.FLAG_SAAS, true, true, true);
        } else if (id == IpcConstants.refreshIpcList) {
            mPresenter.load(Constants.FLAG_FS | Constants.FLAG_BUNDLED_LIST, true, true, true);
        } else if (id == CommonNotifications.cloudStorageChange) {
            mPresenter.load(Constants.FLAG_BUNDLED_LIST, true, true, true);
        } else if (id == CommonNotifications.perspectiveSwitch) {
            mPresenter.switchPerspective(SpUtils.getPerspective(), true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.startAutoRefresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stopAutoRefresh();
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        private List<PageHost> data = new ArrayList<>();

        public void setPages(List<PageHost> list) {
            if (list == null || list.isEmpty()) {
                return;
            }
            this.data = list;
        }

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int position) {
            if (data.size() <= position) {
                return;
            }
            mPageTab.setCurrentTab(position);
            mPresenter.switchPage(data.get(position).getType());
            updateTab(mPresenter.getPageType(), mPresenter.getPeriod());
            resetTop();
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

}
