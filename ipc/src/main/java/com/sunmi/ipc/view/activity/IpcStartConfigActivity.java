package com.sunmi.ipc.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-20.
 */
@EActivity(resName = "activity_ipc_start_config")
public class IpcStartConfigActivity extends BaseActivity {

    @ViewById(resName = "tv_config_tip")
    TextView tvConfigTip;
    @ViewById(resName = "tv_indicator_light")
    TextView tvIndicator;
    @ViewById(resName = "iv_ipc")
    ImageView ivIpc;
    @ViewById(resName = "ctv_privacy")
    CheckedTextView ctvPrivacy;
    @ViewById(resName = "btn_start")
    Button btnStart;

    @Extra
    int ipcType;

    @RouterAnno(
            path = RouterConfig.Ipc.IPC_START_CONFIG
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), IpcStartConfigActivity_.class);
        return intent;
    }

    @AfterViews
    void init(){
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (ipcType == CommonConstants.TYPE_IPC_FS){
            ivIpc.setBackground(ContextCompat.getDrawable(context,R.drawable.ipc_config_fs));
        }else if (ipcType ==CommonConstants.TYPE_IPC_SS){
            ivIpc.setBackground(ContextCompat.getDrawable(context,R.drawable.ipc_config_ss));
        }
        AnimationDrawable animationDrawable = (AnimationDrawable)ivIpc.getBackground();
        if (!animationDrawable.isRunning()){
            animationDrawable.start();
        }
        tvConfigTip.setText(Html.fromHtml(getString(R.string.str_config_tip_ipc_1)));
        tvIndicator.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvIndicator.getPaint().setAntiAlias(true);
    }

    @Click(resName = "btn_start")
    public void nextClick() {
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
    }

    @Click(resName = "tv_indicator_light")
    public void indicatorClick(){
        new CommonDialog.Builder(context)
                .setTitle("您可以尝试以下操作")
                .setMessage(R.string.tip_config_ipc_dialog1)
                .setConfirmButton(R.string.str_confirm)
                .create().show();
    }


}
