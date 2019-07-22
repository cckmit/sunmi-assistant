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
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.dialog.CommonDialog;

@EActivity(resName = "activity_search_printer")
public class PrinterSearchActivity extends BaseActivity//BaseMvpActivity<BtBlePresenter>
        implements //BtBleContract.View, BluetoothAdapter.LeScanCallback,
        PrinterListAdapter.OnItemClickListener, SunmiPrinterClient.IPrinterClient {

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
    //蓝牙adapter
//    private BluetoothAdapter btAdapter;

    private Handler mHandler = new Handler();
    //    private BluetoothClient mClient;
    String bleAddress;
    String sn;

    private Runnable scanStart = new Runnable() {
        @Override
        public void run() {
//            if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
//                startScan();
//            } else {
//                mHandler.postDelayed(this, 1000);
//            }
        }
    };

    @AfterViews
    protected void init() {
//        mPresenter = new BtBlePresenter();
//        mPresenter.attachView(this);
        StatusBarUtils.StatusBarLightMode(this);//状态栏
//        mClient = new BluetoothClient(context);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initBt();
    }

//    @Override
//    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        if (scanRecord.length > 11) {
//            addDevice(device);
//        }
//        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
//            btAdapter.enable();
//        }
//    }

    @Override
    public void onItemClick(final PrinterDevice printerDevice) {
        showLoadingDialog();
        stopScan();
        bleAddress = printerDevice.getAddress();
        sunmiPrinterClient.getPrinterSn(bleAddress);
//        mPresenter.sendData(mClient, bleAddress, Utility.cmdGetSn());
    }

//    @Override
//    public void bindSuccess(int code, String msg, String data) {
//        gotoPrinterSet();
//    }
//
//    @Override
//    public void bindFail(int code, String msg, String data) {
//        if (code == 4402) {
//            shortTip(R.string.tip_printer_already_bound);
//            gotoPrinterSet();
//        } else {
//            shortTip(R.string.tip_printer_bind_fail);
//        }
//    }

//    @Override
//    public void onResponse(byte[] value) {
//        int cmd = Utility.getCmd(value);
//        if (cmd == Constants.SRV2CLI_SEND_SN) {
//            sn = Utility.getSn(value);
//            LogCat.e(TAG, "getsn = " + sn);//N302D94D40068 N302D94D46666
//            mPresenter.bindPrinter(shopId, sn);
//        }
//    }

    private void initBt() {
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        btAdapter = bluetoothManager.getAdapter();
//        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
//            new CommonDialog.Builder(this).setTitle(R.string.str_prompt)
//                    .setMessage(R.string.str_tip_start_blue)
//                    .setCancelButton(R.string.sm_cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    }).setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    btAdapter.enable();
//                    mHandler.post(scanStart);
//                }
//            }).create().show();
//        }
//        startScan();
        sunmiPrinterClient = new SunmiPrinterClient(context, bleAddress, this);
        startScan();
    }

    private void initList() {
        rvResult.init(R.drawable.shap_line_divider);
        printerAdapter = new PrinterListAdapter(context, list);
        printerAdapter.setListener(this);
        rvResult.setAdapter(printerAdapter);
    }

//    private void addDevice(BluetoothDevice device) {
//        if (!macSet.contains(device.getAddress()) && !TextUtils.isEmpty(device.getName())) {
//            if (device.getName().startsWith("cloudprint_")
//                    || device.getName().startsWith("CloudPrint_")) {
//                macSet.add(device.getAddress());
//                PrinterDevice bleDevice = new PrinterDevice();
//                bleDevice.setAddress(device.getAddress());
//                bleDevice.setName(device.getName());
//                listAdd(bleDevice);
//            }
//        }
//    }

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
//        if (btAdapter != null) {
//            btAdapter.startLeScan(this);
//        }
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
//        if (btAdapter != null) {
//            btAdapter.stopLeScan(this);
//        }
        sunmiPrinterClient.stopScan();
    }

    private void gotoPrinterSet() {
        WifiConfigActivity_.intent(context).sn(sn).bleAddress(bleAddress).start();
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
    public void onSnReceived(String sn) {
        sunmiPrinterClient.bindPrinter(shopId, sn);
    }

    @Override
    public void onGetWifiListSuccess() {

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
    public void bindPrinterSuccess(int code, String msg, String data) {
        gotoPrinterSet();
    }

    @Override
    public void bindPrinterFail(int code, String msg, String data) {
        if (code == 4402) {//todo 被自己绑定和被别人绑定都是4402，需要iot区分
//            showErrorDialog(R.string.tip_printer_already_bound);
            gotoPrinterSet();
        } else if (code == 4400) {
            showErrorDialog(R.string.tip_error_sn);
        } else if (code == RpcErrorCode.RPC_COMMON_ERROR
                || code == RpcErrorCode.RPC_ERR_TIMEOUT) {
            showErrorDialog(R.string.tip_bind_printer_error_no_net);
        } else {
            shortTip(R.string.tip_bind_printer_fail);
        }
        //该打印机已被182XXXX9876账号绑定，请解绑后配置。
        //
        //
    }

    @Override
    public void routerFound(Router router) {

    }

    private void showErrorDialog(int msgResId) {
        new CommonDialog.Builder(context)
                .setTitle(R.string.sm_title_hint)
                .setMessage(msgResId)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

}
