package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.commonlibrary.R;

public class CommonProgressDialog extends Dialog {

    public static final int STYLE_SPINNER = 0;

    public static final int STYLE_HORIZONTAL = 1;

    private ProgressBar mProgressBar;
    private TextView mTitleView;
    private String progressFormat;

    private CommonProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public void showWithOutTouchable(boolean touchable) {
        setProgress(0);
        setCanceledOnTouchOutside(touchable);
        show();
    }

    public void showWithOutTouchableCancelable(boolean touchable) {
        setCancelable(touchable);
        showWithOutTouchable(touchable);
    }

    public void setProgress(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
        if (mTitleView != null && progressFormat != null) {
            mTitleView.setText(String.format(progressFormat, progress));
        }
    }

    /**
     * builder class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String message;
        private String progressFormat;
        int mMax = 100;
        private boolean mIndeterminate;

        private int mProgressStyle = STYLE_HORIZONTAL;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setProgressStyle(int style) {
            mProgressStyle = style;
            return this;
        }

        public Builder setProgressFormat(int title) {
            this.progressFormat = (String) context.getText(title);
            return this;
        }

        public void setProgressFormat(String progressFormat) {
            this.progressFormat = progressFormat;
        }

        /**
         * 使用字符串设置对话框消息
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 使用资源设置对话框消息
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setMax(int max) {
            this.mMax = max;
            return this;
        }

        public Builder setIndeterminate(boolean indeterminate) {
            mIndeterminate = indeterminate;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public CommonProgressDialog create() {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化自定义的对话框主题
            final CommonProgressDialog dialog = new CommonProgressDialog(context, R.style.Son_dialog);
            View layout = inflater.inflate(R.layout.dialog_common_progress, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.progressFormat = progressFormat;
            dialog.mTitleView = layout.findViewById(R.id.tv_title);
            if (TextUtils.isEmpty(progressFormat)) {
                dialog.mTitleView.setVisibility(View.GONE);
            } else {
                dialog.mTitleView.setText(progressFormat);
            }

            if (!TextUtils.isEmpty(message)) {
                TextView tvMessage = layout.findViewById(R.id.tv_message);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            }
            dialog.mProgressBar = layout.findViewById(R.id.progress);
            dialog.mProgressBar.setMax(mMax);
            setIndeterminate(mIndeterminate);
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
