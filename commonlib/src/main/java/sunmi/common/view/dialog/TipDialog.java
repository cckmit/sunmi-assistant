package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 * Description:
 * Created by bruce on 2020/3/20.
 */
public class TipDialog extends Dialog {

    private TipDialog(Context context, int theme) {
        super(context, theme);
    }

    public void showWithOutTouchable(boolean touchable) {
        this.setCanceledOnTouchOutside(touchable);
        this.show();
    }

    /**
     * builder class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String tipContent;
        private Drawable tipDrawable;
        private int tipDrawableRes;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置tip内容显示
         */
        public Builder tipContent(int content) {
            this.tipContent = (String) context.getText(content);
            return this;
        }

        /**
         * 设置tip内容显示
         */
        public Builder tipContent(String content) {
            this.tipContent = content;
            return this;
        }

        /**
         * 设置tip图片
         */
        public Builder tipDrawable(@Nullable Drawable drawable) {
            tipDrawable = drawable;
            return this;
        }

        /**
         * 设置tip图片
         */
        public Builder tipDrawable(@DrawableRes int drawable) {
            tipDrawableRes = drawable;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public TipDialog create() {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化自定义的对话框主题
            final TipDialog dialog = new TipDialog(context, R.style.Son_dialog);
            View layout = inflater.inflate(R.layout.dialog_tip, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView tvContent = layout.findViewById(R.id.tv_content);
            if (!TextUtils.isEmpty(tipContent)) {
                tvContent.setText(tipContent);
                tvContent.setVisibility(View.VISIBLE);
            }
            ImageView iv = layout.findViewById(R.id.iv_tip);
            if (tipDrawable != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageDrawable(tipDrawable);
            } else if (tipDrawableRes > 0) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(tipDrawableRes);
            }

            layout.findViewById(R.id.iv_close).setOnClickListener(v -> dialog.cancel());
            dialog.setContentView(layout);
            dialog.setCancelable(true);
            return dialog;
        }
    }

}
