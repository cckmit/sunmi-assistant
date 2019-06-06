package com.sunmi.cloudprinter.ui.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.inuker.bluetooth.library.BluetoothClient;
import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.BlueDevice;
import com.sunmi.cloudprinter.constant.BtBleContract;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.presenter.BtBlePresenter;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.cloudprinter.ui.adaper.PrinterListAdapter;
import com.sunmi.cloudprinter.utils.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.CommonDialog;

@EActivity(resName = "activity_search_printer")
public class PrinterSearchActivity extends BaseMvpActivity<BtBlePresenter>
        implements BtBleContract.View, BluetoothAdapter.LeScanCallback,
        PrinterListAdapter.OnItemClickListener {

    @ViewById(resName = "rv_ble")
    RecyclerView rvResult;
    @ViewById(resName = "divider_top")
    View dividerTop;

    @Extra
    String shopId;

    private Set<String> macSet = new HashSet<>();
    private List<BlueDevice> list = new ArrayList<>();
    private PrinterListAdapter adapter;

    //蓝牙adapter
    private BluetoothAdapter btAdapter;

    private Handler mHandler = new Handler();
    private BluetoothClient mClient;
    String bleAddress;

    private Runnable scanStart = new Runnable() {
        @Override
        public void run() {
            if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
                startScan();
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @AfterViews
    protected void init() {
        mPresenter = new BtBlePresenter();
        mPresenter.attachView(this);
        StatusBarUtils.StatusBarLightMode(this);//状态栏
        mClient = new BluetoothClient(context);
        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.getLocationPermission(this))
            initBt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
//        if (mClient != null) {
//            mClient.disconnect(bleAddress);
//            mClient = null;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initBt();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (scanRecord.length > 11) {
            addDevice(device);
        }
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            btAdapter.enable();
        }
    }

    @Override
    public void onItemClick(final BlueDevice blueDevice) {
        new CommonDialog.Builder(context).setTitle(R.string.str_prompt)
                .setMessage(R.string.str_tip_link_device)
                .setCancelButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopScan();
                        bleAddress = blueDevice.getAddress();
                        mPresenter.sendData(mClient, bleAddress, Utility.cmdGetSn());
                    }
                }).create().show();
    }

    String sn;

    @Override
    public void onResponse(byte[] value) {
        int cmd = Utility.getCmd(value);
        if (cmd == Constants.SRV2CLI_SEND_SN) {
            sn = Utility.getSn(value);
            LogCat.e(TAG, "222222 ffff sn = " + sn);//N302D94D40068 N302D94D46666
            bindPrinter(sn);
        }
    }

    private void initBt() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            new CommonDialog.Builder(this).setTitle(R.string.str_prompt)
                    .setMessage(R.string.str_tip_start_blue)
                    .setCancelButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btAdapter.enable();
                    mHandler.post(scanStart);
                }
            }).create().show();
        }
        startScan();
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvResult.setLayoutManager(layoutManager);
        rvResult.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new PrinterListAdapter(context, list);
        adapter.setListener(this);
        rvResult.setAdapter(adapter);
    }

    private void addDevice(BluetoothDevice device) {
        if (!macSet.contains(device.getAddress()) && !TextUtils.isEmpty(device.getName())) {
            if (device.getName().startsWith("cloudprint_") || device.getName().startsWith("CloudPrint_")) {
                macSet.add(device.getAddress());
                BlueDevice bleDevice = new BlueDevice();
                bleDevice.setAddress(device.getAddress());
                bleDevice.setName(device.getName());
                listAdd(bleDevice);
            }
        }
    }

    private void listAdd(BlueDevice bleDevice) {
        if (!dividerTop.isShown()) dividerTop.setVisibility(View.VISIBLE);
        list.add(bleDevice);
        adapter.notifyDataSetChanged();
    }

    //startLeScan 和stopLeScan 需使用同一个Callback
    private void startScan() {
        if (btAdapter != null) btAdapter.startLeScan(this);
    }

    private void stopScan() {
        if (btAdapter != null) btAdapter.stopLeScan(this);
    }

    private void bindPrinter(String sn) {
        showLoadingDialog();
        IOTCloudApi.bindPrinter(shopId, sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                hideLoadingDialog();
                gotoPrinterSet();
            }

            @Override
            public void onFail(int code, String msg, String data) {
                hideLoadingDialog();
                if (code == 4402) {
                    shortTip("已经被绑定无须重新绑定");
                    gotoPrinterSet();
                } else {
                    shortTip("配置失败，请重试");
                }
            }
        });
    }

    private void gotoPrinterSet() {
        new CommonDialog.Builder(this)
                .setMessage("已完成绑定，是否要继续给打印机配置无线网络？")
                .setCancelButton(R.string.str_skip, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        GotoActivityUtils.gotoMainActivity(context);
                        BaseNotification.newInstance().postNotificationName(Constants.NOTIFICATION_PRINTER_ADDED);
                        finish();
                    }
                }).setConfirmButton(R.string.str_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (mClient != null) {
//                    mClient.disconnect(bleAddress);
//                    mClient = null;
//                }
                dialog.dismiss();
                SetPrinterActivity_.intent(context).sn(sn).bleAddress(bleAddress).start();
            }
        }).create().show();
    }

}
