package com.sunmi.cloudprinter.ui.Activity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.PrinterDevice;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.presenter.SunmiPrinterClient;
import com.sunmi.cloudprinter.ui.adaper.PrinterListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BaseActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.dialog.CommonDialog;

@EActivity(resName = "activity_search_printer")
public class PrinterSearchActivity extends BaseActivity
        implements PrinterListAdapter.OnItemClickListener, SunmiPrinterClient.IPrinterClient {

    @ViewById(resName = "rv_ble")
    SmRecyclerView rvResult;
    @ViewById(resName = "tv_top")
    TextView tvAddPrinter;
    @ViewById(resName = "nsv_printer")
    NestedScrollView nsvPrinter;
    @ViewById(resName = "rl_no_device")
    RelativeLayout rlNoWifi;
    @ViewById(resName = "rl_loading")
    RelativeLayout rlLoading;
    @ViewById(resName = "btn_refresh")
    Button btnRefresh;

    @Extra
    int shopId;

    private static final long DURATION_SCAN = 30_000;
    private Set<String> macSet = new HashSet<>();
    private List<PrinterDevice> list = new ArrayList<>();
    private PrinterListAdapter printerAdapter;

    private SunmiPrinterClient sunmiPrinterClient;

    private boolean isSnGot;

    String bleAddress;
    String sn;

    @AfterViews
    protected void init() {
        StatusBarUtils.StatusBarLightMode(this);//状态栏
        sunmiPrinterClient = new SunmiPrinterClient(context, bleAddress, this);
        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.getLocationPermission(this))
            initBt();
    }

    @Click(resName = "btn_retry")
    void retryClick() {
        tvAddPrinter.setVisibility(View.VISIBLE);
        nsvPrinter.setVisibility(View.VISIBLE);
        rlNoWifi.setVisibility(View.GONE);
        startScan();
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        rlLoading.setVisibility(View.VISIBLE);
        btnRefresh.setVisibility(View.GONE);
        startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        sunmiPrinterClient = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initBt();
    }

    @Override
    public void onItemClick(final PrinterDevice printerDevice) {
        showLoadingDialog();
        stopScan();
        bleAddress = printerDevice.getAddress();
        sunmiPrinterClient.getPrinterSn(bleAddress);
    }

    private void initBt() {
        sunmiPrinterClient = new SunmiPrinterClient(context, bleAddress, this);
        startScan();
    }

    private void initList() {
        rvResult.init(R.drawable.shap_line_divider);
        printerAdapter = new PrinterListAdapter(context, list);
        printerAdapter.setListener(this);
        rvResult.setAdapter(printerAdapter);
    }

    @UiThread
    void addDevice(PrinterDevice bleDevice) {
        list.add(bleDevice);
        printerAdapter.notifyDataSetChanged();
    }

    //startLeScan 和stopLeScan 需使用同一个Callback
    private void startScan() {
        list.clear();
        macSet.clear();
        printerAdapter.notifyDataSetChanged();
        sunmiPrinterClient.startScan();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                if (list.size() <= 0) {
                    tvAddPrinter.setVisibility(View.GONE);
                    nsvPrinter.setVisibility(View.GONE);
                    rlNoWifi.setVisibility(View.VISIBLE);
                } else {
                    rlLoading.setVisibility(View.GONE);
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }
        }, DURATION_SCAN);
    }

    private void stopScan() {
        sunmiPrinterClient.stopScan();
    }

    @Override
    public void onPrinterFount(PrinterDevice printerDevice) {
        if (!macSet.contains(printerDevice.getAddress())) {
            macSet.add(printerDevice.getAddress());
            addDevice(printerDevice);
        }
    }

    @Override
    public void sendDataFail(int code, String msg) {
        showErrorDialog(R.string.tip_printer_connect_fail);
    }

    @Override
    public void getSnRequestSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSnGot) {
                    showErrorDialog(R.string.tip_get_printer_info_fail);
                }
            }
        }, 10_000);
    }

    @Override
    public void onSnReceived(String sn) {//N302D96D40077
        isSnGot = true;
        sunmiPrinterClient.bindPrinter(shopId, sn);
    }

    @Override
    public void onGetWifiListFinish() {

    }

    @Override
    public void onGetWifiListFail() {

    }

    @Override
    public void onSetWifiSuccess() {

    }

    @Override
    public void wifiConfigSuccess() {

    }

    @Override
    public void onWifiConfigFail() {

    }

    @Override
    public void bindPrinterSuccess(int code, String msg, String data) {
        BaseNotification.newInstance().postNotificationName(Constants.NOTIFICATION_PRINTER_ADDED);
        gotoPrinterSet();
    }

    @Override
    public void bindPrinterFail(int code, String msg, String data) {
        if (code == 4400) {
            showErrorDialog(R.string.tip_error_sn);
        } else if (code == 4401) {
            showErrorDialog(R.string.tip_printer_already_bound);
        } else if (code == 4402) {
            gotoPrinterSet();
        } else if (code == 4404) {
            showErrorDialog(R.string.tip_bound_to_other_shop);
        } else {
            showErrorDialog(R.string.tip_bind_printer_error_no_net);
        }
    }

    @Override
    public void routerFound(Router router) {

    }

    private void showErrorDialog(int msgResId) {
        hideLoadingDialog();
        new CommonDialog.Builder(context)
                .setTitle(R.string.sm_title_hint)
                .setMessage(msgResId)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

    private void gotoPrinterSet() {
        WifiConfigActivity_.intent(context).sn(sn).bleAddress(bleAddress).start();
        finish();
    }

}
