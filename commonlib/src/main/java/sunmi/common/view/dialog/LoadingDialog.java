package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 * @Description 加载中等待框（纯动画，show即可）
 */
public class LoadingDialog extends Dialog {

    private TextView tvLoading;
    private int layoutId;

    public LoadingDialog(Context context) {
        super(context, R.style.Son_dialog);
    }

    public LoadingDialog(Context context, int layoutResId) {
        super(context, R.style.Son_dialog);
        this.layoutId = layoutResId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutId > 0) {
            setContentView(layoutId);
        } else {
            setContentView(R.layout.dialog_loading);
            tvLoading = this.findViewById(R.id.tv_loading);
        }
    }

    public void setLoadingContent(String content) {
        if (tvLoading == null) {
            this.show();
            this.dismiss();
            return;
        }
        if (!TextUtils.isEmpty(content)) {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(content);
        } else {
            tvLoading.setVisibility(View.GONE);
        }
    }

    public void setTipColorText(String content, int colorRes) {
        if (tvLoading == null) {
            this.show();
            this.dismiss();
            return;
        }
        if (!TextUtils.isEmpty(content)) {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setTextColor(colorRes);
            tvLoading.setText(content);
        } else {
            tvLoading.setVisibility(View.GONE);
        }
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

}
