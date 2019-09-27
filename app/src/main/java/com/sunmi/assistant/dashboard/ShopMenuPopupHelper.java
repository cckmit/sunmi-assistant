package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.ui.ShopMenuAnimation;

import sunmi.common.utils.SpUtils;
import sunmi.common.view.DropdownMenu;

/**
 * @author yinhui
 * @date 2019-09-17
 */
public class ShopMenuPopupHelper implements DropdownMenu.PopupHelper {

    private static final String TAG = ShopMenuPopupHelper.class.getSimpleName();

    private Context mContext;
    private ConstraintLayout mContent;
    private View mOverlay;
    private final ImageView mArrow;
    private LinearLayout mShopMenuList;
    private ShopMenuAnimation mDropdownAnimator = new ShopMenuAnimation();
    private int mOffset;
    private TextView mHeader;

    public ShopMenuPopupHelper(Context context, ConstraintLayout content, View overlay, ImageView arrow) {
        this.mContext = context;
        this.mContent = content;
        this.mOverlay = overlay;
        this.mArrow = arrow;
    }

    public void setOffset(int offset) {
        this.mOffset = offset;
    }

    public void setCompanyName(String name) {
        if (mHeader != null) {
            mHeader.setText(name);
        }
    }

    @Override
    public void initMenu(RecyclerView list) {
        if (mContext == null || list.getAdapter() == null || list.getAdapter().getItemCount() == 0) {
            return;
        }
        if (mShopMenuList == null) {
            // Add header view
            mShopMenuList = (LinearLayout) LayoutInflater.from(list.getContext())
                    .inflate(R.layout.shop_menu_layout, mContent, false);
            mShopMenuList.setId(View.generateViewId());
            mHeader = mShopMenuList.findViewById(R.id.shop_menu_title);
            mHeader.setText(SpUtils.getCompanyName());
            list.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            mShopMenuList.addView(list);
            // Add view into ConstraintLayout.
            int index = mContent.indexOfChild(mOverlay) + 1;
            mContent.addView(mShopMenuList, index);
        } else {
            mHeader = mShopMenuList.findViewById(R.id.shop_menu_title);
        }
        // Init constraint set of menu view in ConstraintLayout.
        ConstraintSet con = new ConstraintSet();
        con.clone(mContent);
        con.connect(mShopMenuList.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        con.connect(mShopMenuList.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        con.connect(mShopMenuList.getId(), ConstraintSet.TOP, R.id.layout_shop_title, ConstraintSet.BOTTOM, 0);
        con.constrainHeight(mShopMenuList.getId(), ConstraintSet.WRAP_CONTENT);
        con.constrainWidth(mShopMenuList.getId(), ConstraintSet.MATCH_CONSTRAINT);
        con.applyTo(mContent);
        mShopMenuList.measure(0, 0);
        mShopMenuList.getLayoutParams().height = list.getMeasuredHeight() + mHeader.getMeasuredHeight();
    }

    @Override
    public void show(RecyclerView list, boolean animated) {
        if (mShopMenuList == null) {
            return;
        }
        mDropdownAnimator.startAnimationToShow(animated, mShopMenuList, mOffset, mOverlay, mArrow);
    }

    @Override
    public void dismiss(RecyclerView list, boolean animated) {
        if (mShopMenuList == null) {
            return;
        }
        mDropdownAnimator.startAnimationToDismiss(animated, mShopMenuList, mOffset, mOverlay, mArrow);
    }
}
