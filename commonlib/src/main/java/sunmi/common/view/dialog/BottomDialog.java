package sunmi.common.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @date 2019-7-17
 */
public class BottomDialog extends Dialog {

    private BottomDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * builder class for creating a custom dialog
     */
    public static class Builder {
        protected Context context;
        private LayoutInflater inflater;

        private View contentView;
        private int contentLayoutId = -1;
        private ViewGroup.LayoutParams contentLayoutParams;

        private boolean isBtnBottom = false;
        private CharSequence title;
        private CharSequence cancelText;
        private CharSequence okText;
        private int bgColor = -1;
        private int titleTextColor = -1;
        private int cancelTextColor = -1;
        private int okTextColor = -1;
        private int cornerRadius = 12;
        private OnClickListener cancelClickListener, okClickListener;
        private boolean autoDismiss = true;

        public Builder(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public Builder setBtnBottom(boolean isBtnBottom) {
            this.isBtnBottom = isBtnBottom;
            return this;
        }

        /**
         * 设置背景颜色
         *
         * @param color 颜色值（非资源id）
         * @return 建造者
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            this.bgColor = color;
            return this;
        }

        /**
         * 设置标题
         *
         * @param textId 标题文本资源id
         * @return 建造者
         */
        public Builder setTitle(@StringRes int textId) {
            return setTitle(context.getText(textId));
        }

        /**
         * 设置标题
         *
         * @param title 标题文本字符串
         * @return 建造者
         */
        public Builder setTitle(CharSequence title) {
            return setTitle(title, -1);
        }

        /**
         * 设置标题和标题颜色
         *
         * @param textId    标题文本资源id
         * @param textColor 标题颜色值（非资源id）
         * @return 建造者
         */
        public Builder setTitle(@StringRes int textId, @ColorInt int textColor) {
            return setTitle(context.getText(textId), textColor);
        }

        /**
         * 设置标题和标题颜色
         *
         * @param title     标题文本字符串
         * @param textColor 标题颜色值（非资源id）
         * @return 建造者
         */
        public Builder setTitle(CharSequence title, @ColorInt int textColor) {
            this.title = title;
            this.titleTextColor = textColor;
            return this;
        }

        /**
         * 设置对话框内容主体
         *
         * @param v  View对象
         * @param lp 布局参数
         * @return 建造者
         */
        public Builder setContent(View v, ViewGroup.LayoutParams lp) {
            this.contentLayoutId = -1;
            this.contentView = v;
            this.contentLayoutParams = lp;
            return this;
        }

        /**
         * 设置对话框内容主体
         *
         * @param layoutId 内容布局id
         * @return 建造者
         */
        public Builder setContent(@LayoutRes int layoutId) {
            this.contentLayoutId = layoutId;
            this.contentView = null;
            return this;
        }

        /**
         * 设置取消按钮文本，默认事件为dismiss
         *
         * @param textId 文本资源id
         * @return 建造者
         */
        public Builder setCancelButton(@StringRes int textId) {
            return setCancelButton(context.getText(textId), (dialog, which) -> dialog.cancel());
        }

        /**
         * 设置取消按钮文本和事件
         *
         * @param textId   文本资源id
         * @param listener 事件回调
         * @return 建造者
         */
        public Builder setCancelButton(@StringRes int textId, OnClickListener listener) {
            return setCancelButton(context.getText(textId), listener);
        }

        /**
         * 设置取消按钮文本，默认事件为dismiss
         *
         * @param text 文本字符串
         * @return 建造者
         */
        public Builder setCancelButton(CharSequence text) {
            return setCancelButton(text, (dialog, which) -> dialog.cancel());
        }

        /**
         * 设置取消按钮文本和事件
         *
         * @param text     文本字符串
         * @param listener 事件回调
         * @return 建造者
         */
        public Builder setCancelButton(CharSequence text, OnClickListener listener) {
            return setCancelButton(text, -1, listener);
        }

        /**
         * 设置取消按钮文本和颜色，默认事件为dismiss
         *
         * @param textId    文本资源id
         * @param textColor 文本颜色值（非资源id）
         * @return 建造者
         */
        public Builder setCancelButton(@StringRes int textId, @ColorInt int textColor) {
            return setCancelButton(context.getText(textId), textColor, (dialog, which) -> dialog.cancel());
        }

        /**
         * 设置取消按钮文本，颜色和事件
         *
         * @param textId    文本资源id
         * @param textColor 文本颜色值（非资源id）
         * @param listener  事件回调
         * @return 建造者
         */
        public Builder setCancelButton(@StringRes int textId, @ColorInt int textColor, OnClickListener listener) {
            return setCancelButton(context.getText(textId), textColor, listener);
        }

        /**
         * 设置取消按钮文本和颜色，默认事件为dismiss
         *
         * @param text      文本字符串
         * @param textColor 文本颜色值（非资源id）
         * @return 建造者
         */
        public Builder setCancelButton(CharSequence text, @ColorInt int textColor) {
            return setCancelButton(text, textColor, (dialog, which) -> dialog.cancel());
        }

        /**
         * 设置取消按钮文本，颜色和事件
         *
         * @param text      文本字符串
         * @param textColor 文本颜色值（非资源id）
         * @param listener  事件回调
         * @return 建造者
         */
        public Builder setCancelButton(CharSequence text, @ColorInt int textColor, OnClickListener listener) {
            this.cancelText = text;
            this.cancelTextColor = textColor;
            this.cancelClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮文本，默认事件为dismiss
         *
         * @param textId 文本资源id
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId) {
            return setOkButton(context.getText(textId), (dialog, which) -> dialog.dismiss());
        }

        /**
         * 设置确定按钮文本和事件
         *
         * @param textId   文本资源id
         * @param listener 事件回调
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId, OnClickListener listener) {
            return setOkButton(context.getText(textId), listener);
        }

        /**
         * 设置确定按钮文本和事件，是否自动消失
         *
         * @param textId      文本资源id
         * @param listener    事件回调
         * @param autoDismiss 是否自动消失
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId, OnClickListener listener, boolean autoDismiss) {
            return setOkButton(context.getText(textId), listener, autoDismiss);
        }

        /**
         * 设置确定按钮文本，默认事件为dismiss
         *
         * @param text 文本字符串
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text) {
            return setOkButton(text, (dialog, which) -> dialog.dismiss());
        }

        /**
         * 设置确定按钮文本和事件
         *
         * @param text     文本字符串
         * @param listener 事件回调
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text, OnClickListener listener) {
            return setOkButton(text, -1, listener);
        }

        /**
         * 设置确定按钮文本和事件，是否自动消失
         *
         * @param text        文本字符串
         * @param listener    事件回调
         * @param autoDismiss 是否自动消失
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text, OnClickListener listener, boolean autoDismiss) {
            return setOkButton(text, -1, listener, autoDismiss);
        }

        /**
         * 设置确定按钮文本和颜色，默认事件为dismiss
         *
         * @param textId    文本资源id
         * @param textColor 文本颜色值（非资源id）
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId, @ColorInt int textColor) {
            return setOkButton(context.getText(textId), textColor, (dialog, which) -> dialog.dismiss());
        }

        /**
         * 设置确定按钮文本，颜色和事件
         *
         * @param textId    文本资源id
         * @param textColor 文本颜色值（非资源id）
         * @param listener  事件回调
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId, @ColorInt int textColor, OnClickListener listener) {
            return setOkButton(context.getText(textId), textColor, listener);
        }

        /**
         * 设置确定按钮文本，颜色和事件，是否自动消失
         *
         * @param textId      文本资源id
         * @param textColor   文本颜色值（非资源id）
         * @param listener    事件回调
         * @param autoDismiss 是否自动消失
         * @return 建造者
         */
        public Builder setOkButton(@StringRes int textId, @ColorInt int textColor, OnClickListener listener,
                                   boolean autoDismiss) {
            return setOkButton(context.getText(textId), textColor, listener, autoDismiss);
        }

        /**
         * 设置确定按钮文本和颜色，默认事件为dismiss
         *
         * @param text      文本字符串
         * @param textColor 文本颜色值（非资源id）
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text, @ColorInt int textColor) {
            return setOkButton(text, textColor, (dialog, which) -> dialog.dismiss());
        }

        /**
         * 设置确定按钮文本，颜色和事件
         *
         * @param text      文本字符串
         * @param textColor 文本颜色值（非资源id）
         * @param listener  事件回调
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text, @ColorInt int textColor, OnClickListener listener) {
            this.okText = text;
            this.okTextColor = textColor;
            this.okClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮文本，颜色和事件，是否自动消失
         *
         * @param text        文本字符串
         * @param textColor   文本颜色值（非资源id）
         * @param listener    事件回调
         * @param autoDismiss 是否自动消失
         * @return 建造者
         */
        public Builder setOkButton(CharSequence text, @ColorInt int textColor, OnClickListener listener, boolean autoDismiss) {
            this.okText = text;
            this.okTextColor = textColor;
            this.okClickListener = listener;
            this.autoDismiss = autoDismiss;
            return this;
        }

        public Builder setCornerRadius(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public Dialog create() {
            final Dialog dialog = new BottomDialog(context, R.style.BottomDialog);
            @SuppressLint("InflateParams")
            View layout = inflater.inflate(R.layout.dialog_bottom, null);
            int width = context.getResources().getDisplayMetrics().widthPixels;
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    width, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (bgColor != -1) {
                layout.setBackgroundColor(bgColor);
            }

            // 设置Title
            setupTitle(layout);

            // 设置Btn
            setupBtn(dialog, layout);

            // 设置圆角
            setupCornerRadius(layout);

            // 设置内容Layout
            FrameLayout content = layout.findViewById(R.id.layout_dialog_content);
            setupContent(dialog, content);

            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.BottomDialog_Animation);
            }
            return dialog;
        }

