package sunmi.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author yinhui
 * @date 2019-09-10
 */
public class NestedCustomScrollView extends NestedScrollView {

    private OnInterceptListener mInterceptListener;

    public NestedCustomScrollView(@NonNull Context context) {
        super(context);
    }

    public NestedCustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedCustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInterceptListener(OnInterceptListener l) {
        this.mInterceptListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptListener != null) {
            return mInterceptListener.onInterceptTouchEvent(ev);
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public interface OnInterceptListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }
}
