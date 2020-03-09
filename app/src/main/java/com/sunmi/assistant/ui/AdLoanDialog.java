package com.sunmi.assistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunmi.assistant.R;

/**
 * 收银防损推广弹窗
 *
 * @author yinhui
 * @date 2019-12-31
 */
public class AdLoanDialog extends Dialog {

    private AdLoanDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private OnClickListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setListener(OnClickListener l) {
            this.listener = l;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public AdLoanDialog create() {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化自定义的对话框主题
            final AdLoanDialog dialog = new AdLoanDialog(context, R.style.Son_dialog);
            View layout = inflater.inflate(R.layout.dialog_ad_loan, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            layout.findViewById(R.id.iv_go).setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });

            layout.findViewById(R.id.iv_close).setOnClickListener(v -> dialog.cancel());

            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }

}