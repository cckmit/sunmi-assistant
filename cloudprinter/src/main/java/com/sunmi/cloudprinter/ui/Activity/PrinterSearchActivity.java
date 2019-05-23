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

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.BlueDevice;
import com.sunmi.cloudprinter.ui.adaper.BlueListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.CommonDialog;

@EActivity(resName = "activity_search_printer")
public class PrinterSearchActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback {

    @ViewById(resName = "rv_ble")
    RecyclerView rvResult;
    @ViewById(resName = "divider_top")
    View dividerTop;

    private Set<String> snSet = new HashSet<>();
    private List<BlueDevice> list = new ArrayList<>();
    private BlueListAdapter adapter;

    //蓝牙adapter
    private BluetoothAdapter btAdapter;
    private Handler mHandler = new Handler();

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
        StatusBarUtils.StatusBarLightMode(this);//状态栏
        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.getLocationPermission(this))
            initBt();
    }

    private void initBt() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            new CommonDialog.Builder(this).setTitle(R.string.str_prompt).setMessage(R.string.str_tip_start_blue)
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

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (scanRecord.length > 11) {
            addDevice(device);
        }
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            btAdapter.enable();
        }
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvResult.setLayoutManager(layoutManager);
        rvResult.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new BlueListAdapter(context, list);
        rvResult.setAdapter(adapter);
    }

    private void addDevice(BluetoothDevice device) {
        if (!snSet.contains(device.getAddress()) && !TextUtils.isEmpty(device.getName())) {
            LogCat.e(TAG, "222222 added device addr = " + device.getAddress());
            snSet.add(device.getAddress());
            BlueDevice bleDevice = new BlueDevice();
            bleDevice.setAddress(device.getAddress());
            bleDevice.setName(device.getName());
            listAdd(bleDevice);
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

}
