package com.sunmi.assistant.ui.activity.setting;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import sunmi.common.rpc.http.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;

/**
 * 设置
 */
@EActivity(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    @ViewById(R.id.tvVersion)
    TextView tvVersion;
    @ViewById(R.id.tvCash)
    TextView tvCash;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
        tvVersion.setText("版本" + CommonHelper.getAppVersionName(this));
    }

    @Click({R.id.rlAccountSafe, R.id.rlAbout, R.id.rlClearCash, R.id.btnLogout})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.rlAccountSafe://账号安全
                SecurityActivity_.intent(context).start();
                break;
            case R.id.rlAbout://关于
                AboutActivity_.intent(context).start();
                break;
            case R.id.rlClearCash://缓存
                showDialog(this, 0);
                break;
            case R.id.btnLogout://退出
                CommonUtils.trackCommonEvent(context, "settingLogout",
                        "主页_设置_退出登录", Constants.EVENT_MY_INFO);
                showDialog(this, 1);
                break;
        }
    }

    public void showDialog(final Context getActivity, final int Type) {
        final Dialog dialog = new Dialog(getActivity, R.style.BottomDialog);
        View inflate = LayoutInflater.from(getActivity).inflate(R.layout.dialog_factory_reset, null);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 屏幕宽度（像素）
        WindowManager wm = (WindowManager) getActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        //int height = dm.heightPixels;
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        dialogWindow.setAttributes(lp);

        TextView textTip = inflate.findViewById(R.id.tvTip);
        TextView textReCover = inflate.findViewById(R.id.tvSure);
        TextView textCancel = inflate.findViewById(R.id.tvCancel);
        if (Type == 0) {
            textTip.setText(R.string.msg_clear_cache_confirm);
            textReCover.setText(R.string.str_confirm);
        } else if (Type == 1) {
            textTip.setText(R.string.msg_quit_confirm);
            textReCover.setText(R.string.str_confirm);
        }
        textReCover.setTextColor(getResources().getColor(R.color.common_orange));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.tvSure:
                        if (Type == 0) {
                            tvCash.setText("0 M");
                        } else if (Type == 1) {
                            logout();
                        }
                        break;
                }
            }
        };
        textReCover.setOnClickListener(listener);
        textCancel.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.show();//显示对话框
    }

    /**
     * 退出登录
     */
    private void logout() {
        CloudApi.loginOut(new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    shortTip(R.string.tip_logout_success);
                    CommonUtils.logout();
                    LoginActivity_.intent(context).start();
                    finish();
                    System.gc();
                } else {
                    shortTip(R.string.tip_logout_fail);
                }
            }
        });
    }

}
