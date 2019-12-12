package com.sunmi.ipc.cash;

import android.app.Dialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.Config;
import com.sunmi.ipc.calendar.VerticalCalendar;
import com.sunmi.ipc.cash.adapter.CashCalendarAdapter;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CashOverviewContract;
import com.sunmi.ipc.model.CashVideoListBean;
import com.sunmi.ipc.presenter.CashOverviewPresenter;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.RouterConfig;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.FilterItem;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.CircleImage;
import sunmi.common.view.dialog.BottomDialog;
import sunmi.common.view.widget.CenterLayoutManager;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-02.
 */
@EActivity(resName = "activity_cash_video_overview")
public class CashVideoOverviewActivity extends BaseMvpActivity<CashOverviewPresenter> implements CashOverviewContract.View {

    @ViewById(resName = "rv_calendar")
    RecyclerView rvCalender;
    @ViewById(resName = "iv_calendar")
    ImageView ivCalender;
    @ViewById(resName = "tv_total_count_cash")
    TextView tvTotalCountCash;
    @ViewById(resName = "tv_total_count_abnormal")
    TextView tvTotalCountAbnormal;
    @ViewById(resName = "civ_ipc")
    CircleImage civIpc;
    @ViewById(resName = "tv_ipc_name")
    TextView tvIpcName;
    @ViewById(resName = "tv_ipc_sn")
    TextView tvIpcSn;
    @ViewById(resName = "tv_count_cash")
    TextView tvCountCash;
    @ViewById(resName = "tv_count_abnormal")
    TextView tvCountAbnormal;
    @ViewById(resName = "cl_shop_cash")
    ConstraintLayout clShopCash;
    @ViewById(resName = "cl_device")
    ConstraintLayout clDevice;
    @ViewById(resName = "layout_network_error")
    View networkError;

    @Extra
    ArrayList<CashVideoServiceBean> serviceBeans;
    @Extra
    boolean isSingleDevice;

    private final int REQUEST = 0x102;


    private CenterLayoutManager llManager;
    private Calendar today = Calendar.getInstance();
    private Calendar selectedCalendar, threeMonth = Calendar.getInstance();
    private Dialog calendarDialog;
    private VerticalCalendar calendarView;
    private List<Calendar> calendars = new ArrayList<>(15);
    private CashCalendarAdapter adapter;
    private int selectPos = 14;
    private long startTime;
    private long endTime;
    private int deviceId;
    private List<Integer> idList;
    private ScheduledExecutorService service;
    private List<Calendar> points = new ArrayList<>();
    private ArrayList<FilterItem> items = new ArrayList<>();
    private int shopCashCount, deviceCashCount;

