package com.sunmi.assistant.order;

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
class DropdownAnimation {

    private static final String TAG = "DropdownAnimation";
    private AnimatorSet mCurrent;

    void startAnimationToShow(boolean animated, final View dropdownMenu, final View overlay) {
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    dropdownMenu.getTranslationY(), 0);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 0.6f);
            set.play(menuAnim).with(overlayAnim);
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
            dropdownMenu.setTranslationY(0);
            dropdownMenu.setVisibility(View.VISIBLE);
            overlay.setAlpha(0.6f);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    void startAnimationToDismiss(boolean animated, final View dropdownMenu, final View overlay) {
        float height = dropdownMenu.getMeasuredHeight();
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    dropdownMenu.getTranslationY(), -1 * height);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 0);
            set.play(menuAnim).with(overlayAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.INVISIBLE);
                    mCurrent = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.INVISIBLE);
                    mCurrent = null;
                }
            });
            set.start();
            mCurrent = set;
        } else {
            dropdownMenu.setTranslationY(-1 * height);
            dropdownMenu.setVisibility(View.INVISIBLE);
            overlay.setAlpha(0);
            overlay.setVisibility(View.INVISIBLE);
        }
    }
}
