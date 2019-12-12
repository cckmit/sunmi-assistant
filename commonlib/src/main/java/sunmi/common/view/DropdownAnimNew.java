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
public class DropdownAnimNew implements DropdownMenuNew.Anim {

    private DropdownMenuNew[] menus;

    public DropdownAnimNew() {
    }

    public DropdownAnimNew(DropdownMenuNew... menus) {
        this.menus = menus;
    }

    @Override
    public AnimatorSet showAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), 0);
        ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                overlay.getAlpha(), 1f);
        ObjectAnimator arrowAnim = ObjectAnimator.ofFloat(arrow, "rotation",
                0, 180);
        set.play(menuAnim).with(overlayAnim).with(arrowAnim);
        set.setDuration(250);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    @Override
    public void onPostShow(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        TextView title = (TextView) titleHolder.getView(R.id.dropdown_title);
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        title.setSelected(true);
        menu.setTranslationY(0);
        overlay.setAlpha(1f);
        arrow.setRotation(180);
    }

    @Override
    public void onPreShow(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        overlay.setVisibility(View.VISIBLE);
        if (menus != null) {
            for (DropdownMenuNew item : menus) {
                item.dismiss(true);
            }
        }
    }

    @Override
    public AnimatorSet dismissAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), -menu.getHeight());
        ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                overlay.getAlpha(), 0);
        ObjectAnimator arrowAnim = ObjectAnimator.ofFloat(arrow, "rotation",
                -180, 0);
        set.play(menuAnim).with(overlayAnim).with(arrowAnim);
        set.setDuration(250);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    @Override
    public void onPostDismiss(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_arrow);
        menu.setTranslationY(-menu.getHeight());
        overlay.setAlpha(0f);
        arrow.setRotation(0);
        overlay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPreDismiss(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
    }

}
