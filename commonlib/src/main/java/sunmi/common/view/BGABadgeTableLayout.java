package sunmi.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TableLayout;

import cn.bingoogolapple.badgeview.BGABadgeViewHelper;
import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-13.
 */
public class BGABadgeTableLayout extends TableLayout implements BGABadgeable {
    private BGABadgeViewHelper mBadgeViewHelper;

    public BGABadgeTableLayout(Context context) {
        this(context, null);
    }

    public BGABadgeTableLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        mBadgeViewHelper = new BGABadgeViewHelper(this, context, attrs, BGABadgeViewHelper.BadgeGravity.RightCenter);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mBadgeViewHelper.onTouchEvent(event);
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mBadgeViewHelper.drawBadge(canvas);
    }

    @Override
    public void showCirclePointBadge() {
        mBadgeViewHelper.showCirclePointBadge();
    }

    @Override
    public void showTextBadge(String badgeText) {
        mBadgeViewHelper.showTextBadge(badgeText);
    }

    @Override
    public void hiddenBadge() {
        mBadgeViewHelper.hiddenBadge();
    }

    @Override
    public void showDrawableBadge(Bitmap bitmap) {
        mBadgeViewHelper.showDrawable(bitmap);
    }

    @Override
    public void setDragDismissDelegate(BGADragDismissDelegate delegate) {
        mBadgeViewHelper.setDragDismissDelegate(delegate);
    }

    @Override
    public boolean isShowBadge() {
        return mBadgeViewHelper.isShowBadge();
    }

    @Override
    public boolean isDraggable() {
        return mBadgeViewHelper.isDraggable();
    }

    @Override
    public boolean isDragging() {
        return mBadgeViewHelper.isDragging();
    }

    @Override
    public BGABadgeViewHelper getBadgeViewHelper() {
        return mBadgeViewHelper;
    }

}
