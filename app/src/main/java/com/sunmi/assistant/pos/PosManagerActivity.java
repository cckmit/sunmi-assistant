package com.sunmi.assistant.pos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.pos.contract.PosContract;
import com.sunmi.assistant.pos.contract.PosPresenter;
import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;

/**
 * @author yangShiJie
 * @date 2019-11-14
 */
@SuppressLint("Registered")
@EActivity(R.layout.pos_activity_manager)
public class PosManagerActivity extends BaseMvpActivity<PosPresenter> implements PosContract.View {
    @ViewById(R.id.av_view_cup)
    PosArcView arcViewCpu;
    @ViewById(R.id.av_view_storage)
    PosArcView arcViewStorage;
    @ViewById(R.id.av_view_store)
    PosArcView arcViewSd;
    @ViewById(R.id.tv_ghz_value)
    TextView tvGhzValue;
    @ViewById(R.id.tv_cpu_total)
    TextView tvCpuTotal;
    @ViewById(R.id.tv_storage_value)
    TextView tvStorageValue;
    @ViewById(R.id.tv_storage_name)
    TextView tvStorageName;
    @ViewById(R.id.tv_storage_total)
    TextView tvStorageTotal;
    @ViewById(R.id.tv_store_value)
    TextView tvSdValue;
    @ViewById(R.id.tv_store_name)
    TextView tvSdName;
    @ViewById(R.id.tv_store_total)
    TextView tvSdTotal;
    @ViewById(R.id.pos_name)
    TextView posName;
    @ViewById(R.id.pos_battery)
    TextView posBattery;
    @ViewById(R.id.iv_battery)
    ImageView ivBattery;
    @ViewById(R.id.iv_dev)
    ImageView ivDev;
    @ViewById(R.id.tv_guarantee_status)
    TextView tvGuaranteeStatus;

    @Extra
    SunmiDevice device;
    /**
     * 设置进度颜色
     */
    private int[] colorOrange = new int[]{Color.parseColor("#FF7040"), Color.parseColor("#FF3838"), Color.parseColor("#FF7040")};
    private int[] colorDeepBlue = new int[]{Color.parseColor("#3399FF"), Color.parseColor("#3355FF"), Color.parseColor("#3399FF")};
    private int[] colorLightBlue = new int[]{Color.parseColor("#46EBEB"), Color.parseColor("#00AAFF"), Color.parseColor("#46EBEB")};

    private PosDetailsResp mResp;
    private ScheduledExecutorService executorService;

    @AfterViews
    void init() {
        mPresenter = new PosPresenter(device.getDeviceid());
        mPresenter.attachView(this);
        mPresenter.getPosType(device.getModel());
        posName.setText("SUNMI " + device.getModel());
        getPosDetails();
        startExecutorService();
    }

    private void getPosDetails() {
        mPresenter.getPosDetails();
        mPresenter.getPosGuarantee();
    }

