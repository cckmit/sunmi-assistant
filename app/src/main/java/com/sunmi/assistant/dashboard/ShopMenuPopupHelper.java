package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
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

    private Context mContext;
    private ConstraintLayout mContent;
    private View mOverlay;
    private LinearLayout mShopMenuList;
    private ShopMenuAnimation mDropdownAnimator = new ShopMenuAnimation();
    private int mOffset;

    public ShopMenuPopupHelper(Context context, ConstraintLayout content, View overlay) {
        this.mContext = context;
        this.mContent = content;
        this.mOverlay = overlay;
    }

    public void setOffset(int offset) {
        this.mOffset = offset;
    }

    @Override
    public void initMenu(RecyclerView list) {
        if (mContext == null || list.getAdapter() == null || list.getAdapter().getItemCount() == 0) {
            return;
        }
        // Add header view
        mShopMenuList = new LinearLayout(mContext);
        mShopMenuList.setId(View.generateViewId());
        mShopMenuList.setOrientation(LinearLayout.VERTICAL);
        mShopMenuList.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        TextView header = new TextView(mContext);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.dp_32)));
        int padding = (int) mContext.getResources().getDimension(R.dimen.dp_4);
        header.setText(SpUtils.getCompanyName());
        header.setPadding(padding, 0, padding, 0);
        header.setEllipsize(TextUtils.TruncateAt.END);
        header.setLines(1);
        header.setTextSize(12f);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.color_525866));
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_F0F2F5));
        list.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        mShopMenuList.addView(header);
        mShopMenuList.addView(list);
        // Add view into ConstraintLayout.
        int index = mContent.indexOfChild(mOverlay) + 1;
        if (mContent.indexOfChild(mShopMenuList) == -1) {
            mContent.addView(mShopMenuList, index);
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
    }

    @Override
    public void show(RecyclerView list, boolean animated) {
        if (mShopMenuList == null) {
            return;
        }
        mDropdownAnimator.startAnimationToShow(animated, mShopMenuList, mOffset, mOverlay);
    }

    @Override
    public void dismiss(RecyclerView list, boolean animated) {
        if (mShopMenuList == null) {
            return;
        }
        mDropdownAnimator.startAnimationToDismiss(animated, mShopMenuList, mOffset, mOverlay);
    }
}
