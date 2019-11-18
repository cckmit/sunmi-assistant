package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 *
 */
public class LoadingDialog extends Dialog {

    private TextView tvLoading;

    private int mTextColorLightDialog;
    private int mTextColorDarkDialog;

    private boolean mIsDark;
    private String mContent;
    private int mContentColor;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        mTextColorLightDialog = ContextCompat.getColor(getContext(), R.color.text_normal);
        mTextColorDarkDialog = ContextCompat.getColor(getContext(), R.color.c_white);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLoadingLight();
    }

    public void setLoadingLight() {
        if (!mIsDark) {
            return;
        }
        mIsDark = false;
        setContentView(R.layout.dialog_loading);
        tvLoading = findViewById(R.id.tv_loading);
        updateContent();
    }

    public void setLoadingDark() {
        if (mIsDark) {
            return;
        }
        mIsDark = true;
        setContentView(R.layout.dialog_loading_dark);
        tvLoading = findViewById(R.id.tv_loading);
        updateContent();
    }

    public void setContent(String content) {
        if (tvLoading == null) {
            return;
        }
        mContent = content;
        mContentColor = 0;
        updateContent();
    }

    public void setContent(String content, @ColorInt int color) {
        if (tvLoading == null) {
            return;
        }
        mContent = content;
        mContentColor = color;
        updateContent();
    }

    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateContent() {
        if (TextUtils.isEmpty(mContent)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            int color = mContentColor;
            if (color == 0) {
                color = mIsDark ? mTextColorDarkDialog : mTextColorLightDialog;
            }
            if (tvLoading.getCurrentTextColor() != color) {
                tvLoading.setTextColor(color);
            }
            tvLoading.setText(mContent);
        }
    }

}
