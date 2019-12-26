package com.sunmi.assistant.ui.activity.merchant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.assistant.R;


/**
 * 平台授权
 * Created by YangShiJie on 2019/6/27.
 */
public class AuthDialog extends Dialog {


    AuthDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    public static final class Builder {
        private Activity context;
        private String message;
        private OnClickListener cancelButtonClickListener, allowButtonClickListener;

        public Builder(Activity context) {
            this.context = context;
        }

        /**
         * 使用字符串设置对话框消息
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置确定
         */
        public Builder setAllowButton(DialogInterface.OnClickListener listener) {
            this.allowButtonClickListener = listener;
            return this;
        }

        /**
         * 设置取消
         */
        public Builder setCancelButton(DialogInterface.OnClickListener listener) {
            this.cancelButtonClickListener = listener;
            return this;
        }


        /**
         * 获取当前Activity所在的窗体
         *
         * @param dialog
         */
        private void getWindow(AuthDialog dialog) {
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity(Gravity.BOTTOM);
                // 屏幕宽度（像素）
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                //int height = dm.heightPixels;
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = width;
                dialogWindow.setAttributes(lp);
            }
        }

        /**
         * 创建自定义的对话框
         */
        public AuthDialog create() {
            AuthDialog dialog = new AuthDialog(context, R.style.BottomDialog);
            View layout = LayoutInflater.from(context).inflate(R.layout.dialog_auth_platform, null);
            dialog.setContentView(layout);
            getWindow(dialog);
            TextView tvAuthPlatform = layout.findViewById(R.id.tv_auth_platform);
            tvAuthPlatform.setText(message);
            Button btnAllow = layout.findViewById(R.id.btnAllow);
            Button btnCancel = layout.findViewById(R.id.btnCancel);
            View.OnClickListener listener = v -> {
                switch (v.getId()) {
                    case R.id.btnAllow://允许授权
                        dialog.cancel();
                        allowButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        break;
                    case R.id.btnCancel://暂不授权
                        dialog.cancel();
                        cancelButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        break;
                    default:
                        break;
                }
            };
            btnAllow.setOnClickListener(listener);
            btnCancel.setOnClickListener(listener);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

    }
}
