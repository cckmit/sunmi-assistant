package com.sunmi.ipc.face.util;

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
public class BottomAnimation {

    private static final String TAG = BottomAnimation.class.getSimpleName();
    private AnimatorSet mCurrent;

    public void startAnimationToShow(boolean animated, final View view) {
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(view, "translationY",
                    view.getTranslationY(), 0);
            set.play(menuAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
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
            view.setTranslationY(0);
            view.setVisibility(View.VISIBLE);
        }
    }

    public void startAnimationToDismiss(boolean animated, final View view) {
        float height = view.getMeasuredHeight();
        if (animated) {
            if (mCurrent != null) {
                mCurrent.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(view, "translationY",
                    view.getTranslationY(), height);
            set.play(menuAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    mCurrent = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    view.setVisibility(View.GONE);
                    mCurrent = null;
                }
            });
            set.start();
            mCurrent = set;
        } else {
            view.setTranslationY(height);
            view.setVisibility(View.GONE);
        }
    }
}
