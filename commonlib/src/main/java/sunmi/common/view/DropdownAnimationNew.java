package sunmi.common.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 * 下拉列表动画辅助类
 *
 * @author yinhui
 * @since 2019-12-5
 */
public class DropdownAnimationNew implements DropdownMenuNew.Animation {

    @Override
    public AnimatorSet showAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), 0);
        ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                overlay.getAlpha(), 1f);
        set.play(menuAnim).with(overlayAnim);
        set.setDuration(250);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    @Override
    public void showImmediately(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        TextView title = (TextView) titleHolder.getView(R.id.dropdown_title);
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        title.setSelected(true);
        arrow.setSelected(true);
        menu.setTranslationY(0);
        overlay.setAlpha(1f);
        overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public AnimatorSet dismissAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), -menu.getHeight());
        ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                overlay.getAlpha(), 0);
        set.play(menuAnim).with(overlayAnim);
        set.setDuration(250);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    @Override
    public void dismissImmediately(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        TextView title = (TextView) titleHolder.getView(R.id.dropdown_title);
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        arrow.setSelected(false);
        menu.setTranslationY(-menu.getHeight());
        overlay.setAlpha(0f);
        overlay.setVisibility(View.INVISIBLE);
    }

}
