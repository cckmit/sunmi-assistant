package com.sunmi.ipc.ipcset;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

/**
 * Created by YangShiJie on 2019/7/16.
 */
@EActivity(resName = "ipc_activity_wifi")
public class IpcSettingWiFiActivity extends BaseActivity {
    @ViewById(resName = "tv_wifi_name")
    TextView tvWifiNme;
    @ViewById(resName = "tv_status")
    TextView tvStatus;
    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;

    private TextView tvProgress;
    private Dialog connectDialog;

    private Timer timer = null;
    private TimerTask timerTask = null;
    private int countdown = 30;

    //开启计时
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                showDownloadProgress();
            }
        }, 0, 1000);
    }

    @UiThread
    void showDownloadProgress() {
        countdown--;
        tvProgress.setText(getString(R.string.ipc_setting_dialog_wifi_progress, countdown));
        if (countdown == 0) {
            stopTimer();
        }
    }

    // 停止计时
    private void stopTimer() {
        countdown = 30;
        if (connectDialog != null) {
            connectDialog.dismiss();
            connectDialog = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        tvWifiNme.setText("wifi name");
        tvStatus.setText(R.string.ipc_setting_tip_wifi_discovery);

        showWifiList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void showWifiList() {
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i + "66666");
        }
        recyclerView.setAdapter(new CommonListAdapter<String>(context,
                R.layout.ipc_item_wifi, list) {
            @Override
            public void convert(ViewHolder holder, final String bean) {
                TextView tvName = holder.getView(R.id.tv_wifi_name);
                tvName.setText(list.get(holder.getAdapterPosition()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputPasswordDialog();
                    }
                });
            }
        });
    }

    /**
     * 输入wifi密码
     */
    private void inputPasswordDialog() {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_wifi_input_pwd)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_sure, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        dialog.dismiss();
                        startTimer();
                        connectWifiProgress();
                    }
                }).create().show();
    }

    /**
     * wifi密码error
     */
    private void passwordErrorDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_wifi_pwd_error)
                .setMessage(getString(R.string.ipc_setting_dialog_wifi_pwd_error_content))
                .setConfirmButton(R.string.ipc_setting_dialog_wifi_pwd_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelButton(R.string.str_close).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 网络异常
     */
    private void netExceptionDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_wifi_net_error)
                .setMessage(getString(R.string.ipc_setting_dialog_wifi_net_error_content))
                .setConfirmButton(R.string.str_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelButton(R.string.str_close).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 连接wifi中
     */
    private void connectWifiProgress() {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        connectDialog = new Dialog(context, R.style.Son_dialog);
        View layout = inflater.inflate(R.layout.dialog_connect_wifi_progress, null);
        tvProgress = layout.findViewById(R.id.tv_countDown);
        tvProgress.setText(getString(R.string.ipc_setting_dialog_wifi_progress, 0));

        connectDialog.setContentView(layout);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.show();
    }

}