    //开启定时
    private void startExecutorService() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(this::getPosDetails, 30, 30, TimeUnit.SECONDS);
    }

    //结束定时
    public void closeExecutorService() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    @Click(R.id.ctl_dev)
    void devDetailClick() {
        PosDetailsActivity_.intent(this).device(device).posResp(mResp).start();
    }

    @Click(R.id.ctl_guarantee)
    void guaranteeClick() {
        PosGuaranteeActivity_.intent(this).device(device).start();
    }


    @Override
    public void getPosDetailsSuccess(PosDetailsResp resp) {
        mResp = resp;
        //cpu
        String useCpuPercent = resp.getRunningInfo().getUsedCpuPercent();
        int mUseCpuPercent = Integer.valueOf(useCpuPercent.replace("%", ""));
        String cpu = resp.getRunningInfo().getCpuFrequency().replaceAll(" ", "");
        double totalCpu = Double.valueOf(replaceA_Z(cpu));
        double useCpu = mUseCpuPercent * totalCpu / 100;
        tvGhzValue.setText(strDecimal(useCpu));
        tvCpuTotal.setText(String.format(getString(R.string.pos_total_data), cpu));
        circlePercent(arcViewCpu, mUseCpuPercent, tvGhzValue.getText().toString());
        //running mem
        String useMemPercent = resp.getRunningInfo().getUsedMemPercent();
        int mUseMemPercent = Integer.valueOf(useMemPercent.replace("%", ""));
        String totalMem = resp.getRunningInfo().getTotalMem().replaceAll(" ", "");
        String useMem = resp.getRunningInfo().getUsedMem().replaceAll(" ", "");
        double mUseMem = Double.valueOf(replaceA_Z(useMem));
        if (useMem.contains(getString(R.string.pos_g))) {
            tvStorageValue.setText(strDecimal(mUseMem));
        } else if (useMem.contains(getString(R.string.pos_m))) {
            if (mUseMem >= 1000) {
                tvStorageName.setText(R.string.pos_g);
                tvStorageValue.setText(strDecimal(mUseMem / 1024));
            } else {
                tvStorageName.setText(R.string.pos_m);
                tvStorageValue.setText(strDecimal(mUseMem));
            }
        }
        circlePercent(arcViewStorage, mUseMemPercent, tvStorageValue.getText().toString());
        tvStorageTotal.setText(String.format(getString(R.string.pos_total_data), totalMem));
        //sd
        String useSd = resp.getRunningInfo().getUsedSd().replaceAll(" ", "");
        String totalSd = resp.getRunningInfo().getTotalSd().replaceAll(" ", "");
        double mUseSd = Double.valueOf(replaceA_Z(useSd));
        if (useSd.contains(getString(R.string.pos_g))) {
            tvSdName.setText(R.string.pos_g);
            tvSdValue.setText(strDecimal(mUseSd));
        } else if (useSd.contains(getString(R.string.pos_m))) {
            if (mUseSd >= 1000) {
                tvSdName.setText(R.string.pos_g);
                tvSdValue.setText(strDecimal(mUseSd / 1024));
            } else {
                tvSdName.setText(R.string.pos_m);
                tvSdValue.setText(strDecimal(mUseSd));
            }
        }
        double mTotalSd = Double.valueOf(replaceA_Z(totalSd));
        int useSdPercent = (int) (mUseSd * 100 / mTotalSd);
        circlePercent(arcViewSd, useSdPercent, tvSdValue.getText().toString());
        tvSdTotal.setText(String.format(getString(R.string.pos_total_data), totalSd));
        //battery
        String batteryPercent = resp.getRunningInfo().getBatteryPercent();
        int isCharging = resp.getRunningInfo().getIsCharging();//0没有充电 1充电
        String batteryPer = resp.getRunningInfo().getBatteryPercent().replace("%", "");
        int mBatteryPer = Integer.valueOf(batteryPer);
        posBattery.setText(batteryPercent);
        if (isCharging == 1) {
            ivBattery.setImageResource(R.mipmap.ic_pos_battery_change);
        } else {
            showBatteryStatus(mBatteryPer);
        }
    }

    @Override
    public void getPosGuaranteeSuccess(PosWarrantyResp resp) {
        //1=没过保，0=已过保
        int status = resp.getStatus();
        if (status == 1) {
            tvGuaranteeStatus.setText(R.string.pos_activated);
            tvGuaranteeStatus.setTextColor(ContextCompat.getColor(this, R.color.text_caption));
        } else {
            tvGuaranteeStatus.setText(R.string.pos_expire);
            tvGuaranteeStatus.setTextColor(ContextCompat.getColor(this, R.color.caution_primary));
        }
    }

    @Override
    public void getPosTypeSuccess(boolean isDesktop) {
        ivDev.setBackgroundResource(isDesktop ? R.mipmap.ic_pos_dev_desktop : R.mipmap.ic_pos_dev);
        posBattery.setVisibility(isDesktop ? View.GONE : View.VISIBLE);
        ivBattery.setVisibility(isDesktop ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeExecutorService();
    }

    private String strDecimal(double d) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
        return df.format(d);
    }

    private void circlePercent(PosArcView view, int percent, String used) {
        if (TextUtils.equals("0.0", used)) {
            percent = 0;
        }
        if (percent <= 50) {
            view.setValues(colorLightBlue, percent);
        } else if (percent <= 80) {
            view.setValues(colorDeepBlue, percent);
        } else if (percent <= 100) {
            view.setValues(colorOrange, percent);
        } else {
            view.setValues(colorOrange, 100);
        }
    }

    private void showBatteryStatus(int percent) {
        int resourceId = CommonHelper.getResource(BaseApplication.getContext(),
                "ic_pos_battery" + (percent / 10) + "0", R.mipmap.ic_pos_battery50);
        ivBattery.setImageResource(resourceId);
    }

    /**
     * 替换的字符串中的字母
     *
     * @param str str
     * @return
     */
    private String replaceA_Z(String str) {
        return str.replaceAll("[a-zA-Z]", "");
    }
}
