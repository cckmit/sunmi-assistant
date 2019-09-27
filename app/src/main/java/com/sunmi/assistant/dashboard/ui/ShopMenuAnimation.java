package com.sunmi.assistant.dashboard.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 下拉列表动画辅助类
 *
 * @author yinhui
 * @since 2019-06-27
 */
public class ShopMenuAnimation {

    private static final String TAG = ShopMenuAnimation.class.getSimpleName();

    private AnimatorSet mCurrent;

    public void startAnimationToShow(boolean animated, final View dropdownMenu, int offset,
                                     final View overlay, final View arrow) {
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    dropdownMenu.getTranslationY(), offset);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 0.6f);
            ObjectAnimator arrowAnim = ObjectAnimator.ofFloat(arrow, "rotation", 0, 180);
            set.play(menuAnim).with(overlayAnim).with(arrowAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dropdownMenu.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrent = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrent = null;
                }
            });
            set.start();
            mCurrent = set;
        } else {
            dropdownMenu.setTranslationY(offset);
            dropdownMenu.setVisibility(View.VISIBLE);
            overlay.setAlpha(0.6f);
            arrow.setRotation(180);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    public void startAnimationToDismiss(boolean animated, final View dropdownMenu, int offset,
                                        final View overlay, final View arrow) {
        float height = dropdownMenu.getMeasuredHeight();
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    dropdownMenu.getTranslationY(), -1 * height + offset);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 0);
            ObjectAnimator arrowAnim = ObjectAnimator.ofFloat(arrow, "rotation", -180, 0);
            set.play(menuAnim).with(overlayAnim).with(arrowAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.INVISIBLE);
                    arrow.setRotation(0);
                    mCurrent = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.INVISIBLE);
                    arrow.setRotation(0);
                    mCurrent = null;
                }
            });
            set.start();
            mCurrent = set;
        } else {
            dropdownMenu.setTranslationY(-1 * height + offset);
            dropdownMenu.setVisibility(View.INVISIBLE);
            overlay.setAlpha(0);
            arrow.setRotation(0);
            overlay.setVisibility(View.INVISIBLE);
        }
    }
}
