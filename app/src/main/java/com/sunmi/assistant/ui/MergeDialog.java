package com.sunmi.assistant.ui;

import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.UserMergeActivity_;

import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:合并账号确认
 * Created by bruce on 2019/1/9.
 */
public class MergeDialog {
    private CommonDialog commonDialog;
    private Context context;
    private String url;

    public MergeDialog(Context context, String url) {
        this.context = context;
        this.url = url;
        createDialog();
    }

    private void createDialog() {
        commonDialog = new CommonDialog.Builder(context)
                .setTitle(R.string.dialog_title_warm_tip)
                .setMessage(R.string.dialog_msg_merge)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm,
                        (dialog, which) -> gotoMerge()).create();
    }

    public void show() {
        commonDialog.show();
    }

    private void gotoMerge() {
        UserMergeActivity_.intent(context).url(url).start();
    }

}