        /**
         * 设置对话框标题
         *
         * @param layout 对话框根布局
         */
        protected void setupTitle(View layout) {
            TextView tvTitle = layout.findViewById(R.id.tv_dialog_title);
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setText(title);
                if (titleTextColor != -1) {
                    tvTitle.setTextColor(titleTextColor);
                }
            }
        }

        protected void setupCornerRadius(View layout) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            float radius = CommonHelper.dp2px(context, cornerRadius);
            drawable.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});
            if (bgColor != -1) {
                drawable.setColor(bgColor);
            } else {
                drawable.setColor(ContextCompat.getColor(context, R.color.c_white));
            }
            layout.setBackground(drawable);
        }

        /**
         * 设置对话框内容
         *
         * @param content 对话框内容布局容器
         */
        protected void setupContent(final Dialog dialog, FrameLayout content) {
            if (contentView != null) {
                content.addView(contentView, contentLayoutParams);
            } else if (contentLayoutId != -1) {
                inflater.inflate(contentLayoutId, content);
            }
        }

        /**
         * 设置确认/取消按钮样式
         *
         * @param dialog 对话框
         * @param layout 对话框根布局
         */
        protected void setupBtn(final Dialog dialog, View layout) {
            Button btnCancel;
            Button btnOk;
            if (isBtnBottom) {
                layout.findViewById(R.id.layout_dialog_btn).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.btn_dialog_ok_top).setVisibility(View.GONE);
                layout.findViewById(R.id.btn_dialog_cancel_top).setVisibility(View.GONE);
                btnCancel = layout.findViewById(R.id.btn_dialog_cancel_bottom);
                btnOk = layout.findViewById(R.id.btn_dialog_ok_bottom);
            } else {
                layout.findViewById(R.id.layout_dialog_btn).setVisibility(View.GONE);
                btnOk = layout.findViewById(R.id.btn_dialog_ok_top);
                btnOk.setVisibility(View.VISIBLE);
                btnCancel = layout.findViewById(R.id.btn_dialog_cancel_top);
                btnCancel.setVisibility(View.VISIBLE);
            }

            // 设置Cancel按钮
            if (TextUtils.isEmpty(cancelText)) {
                btnCancel.setVisibility(View.GONE);
            } else {
                btnCancel.setText(cancelText);
                if (cancelTextColor != -1) {
                    btnCancel.setTextColor(cancelTextColor);
                }
                if (cancelClickListener != null) {
                    btnCancel.setOnClickListener(v -> {
                        dialog.cancel();
                        cancelClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    });
                }
            }

            // 设置ok按钮
            if (TextUtils.isEmpty(okText)) {
                btnOk.setVisibility(View.GONE);
            } else {
                btnOk.setText(okText);
                if (okTextColor != -1) {
                    btnOk.setTextColor(okTextColor);
                }
                if (okClickListener != null) {
                    btnOk.setOnClickListener(v -> {
                        if (autoDismiss) {
                            dialog.dismiss();
                        }
                        okClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    });
                }
            }
        }
    }

}