package com.sunmi.ipc.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
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
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.activity.ProtocolActivity_;
import sunmi.common.view.dialog.ListDialog;

import static sunmi.common.view.activity.ProtocolActivity.USER_IPC_PROTOCOL;

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
    @Extra
    int source;

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
        initProtocol();
        btnStart.setEnabled(ctvPrivacy.isChecked());
        ctvPrivacy.setOnClickListener(v -> {
            ctvPrivacy.toggle();
            btnStart.setEnabled(ctvPrivacy.isChecked());
        });
    }


    private void initProtocol() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String protocol = getString(R.string.str_ipc_protocol);
        builder.append(ctvPrivacy.getText());
        int len = builder.length();
        builder.append(protocol);
        TextPaint tp = new TextPaint();
        tp.linkColor = R.color.white_40a;
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ProtocolActivity_.intent(context)
                        .protocolType(USER_IPC_PROTOCOL).start();
                overridePendingTransition(com.commonlibrary.R.anim.activity_open_down_up, 0);
            }
        };
        span.updateDrawState(tp);
        builder.setSpan(span, len, len + protocol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ctvPrivacy.setText(builder);
        ctvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        ctvPrivacy.setText(builder);
    }

    @Click(resName = "btn_start")
    public void nextClick() {
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
        IpcChooseInternetActivity_.intent(context).ipcType(ipcType).source(source).start();
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
                .setListHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
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
