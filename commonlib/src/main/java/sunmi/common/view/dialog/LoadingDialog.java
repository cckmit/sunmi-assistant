package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 * 全页面的Loading Dialog，支持橙色和暗色。
 * 其中橙色用于空白页面的Loading
 * 暗色用于已有内容页面的Loading
 *
 * @author yinhui
 * @date 2019-11-18
 */
public class LoadingDialog extends Dialog {

    public static final int MODE_ORANGE = 0;
    public static final int MODE_DARK = 1;
    public static final int MODE_WHITE = 2;

    private LinearLayout llContainer;
    private ProgressBar pbLoading;
    private TextView tvLoading;

    private static Drawable sLoadingOrange;
    private static Drawable sLoadingWhite;
    private static int mTextColorBlack;
    private static int mTextColorWhite;

    private int mMode;
    private String mContent;
    private int mColor;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        if (sLoadingOrange == null || sLoadingWhite == null) {
            int loadingSize = (int) context.getResources().getDimension(R.dimen.dp_32);
            sLoadingOrange = ContextCompat.getDrawable(context, R.drawable.loading_common);
            sLoadingWhite = ContextCompat.getDrawable(context, R.drawable.loading_common_white);
            sLoadingOrange.setBounds(0, 0, loadingSize, loadingSize);
            sLoadingWhite.setBounds(0, 0, loadingSize, loadingSize);
        }
        if (mTextColorBlack == 0 || mTextColorWhite == 0) {
            mTextColorBlack = ContextCompat.getColor(context, R.color.text_normal);
            mTextColorWhite = ContextCompat.getColor(context, R.color.c_white);
        }
        mMode = MODE_ORANGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        llContainer = findViewById(R.id.container);
        pbLoading = findViewById(R.id.pb_loading);
        tvLoading = findViewById(R.id.tv_loading);
        if (mMode == MODE_DARK) {
            llContainer.setBackgroundResource(R.drawable.bg_dialog_loading_dark);
            pbLoading.setIndeterminateDrawable(sLoadingWhite);
            tvLoading.setVisibility(View.GONE);
        } else {
            llContainer.setBackground(null);
            pbLoading.setIndeterminateDrawable(sLoadingOrange);
            tvLoading.setVisibility(View.GONE);
        }
        setContent(mContent, mColor);
    }

    /**
     * 设置为亮色Loading（透明蒙版，透明背景，橙色Loading）
     */
    public void setLoadingOrange() {
        if (mMode == MODE_ORANGE) {
            return;
        }
        mMode = MODE_ORANGE;
        if (llContainer == null || pbLoading == null || tvLoading == null) {
            return;
        }
        llContainer.setBackground(null);
        pbLoading.setIndeterminateDrawable(sLoadingOrange);
        tvLoading.setVisibility(View.GONE);
    }

    /**
     * 设置为暗色Loading（透明蒙版，灰色圆角矩形背景，白色Loading）
     */
    public void setLoadingDark() {
        if (mMode == MODE_DARK) {
            return;
        }
        mMode = MODE_DARK;
        if (llContainer == null || pbLoading == null || tvLoading == null) {
            return;
        }
        llContainer.setBackgroundResource(R.drawable.bg_dialog_loading_dark);
        pbLoading.setIndeterminateDrawable(sLoadingWhite);
        tvLoading.setVisibility(View.GONE);
    }

    /**
     * 设置说明文字
     *
     * @param content 说明
     */
    public void setContent(String content) {
        mContent = content;
        mColor = 0;
        if (tvLoading == null) {
            return;
        }
        setContent(content, 0);
    }

    /**
     * 设置说明文字以及文字颜色
     *
     * @param content 说明
     * @param color   色值（非资源id）
     */
    public void setContent(String content, @ColorInt int color) {
        mContent = content;
        mColor = color;
        if (tvLoading == null) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            if (color == 0) {
                color = mMode == MODE_ORANGE ? mTextColorBlack : mTextColorWhite;
            }
            if (tvLoading.getCurrentTextColor() != color) {
                tvLoading.setTextColor(color);
            }
            tvLoading.setText(content);
        }
    }

    /**
     * 关闭Loading
     */
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

}
