package com.sunmi.assistant.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DashboardPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpFragment;

/**
 * 首页数据Dashboard的展示
 *
 * @author yinhui
 * @since 2019-06-13
 */
@EFragment(R.layout.dashboard_fragment_main)
public class DashboardFragment extends BaseMvpFragment<DashboardPresenter>
        implements DashboardContract.View {

    @ViewById(R.id.tv_dashboard_company_name)
    TextView mCompanyName;
    @ViewById(R.id.tv_dashboard_store_name)
    TextView mStoreName;

    @ViewById(R.id.tv_dashboard_today)
    TextView mTimeSpanToday;
    @ViewById(R.id.tv_dashboard_week)
    TextView mTimeSpanWeek;
    @ViewById(R.id.tv_dashboard_month)
    TextView mTimeSpanMonth;

    @ViewById(R.id.rv_dashboard_card_list)
    RecyclerView mCardList;

    @AfterViews
    void init() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        mPresenter.loadConfig();
        mPresenter.loadDashboardData(DashboardContract.TIME_SPAN_TODAY);
        initView();
    }

    @UiThread
    void initView() {
        initSwitchers();
    }

    @UiThread
    void initSwitchers() {
        mTimeSpanToday.setSelected(true);
        mTimeSpanWeek.setSelected(false);
        mTimeSpanMonth.setSelected(false);
    }

    @UiThread
    void setNames(String company, String store) {
        mCompanyName.setText(company);
        mStoreName.setText(store);
    }

    @UiThread
    void setAdapter() {

    }

    @Click(R.id.tv_dashboard_today)
    void switchToToday() {
        mPresenter.loadDashboardData(DashboardContract.TIME_SPAN_TODAY);
        mTimeSpanToday.setSelected(true);
        mTimeSpanWeek.setSelected(false);
        mTimeSpanMonth.setSelected(false);
    }

    @Click(R.id.tv_dashboard_week)
    void switchToWeek() {
        mPresenter.loadDashboardData(DashboardContract.TIME_SPAN_WEEK);
        mTimeSpanToday.setSelected(false);
        mTimeSpanWeek.setSelected(true);
        mTimeSpanMonth.setSelected(false);
    }

    @Click(R.id.tv_dashboard_month)
    void switchToMonth() {
        mPresenter.loadDashboardData(DashboardContract.TIME_SPAN_MONTH);
        mTimeSpanToday.setSelected(false);
        mTimeSpanWeek.setSelected(false);
        mTimeSpanMonth.setSelected(true);
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

}
