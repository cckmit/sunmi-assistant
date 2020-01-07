package com.sunmi.ipc.cash.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sunmi.ipc.R;

/**
 * 收银防损推广弹窗
 *
 * @author yinhui
 * @date 2019-12-31
 */
public class OpenLossPreventServiceDialog extends Dialog {

    private OpenLossPreventServiceDialog(Context context, int theme) {
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
        public OpenLossPreventServiceDialog create() {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化自定义的对话框主题
            final OpenLossPreventServiceDialog dialog = new OpenLossPreventServiceDialog(context, R.style.Son_dialog);
            View layout = inflater.inflate(R.layout.dialog_cash_loss_prevention_service, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            Button learnMore = layout.findViewById(R.id.btn_learn_more);
            learnMore.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });

            ImageView dismiss = layout.findViewById(R.id.iv_dismiss);
            dismiss.setOnClickListener(v -> dialog.cancel());

            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }

}