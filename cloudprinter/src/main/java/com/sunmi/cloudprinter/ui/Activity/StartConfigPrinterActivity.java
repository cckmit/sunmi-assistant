package com.sunmi.cloudprinter.ui.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.sunmi.cloudprinter.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:
 * Created by bruce on 2019/7/18.
 */
@EActivity(resName = "activity_start_config_sm_device")
public class StartConfigPrinterActivity extends BaseActivity {
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "iv_image")
    ImageView ivImage;
    @ViewById(resName = "tv_tip_1")
    TextView tvTip1;
    @ViewById(resName = "tv_tip_2")
    TextView tvTip2;
    @ViewById(resName = "tv_tip_3")
    TextView tvTip3;
    @ViewById(resName = "tv_tip_4")
    TextView tvTip4;
    @ViewById(resName = "ctv_privacy")
    CheckedTextView ctvPrivacy;

    @Extra
    int shopId;

    BluetoothAdapter btAdapter;

    BroadcastReceiver blueToothValueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1000);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        gotoPrinterConfig();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(R.string.str_title_printer_set);
        ivImage.setImageResource(R.mipmap.ic_device_config_printer);
        tvTip1.setText(R.string.str_config_tip_printer);
        tvTip2.setText(R.string.str_config_tip_printer_1);
        tvTip3.setText(R.string.str_config_tip_printer_3);
//        tvTip3.setText(Html.fromHtml(getString(R.string.str_config_tip_printer_2)));
//        tvTip4.setVisibility(View.VISIBLE);
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.white_40a, false);
        BluetoothClient mClient = new BluetoothClient(context);
        registerReceiver(blueToothValueReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Click(resName = "btn_start")
    public void nextClick(View v) {
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
        if (PermissionUtils.getLocationPermission(this))
            checkBtStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (blueToothValueReceiver != null)
            unregisterReceiver(blueToothValueReceiver);
//        if (mClient != null) {
//            mClient.disconnect(bleAddress);
//            mClient = null;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkBtStatus();
    }

    private void gotoPrinterConfig() {
        PrinterSearchActivity_.intent(this).shopId(shopId).start();
    }

    private void checkBtStatus() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            new CommonDialog.Builder(this).setTitle(R.string.str_prompt)
                    .setMessage(R.string.str_tip_start_blue)
                    .setCancelButton(R.string.sm_cancel)
                    .setConfirmButton(R.string.sm_enable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btAdapter.enable();
                        }
                    }).create().show();
        } else {
            gotoPrinterConfig();
        }
    }

}
