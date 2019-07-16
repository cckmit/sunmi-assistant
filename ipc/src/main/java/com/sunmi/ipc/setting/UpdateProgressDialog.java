package com.sunmi.ipc.ipcset;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunmi.ipc.R;

/**
 * Created by YangShiJie on 2019/7/15.
 */
public class UpdateProgressDialog extends Dialog {
    @SuppressLint("StaticFieldLeak")
    private static TextView tvProgress;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar showProgress;

    UpdateProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void canceledOnTouchOutside(boolean touchable) {
        this.setCanceledOnTouchOutside(touchable);
        this.show();
    }

    public void progressDismiss() {
        this.dismiss();
        if (tvProgress != null) {
            tvProgress = null;
        }
        if (showProgress != null) {
            showProgress = null;
        }
    }

    public void setText(Context context, int progress) {
        tvProgress.setText(context.getString(R.string.ipc_setting_dialog_upgrade_progress, String.valueOf(progress)));
        showProgress.setProgress(progress);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 创建自定义的对话框
         */
        public UpdateProgressDialog create() {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化自定义的对话框主题
            final UpdateProgressDialog dialog = new UpdateProgressDialog(context, R.style.Son_dialog);
            View layout = inflater.inflate(R.layout.dialog_upgrade_progress, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            tvProgress = layout.findViewById(R.id.tv_progress);
            showProgress = layout.findViewById(R.id.pBar_progress);
            tvProgress.setText(context.getString(R.string.ipc_setting_dialog_upgrade_progress, "0"));
            showProgress.setProgress(0);

            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}
