package com.sunmi.ipc.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
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

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.ListDialog;

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

    private List<CharSequence> list = new ArrayList<>();
    private AnimationDrawable drawable;

    @RouterAnno(
            path = RouterConfig.Ipc.IPC_START_CONFIG
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), IpcStartConfigActivity_.class);
        return intent;
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (ipcType == CommonConstants.TYPE_IPC_FS) {
            ivIpc.setBackgroundResource(R.drawable.ipc_config_fs);
        } else if (ipcType == CommonConstants.TYPE_IPC_SS) {
            ivIpc.setBackgroundResource(R.drawable.ipc_config_ss);
        }
        drawable = (AnimationDrawable) ivIpc.getBackground();
        if (!drawable.isRunning()) {
            drawable.start();
        }
        list.add(getString(R.string.tip_config_ipc_dialog1));
        list.add(getString(R.string.tip_config_ipc_dialog2));
        list.add(Html.fromHtml(getString(R.string.tip_config_ipc_dialog3)));
        tvConfigTip.setText(Html.fromHtml(getString(R.string.str_config_tip_ipc_1)));
        tvIndicator.setVisibility(View.VISIBLE);
        ctvPrivacy.setVisibility(View.VISIBLE);
        tvIndicator.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvIndicator.getPaint().setAntiAlias(true);
        ViewUtils.setPrivacy(this, ctvPrivacy, com.commonlibrary.R.color.white_40a, false);
        btnStart.setEnabled(ctvPrivacy.isChecked());
        ctvPrivacy.setOnClickListener(v -> {
            ctvPrivacy.toggle();
            btnStart.setEnabled(ctvPrivacy.isChecked());
        });
    }

    @Click(resName = "btn_start")
    public void nextClick() {
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
        IpcChooseInternetActivity_.intent(context).ipcType(ipcType).start();
    }

    @Click(resName = "tv_indicator_light")
    public void indicatorClick() {
        final CommonListAdapter adapter = new CommonListAdapter<CharSequence>(context, R.layout.item_ipc_config_dialog, list) {
            @Override
            public void convert(ViewHolder holder, CharSequence charSequence) {
                holder.setText(R.id.tv_content, charSequence);
            }
        };
        new ListDialog.Builder<CommonListAdapter>(context)
                .setTitle(R.string.str_ipc_config_dialog_title)
                .setItemDecoration(false)
                .setAdapter(adapter)
                .setConfirmButton(R.string.str_confirm)
                .create().show();
    }

    @Override
    protected void onDestroy() {
        if (drawable.isRunning()) {
            drawable.stop();
        }
        super.onDestroy();
    }
}