    @RouterAnno(
            path = RouterConfig.Ipc.CASH_VIDEO_OVERVIEW
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), CashVideoOverviewActivity_.class);
        return intent;
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);
        mPresenter = new CashOverviewPresenter(serviceBeans);
        mPresenter.attachView(this);
        llManager = new CenterLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        rvCalender.setLayoutManager(llManager);
        selectedCalendar = Calendar.getInstance();
        items.add(new FilterItem(-1, getString(R.string.str_all_device), false));
        threeMonth.add(Calendar.MONTH, -3);
        threeMonth.add(Calendar.DATE, 1);
        threeMonth.set(Calendar.HOUR_OF_DAY, 0);
        threeMonth.set(Calendar.MINUTE, 0);
        threeMonth.set(Calendar.SECOND, 0);
        threeMonth.set(Calendar.MILLISECOND, 0);
        mPresenter.getCashVidoTimeSlots(deviceId, (threeMonth.getTimeInMillis() / 1000), (DateTimeUtils.getTomorrow().getTime() / 1000));
        if (isSingleDevice) {
            deviceId = serviceBeans.get(0).getDeviceId();
            clShopCash.setVisibility(View.GONE);
            clDevice.setBackgroundResource(R.drawable.bg_top_gray_black_radius);
        } else {
            deviceId = -1;
        }
        initDate();
    }

    private void initDate() {
        initStartAndEndTime();
        calendars.clear();
        Calendar c;
        int todayIndex = DateTimeUtils.getDiffDays(selectedCalendar.getTime(), today.getTime());
        int mothIndex = DateTimeUtils.getDiffDays(threeMonth.getTime(), selectedCalendar.getTime());
        if (todayIndex < 15) {
            selectPos = 14 - todayIndex;
            c = Calendar.getInstance();
            c.add(Calendar.DATE, -14);
        } else if (mothIndex < 15 && todayIndex > 15) {
            selectPos = mothIndex;
            c = (Calendar) threeMonth.clone();
        } else {
            selectPos = 7;
            c = selectedCalendar;
            c.add(Calendar.DATE, -7);
        }
        for (int i = 0; i < 15; i++) {
            calendars.add((Calendar) c.clone());
            c.add(Calendar.DATE, 1);
        }
        initAdapter();
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new CashCalendarAdapter(context, calendars);
            adapter.setOnItemClickListener((calendar, pos) -> {
                llManager.smoothScrollToPosition(rvCalender, new RecyclerView.State(), pos);
                selectedCalendar = calendar;
                initStartAndEndTime();
            });
            rvCalender.setAdapter(adapter);
            rvCalender.scrollToPosition(selectPos);
        } else {
            adapter.setSelectPosition(selectPos);
            llManager.smoothScrollToPosition(rvCalender, new RecyclerView.State(), selectPos);
        }
    }

    /**
     * 计算开始时间和结束时间并定时调用接口
     */
    @Background
    protected void initStartAndEndTime() {
        showLoadingDialog();
        if (idList == null) {
            idList = new ArrayList<>();
            for (CashVideoServiceBean bean : serviceBeans) {
                idList.add(bean.getDeviceId());
                items.add(new FilterItem(bean.getDeviceId(), bean.getDeviceName()));
            }
        }
        startTime = (DateTimeUtils.getDayStart(selectedCalendar).getTimeInMillis()) / 1000;
        endTime = startTime + 3600 * 24;
        service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            if (isSingleDevice) {
                mPresenter.getIpcCashVideoCount(idList, startTime, endTime);
            } else {
                mPresenter.getShopCashVideoCount(startTime, endTime);
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

    @Click(resName = "iv_calendar")
    public void calendarClick() {
        if (isFastClick(1000)) {
            return;
        }
        if (calendarDialog == null || calendarView == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -3);
            Config config = new Config.Builder()
                    .setMinDate(c)
                    .setPoint(points)
                    .build();
            int height = (int) (Utils.getScreenHeight(context) * 0.85);
            calendarView = new VerticalCalendar(this, config);
            calendarView.setOnCalendarSelectListener(calendar -> selectedCalendar = calendar);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            calendarDialog = new BottomDialog.Builder(context)
                    .setTitle(R.string.str_title_calendar)
                    .setContent(calendarView, lp)
                    .setCancelButton(R.string.sm_cancel)
                    .setOkButton(R.string.str_confirm, (dialog, which) -> {
                        initDate();
                    }).create();
        }
        calendarView.setSelected(startTime * 1000);
        calendarDialog.show();
    }

    @Click(resName = "btn_refresh")
    public void refreshClick() {
        showLoadingDialog();
        if (isSingleDevice) {
            mPresenter.getIpcCashVideoCount(idList, startTime, endTime);
        } else {
            mPresenter.getShopCashVideoCount(startTime, endTime);
        }
    }

    @Click(resName = "ll_cash_video")
    public void totalCashClick() {
        clearItems();
        items.get(0).setChecked(true);
        CashVideoListActivity_.intent(context).startTime(startTime).endTime(endTime).items(items)
                .isSingleDevice(isSingleDevice).total(shopCashCount).startForResult(REQUEST);
    }

    @Click(resName = "ll_abnormal_video")
    public void totalAbnormalClick() {
        clearItems();
        items.get(0).setChecked(true);
        CashVideoListActivity_.intent(context).startTime(startTime).endTime(endTime)
                .videoType(IpcConstants.CASH_VIDEO_ABNORMAL).items(items).isSingleDevice(isSingleDevice)
                .total(shopCashCount).startForResult(REQUEST);
    }

    @Click(resName = "cv_cash")
    public void deviceCashClick() {
        clearItems();
        items.get(1).setChecked(true);
        CashVideoListActivity_.intent(context).startTime(startTime).endTime(endTime)
                .deviceId(idList.get(0)).items(items).isSingleDevice(isSingleDevice)
                .total(deviceCashCount).startForResult(REQUEST);
    }

    @Click(resName = "cv_abnormal")
    public void deviceAbnormalClick() {
        clearItems();
        items.get(1).setChecked(true);
        CashVideoListActivity_.intent(context).startTime(startTime).endTime(endTime)
                .deviceId(idList.get(0)).videoType(IpcConstants.CASH_VIDEO_ABNORMAL).items(items)
                .isSingleDevice(isSingleDevice).total(deviceCashCount).startForResult(REQUEST);
    }

    private void clearItems() {
        for (FilterItem item : items) {
            item.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.shutdown();
    }

    @Override
    public void getCashVideoTimeSlotsSuccess(List<Long> timeSlots) {
        Set<Long> times = new HashSet<>(timeSlots);
        for (long time : times) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time * 1000);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            points.add((Calendar) c.clone());
        }
    }

    @Override
    public void getShopCashVideoCountSuccess(CashVideoListBean bean) {
        mPresenter.getIpcCashVideoCount(idList, startTime, endTime);
        shopCashCount = bean.getTotalCount();
        tvTotalCountCash.setText(String.valueOf(shopCashCount));
        tvTotalCountAbnormal.setText(String.valueOf(bean.getAbnormalVideoCount()));
    }

    @UiThread
    @Override
    public void getIpcCashVideoCountSuccess(List<CashVideoServiceBean> beans) {
        if (networkError.isShown()) {
            networkError.setVisibility(View.GONE);
        }
        hideLoadingDialog();
        if (beans.isEmpty()) {
            return;
        }
        CashVideoServiceBean bean = beans.get(0);
        ImageUtils.loadImage(context, bean.getImgUrl(), civIpc, false, -1);
        tvIpcName.setText(bean.getDeviceName());
        tvIpcSn.setText(getString(R.string.str_dev_sn, bean.getDeviceSn()));
        deviceCashCount = bean.getTotalCount();
        tvCountCash.setText(String.valueOf(deviceCashCount));
        tvCountAbnormal.setText(String.valueOf(bean.getAbnormalVideoCount()));
    }

    @Override
    public void netWorkError() {
        hideLoadingDialog();
        networkError.setVisibility(View.VISIBLE);
    }

    @OnActivityResult(REQUEST)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (isSingleDevice) {
                mPresenter.getIpcCashVideoCount(idList, startTime, endTime);
            } else {
                mPresenter.getShopCashVideoCount(startTime, endTime);
            }
        }
    }
}
