package sunmi.common.view.webview;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-31.
 */
public class AndroidBug5497Workaround {

// For more information, see https://code.google.com/p/android/issues/detail?id=5497
// To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity, boolean isFullScreen) {
        new AndroidBug5497Workaround(activity, isFullScreen);
    }
    private boolean mIsFullScreen;

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int usableHeightSansKeyboard = -1;

    private AndroidBug5497Workaround(Activity activity, boolean isFullScreen) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        mIsFullScreen = isFullScreen;
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            if (usableHeightSansKeyboard < 0) {
                // 第一次进行赋值
                usableHeightSansKeyboard = usableHeightNow;
            }
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
// keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
// keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        if (mIsFullScreen) {
            return r.bottom;
        } else {
            return (r.bottom - r.top);
        }
    }

}
