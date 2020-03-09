package com.sunmi.assistant.dashboard.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;

import sunmi.common.view.DropdownMenuNew;

public class ShopMenuAnim implements DropdownMenuNew.Anim {

    private int offset = 0;

    public ShopMenuAnim() {
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public AnimatorSet showAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_item_arrow);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), offset);
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
        TextView title = (TextView) titleHolder.getView(R.id.dropdown_item_title);
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_item_arrow);
        title.setSelected(true);
        menu.setTranslationY(offset);
        overlay.setAlpha(1f);
        arrow.setRotation(180);
    }

    @Override
    public void onPreShow(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        menu.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public AnimatorSet dismissAnimator(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_item_arrow);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                menu.getTranslationY(), -menu.getHeight() + offset);
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
        ImageView arrow = (ImageView) titleHolder.getView(R.id.dropdown_item_arrow);
        menu.setTranslationY(-menu.getHeight() + offset);
        overlay.setAlpha(0f);
        arrow.setRotation(0);
        menu.setVisibility(View.INVISIBLE);
        overlay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPreDismiss(DropdownMenuNew.ViewHolder titleHolder, View menu, View overlay) {
    }

}
