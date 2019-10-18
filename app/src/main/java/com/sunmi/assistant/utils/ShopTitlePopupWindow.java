package com.sunmi.assistant.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sunmi.assistant.R;

import java.util.List;

import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;


/**
 * @author admin
 * @date 2018/7/10
 */

public class ShopTitlePopupWindow extends PopupWindow implements View.OnTouchListener {
    /**
     * 当店铺列表大于固定数量，RecyclerView设置固定高度
     */
    private static final int SHOP_ITEM_SIZE = 7;
    private Activity mContext;
    private TextView mSetViewImg;

    @SuppressLint("ClickableViewAccessibility")
    public ShopTitlePopupWindow(Activity activity, View topToPopupWindowView,
                                List<ShopListResp.ShopInfo> shopList, TextView mSetViewImg) {
        super();
        if (activity != null) {
            this.mContext = activity;
            this.mSetViewImg = mSetViewImg;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewLayout = inflater.inflate(R.layout.device_popwindow_shop_list, null);
        setContentView(viewLayout);
        //宽高
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //动画
        setAnimationStyle(R.anim.pop_action_sheet_enter);
        //显示位置
        showAsDropDown(topToPopupWindowView);
        init();

        TextView tvItemCompany = viewLayout.findViewById(R.id.tv_item_company);
        RecyclerView itemRecyclerView = viewLayout.findViewById(R.id.item_recycler_view);
        String companyName = SpUtils.getCompanyName();
        tvItemCompany.setText(companyName);
        if (shopList.size() > SHOP_ITEM_SIZE) {
            itemRecyclerView.getLayoutParams().height =
                    (int) (mContext.getResources().getDimension(R.dimen.dp_48) * 7.5);
        }
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        itemRecyclerView.setAdapter(new ShopRecyclerViewAdapter(mContext, shopList));
        viewLayout.setOnTouchListener(this);
        this.setTouchInterceptor(this);
    }

    /**
     * popupWindow设置参数
     */
    private void init() {
        //背景
        ColorDrawable cd = new ColorDrawable(0x00000000);
        setBackgroundDrawable(cd);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setTouchable(true);
        setOutsideTouchable(true);
        //获取isShowing()的状态
        setFocusable(true);
        update();
    }

    private void setImageBackground() {
        if (mSetViewImg != null) {
            mSetViewImg.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_drop_down_white), null);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE ||
                event.getAction() == MotionEvent.ACTION_DOWN) {
            //title+statusBar高度
            int titleHeight = (int) mContext.getResources().getDimension(R.dimen.dp_64) + Utils.getStatusBarHeight(mContext);
            //RecyclerView底部Y高度坐标
            int recyclerViewBottomHeight = (int) (mContext.getResources().getDimension(R.dimen.dp_48) * 7.5 + (int) mContext.getResources().getDimension(R.dimen.dp_32));
            if (this.isShowing() && event.getY() < titleHeight || event.getY() > recyclerViewBottomHeight) {
                dismiss();
                setImageBackground();
                return true;
            }
        }
        return false;
    }

    /**
     * Android 7.0以上 view显示问题
     */
    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getWindowVisibleDisplayFrame(rect);
            Activity activity = (Activity) anchor.getContext();
            Rect outRect1 = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
            int h = outRect1.height() - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    private class ShopRecyclerViewAdapter extends CommonListAdapter<ShopListResp.ShopInfo> {
        /**
         * @param context 上下文
         * @param list    列表数据
         */
        public ShopRecyclerViewAdapter(Context context, List<ShopListResp.ShopInfo> list) {
            super(context, R.layout.dropdown_item, list);
        }

        @Override
        public void convert(ViewHolder holder, ShopListResp.ShopInfo shopInfo) {
            TextView dropdownItemName = holder.getView(R.id.dropdown_item_name);
            ImageView dropdownItemCheckbox = holder.getView(R.id.dropdown_item_checkbox);
            dropdownItemName.setText(shopInfo.getShop_name());
            if (holder.getAdapterPosition() == 0) {
                dropdownItemCheckbox.setVisibility(View.VISIBLE);
                dropdownItemName.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
            } else {
                dropdownItemCheckbox.setVisibility(View.GONE);
                dropdownItemName.setTextColor(ContextCompat.getColor(mContext, R.color.text_body));
            }
            holder.itemView.setOnClickListener(v -> {
                SpUtils.setShopId(shopInfo.getShop_id());
                SpUtils.setShopName(shopInfo.getShop_name());
                BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.shopNameChanged);
                dismiss();
                setImageBackground();
            });
        }
    }
}
