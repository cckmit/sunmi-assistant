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

//    private TextView mTitleView;
//    private TextView mMessageView;
//
//    private int mProgressStyle = STYLE_SPINNER;
//    private String mProgressNumberFormat;
//    private NumberFormat mProgressPercentFormat;
//
//    private int mMax;
//    private int mProgressVal;
//    private int mSecondaryProgressVal;
//    private int mIncrementBy;
//    private CharSequence mMessage;
//    private boolean mIndeterminate;
//
//    private boolean mHasStarted;
//    private Handler mViewUpdateHandler;
//
//    public CommonProgressDialog(Context context) {
//        super(context);
//        initFormats();
//    }
//
//    public CommonProgressDialog(Context context, int theme) {
//        super(context, theme);
//        initFormats();
//    }
//
//    private void initFormats() {
//        mProgressNumberFormat = "%1d/%2d";
//        mProgressPercentFormat = NumberFormat.getPercentInstance();
//        mProgressPercentFormat.setMaximumFractionDigits(0);
//    }
//
//    public static ProgressDialog show(Context context, CharSequence title,
//                                      CharSequence message) {
//        return show(context, title, message, false);
//    }
//
//    public static ProgressDialog show(Context context, CharSequence title,
//                                      CharSequence message, boolean indeterminate) {
//        return show(context, title, message, indeterminate, false, null);
//    }
//
//    public static ProgressDialog show(Context context, CharSequence title,
//                                      CharSequence message, boolean indeterminate, boolean cancelable) {
//        return show(context, title, message, indeterminate, cancelable, null);
//    }
//
//    public static ProgressDialog show(Context context, CharSequence title,
//                                      CharSequence message, boolean indeterminate,
//                                      boolean cancelable, OnCancelListener cancelListener) {
//        ProgressDialog dialog = new ProgressDialog(context);
//        dialog.setTitle(title);
//        dialog.setMessage(message);
//        dialog.setIndeterminate(indeterminate);
//        dialog.setCancelable(cancelable);
//        dialog.setOnCancelListener(cancelListener);
//        dialog.show();
//        return dialog;
//    }
//
//    @SuppressLint("HandlerLeak")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        View view;
//        if (mProgressStyle == STYLE_HORIZONTAL) {
//            mViewUpdateHandler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//
//                    /* Update the number and percent */
//                    int progress = mProgressBar.getProgress();
//                    int max = mProgressBar.getMax();
//                    if (mProgressNumberFormat != null) {
//                        String format = mProgressNumberFormat;
//                        mTitleView.setText(String.format(format, progress, max));
//                    } else {
//                        mTitleView.setText("");
//                    }
//                }
//            };
//            view = inflater.inflate(R.layout.dialog_common_progress, null);
//        } else {
//            view = inflater.inflate(R.layout.dialog_common_progress, null);
//        }
//        mTitleView = view.findViewById(R.id.tv_title);
//        mMessageView = view.findViewById(R.id.tv_message);
//        mProgressBar = view.findViewById(R.id.progress);
//        setContentView(view);
//
//        if (mMax > 0) {
//            setMax(mMax);
//        }
//        if (mProgressVal > 0) {
//            setProgress(mProgressVal);
//        }
//        if (mSecondaryProgressVal > 0) {
//            setSecondaryProgress(mSecondaryProgressVal);
//        }
//        if (mIncrementBy > 0) {
//            incrementProgressBy(mIncrementBy);
//        }
//        if (mMessage != null) {
//            setMessage(mMessage);
//        }
//        setIndeterminate(mIndeterminate);
//        onProgressChanged();
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mHasStarted = true;
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mHasStarted = false;
//    }
//
//    /**
//     * Sets the current progress.
//     *
//     * @param value the current progress, a value between 0 and {@link #getMax()}
//     * @see ProgressBar#setProgress(int)
//     */
//    public void setProgress(int value) {
//        if (mHasStarted) {
//            mProgressBar.setProgress(value);
//            onProgressChanged();
//        } else {
//            mProgressVal = value;
//        }
//    }
//
//    /**
//     * Sets the secondary progress.
//     *
//     * @param secondaryProgress the current secondary progress, a value between 0 and
//     *                          {@link #getMax()}
//     * @see ProgressBar#setSecondaryProgress(int)
//     */
//    public void setSecondaryProgress(int secondaryProgress) {
//        if (mProgressBar != null) {
//            mProgressBar.setSecondaryProgress(secondaryProgress);
//            onProgressChanged();
//        } else {
//            mSecondaryProgressVal = secondaryProgress;
//        }
//    }
//
//    /**
//     * Gets the current progress.
//     *
//     * @return the current progress, a value between 0 and {@link #getMax()}
//     */
//    public int getProgress() {
//        if (mProgressBar != null) {
//            return mProgressBar.getProgress();
//        }
//        return mProgressVal;
//    }
//
//    /**
//     * Gets the current secondary progress.
//     *
//     * @return the current secondary progress, a value between 0 and {@link #getMax()}
//     */
//    public int getSecondaryProgress() {
//        if (mProgressBar != null) {
//            return mProgressBar.getSecondaryProgress();
//        }
//        return mSecondaryProgressVal;
//    }
//
//    /**
//     * Gets the maximum allowed progress value. The default value is 100.
//     *
//     * @return the maximum value
//     */
//    public int getMax() {
//        if (mProgressBar != null) {
//            return mProgressBar.getMax();
//        }
//        return mMax;
//    }
//
//    /**
//     * Sets the maximum allowed progress value.
//     */
//    public void setMax(int max) {
//        if (mProgressBar != null) {
//            mProgressBar.setMax(max);
//            onProgressChanged();
//        } else {
//            mMax = max;
//        }
//    }
//
//    /**
//     * Increments the current progress value.
//     *
//     * @param diff the amount by which the current progress will be incremented,
//     *             up to {@link #getMax()}
//     */
//    public void incrementProgressBy(int diff) {
//        if (mProgressBar != null) {
//            mProgressBar.incrementProgressBy(diff);
//            onProgressChanged();
//        } else {
//            mIncrementBy += diff;
//        }
//    }
//
//    /**
//     * Change the indeterminate mode for this ProgressDialog. In indeterminate
//     * mode, the progress is ignored and the dialog shows an infinite
//     * animation instead.
//     *
//     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
//     * is always indeterminate and will ignore this setting.</p>
//     *
//     * @param indeterminate true to enable indeterminate mode, false otherwise
//     * @see #setProgressStyle(int)
//     */
//    public void setIndeterminate(boolean indeterminate) {
//        if (mProgressBar != null) {
//            mProgressBar.setIndeterminate(indeterminate);
//        } else {
//            mIndeterminate = indeterminate;
//        }
//    }
//
//    /**
//     * Whether this ProgressDialog is in indeterminate mode.
//     *
//     * @return true if the dialog is in indeterminate mode, false otherwise
//     */
//    public boolean isIndeterminate() {
//        if (mProgressBar != null) {
//            return mProgressBar.isIndeterminate();
//        }
//        return mIndeterminate;
//    }
//
//    public void setMessage(CharSequence message) {
//        if (mMessageView != null) {
//            mMessageView.setVisibility(View.VISIBLE);
//            mMessageView.setText(message);
//        } else {
//            mMessage = message;
//        }
//    }
//
//    /**
//     * Sets the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
//     * {@link #STYLE_HORIZONTAL}. The default is {@link #STYLE_SPINNER}.
//     *
//     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
//     * is always indeterminate and will ignore the {@link #setIndeterminate(boolean)
//     * indeterminate} setting.</p>
//     *
//     * @param style the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
//     *              {@link #STYLE_HORIZONTAL}
//     */
//    public void setProgressStyle(int style) {
//        mProgressStyle = style;
//    }
//
//    /**
//     * Change the format of the small text showing current and maximum units
//     * of progress.  The default is "%1d/%2d".
//     * Should not be called during the number is progressing.
//     *
//     * @param format A string passed to {@link String#format String.format()};
//     *               use "%1d" for the current number and "%2d" for the maximum.  If null,
//     *               nothing will be shown.
//     */
//    public void setProgressNumberFormat(String format) {
//        mProgressNumberFormat = format;
//        onProgressChanged();
//    }
//
//    /**
//     * Change the format of the small text showing the percentage of progress.
//     * The default is
//     * {@link NumberFormat#getPercentInstance() NumberFormat.getPercentageInstnace().}
//     * Should not be called during the number is progressing.
//     *
//     * @param format An instance of a {@link NumberFormat} to generate the
//     *               percentage text.  If null, nothing will be shown.
//     */
//    public void setProgressPercentFormat(NumberFormat format) {
//        mProgressPercentFormat = format;
//        onProgressChanged();
//    }
//
//    private void onProgressChanged() {
//        if (mProgressStyle == STYLE_HORIZONTAL) {
//            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
//                mViewUpdateHandler.sendEmptyMessage(0);
//            }
//        }
//    }

}
