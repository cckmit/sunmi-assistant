package com.sunmi.cloudprinter.ui.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.contract.ReadAndWriteContract;
import com.sunmi.cloudprinter.presenter.ReadAndWritePresenter;
import com.sunmi.cloudprinter.ui.adaper.RouterListAdapter;
import com.sunmi.cloudprinter.utils.ByteUtils;
import com.sunmi.cloudprinter.utils.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.ProgressDialog;

@EActivity(resName = "activity_read_and_write")
public class ReadAndWriteActivity extends BaseMvpActivity<ReadAndWritePresenter> implements ReadAndWriteContract.View {
    @ViewById(resName = "rv_router")
    RecyclerView rvRouter;
    @ViewById(resName = "pb_sending")
    ProgressBar progressBar;

    @Extra
    String bleAddress;

    @Extra
    Bundle bundle;

    private BluetoothClient mClient;
    private ProgressDialog dialog;
    private Dialog routerDialog;
    private String pwd = "";
    private String sn = "123456";

    private List<Router> routers = new ArrayList<>();
    private RouterListAdapter adapter;
    private byte version = 100;

    @AfterViews
    protected void init() {
        StatusBarUtils.StatusBarLightMode(this);//状态栏
        mPresenter = new ReadAndWritePresenter();
        mPresenter.attachView(this);
        mClient = new BluetoothClient(context);
        showProgressDialog();
        connectBle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvRouter.setLayoutManager(layoutManager);
        adapter = new RouterListAdapter(routers);
        adapter.setOnItemClickListener(new RouterListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<Router> data) {
                showMessageDialog(data.get(position));
            }
        });
        rvRouter.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClient != null) {
            mClient.disconnect(bleAddress);
            mClient = null;
        }
    }

    @UiThread
    public void connectBle() {
        if (mClient == null) return;
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();
        mClient.connect(bleAddress, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                LogCat.e(TAG, "connect   = " + code);
                if (code == 0) {
                    closeProgressDialog();
                    onInitMtu();
                }
            }
        });
    }

    @UiThread
    @Override
    public void initRouter(Router router) {
        routers.add(router);
        adapter.notifyDataSetChanged();

    }

    private void showMessageDialog(final Router router) {
        routerDialog = new Dialog(context, R.style.Son_dialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_router_message, null);
        final ClearableEditText etPassword = view.findViewById(R.id.etPassword);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSure = view.findViewById(R.id.btnSure);
        RelativeLayout rlPassword = view.findViewById(R.id.rlPassword);
        if (!router.isHasPwd()) {
            rlPassword.setVisibility(View.GONE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerDialog.dismiss();
                routerDialog = null;
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (router.isHasPwd()) {
                    pwd = etPassword.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        shortTip(R.string.hint_input_router_pwd);
                        return;
                    }
                    router.setPwd(pwd);
                    byte[] password = ByteUtils.String2Byte64(pwd);
                    onSendMessage(Utility.cmdConnectWifi(version, router.getEssid(), password));

                } else {
                    byte[] password = ByteUtils.getNoneByte64();
                    onSendMessage(Utility.cmdConnectWifi(version, router.getEssid(), password));
                }
            }
        });
        routerDialog.setContentView(view);
        routerDialog.setCancelable(false);
        routerDialog.show();
    }


    @UiThread
    @Override
    public void onInitNotify() {
        if (mClient == null) return;
        mClient.notify(bleAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                Log.e(TAG, "onNotify value = " + value.length);
                for (byte data : value) {
                    Log.e(TAG, "onNotify data = " + data);
                }
                mPresenter.onNotify(value,version);
            }

            @Override
            public void onResponse(int code) {
                Log.e(TAG, "initNotify onResponse, code = " + code);
                if (code == 0) {
                    mPresenter.initNotifySuccess(version);
                }else {
                    mPresenter.initNotifyFailed();
                }
            }
        });
    }

    @UiThread
    @Override
    public void onInitMtu() {
        if (mClient == null) return;
        mClient.requestMtu(bleAddress, 256, new BleMtuResponse() {
            @Override
            public void onResponse(int code, Integer data) {
                if (code == 0) {
                    mPresenter.initMtuSuccess();
                } else {
                  mPresenter.initMtuFailed();
                }
            }
        });
    }

    @UiThread
    @Override
    public void onSendMessage(final byte[] data) {
        if (mClient == null) return;
        mClient.write(bleAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, data, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == 0) {
                    shortTip("send success");
                    Log.e(TAG, "send Message:" + data[5]);
                    if (data[5] == 8) {
                        routerDialog.dismiss();
                        PrinterManageActivity_.intent(context).sn(sn).userId(bundle.getString("userId"))
                                .merchantId(bundle.getString("merchantId")).start();
                    }
                }
            }
        });
    }

    @UiThread
    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSn(String sn) {
        this.sn=sn;
    }

    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setContent(getString(R.string.str_set_link));
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
