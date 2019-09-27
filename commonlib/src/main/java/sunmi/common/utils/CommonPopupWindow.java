package sunmi.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * @author admin
 * @date 2018/7/10
 */

public class CommonPopupWindow extends PopupWindow {
    Context mContext;
    private WindowManager mWindowManager;

    public CommonPopupWindow(View contentView, int width, int height, boolean focusable) {
        super();
        if (contentView != null) {
            mContext = contentView.getContext();
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
    }

    @Override
    public void showAsDropDown(View anchor) {
        //Android 7.0以上 view显示问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getWindowVisibleDisplayFrame(rect);
            Activity activity = (Activity) anchor.getContext();
            Rect outRect1 = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
            int h = outRect1.height() - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }
}
