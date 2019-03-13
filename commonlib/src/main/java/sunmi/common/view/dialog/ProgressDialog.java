package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.commonlibrary.R;

public class ProgressDialog extends Dialog {

    TextView tvTip;

    public ProgressDialog(Context context) {
        super(context, R.style.Son_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_sunmi);
        tvTip = findViewById(R.id.tvTip);
        setCanceledOnTouchOutside(false);
    }

    public void setContent(String content) {
        if (getContext() == null) return;
        if (tvTip == null) {
            this.show();
            this.dismiss();
        }
        if (!TextUtils.isEmpty(content)) {
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(content);
        } else {
            tvTip.setVisibility(View.GONE);
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
