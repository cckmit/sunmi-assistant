package com.sunmi.ipc.cash;

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

import com.bumptech.glide.Glide;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.CashVideoResp;

import java.util.ArrayList;

import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;


/**
 * @author admin
 * @date 2018/7/10
 */

public class CashVideoPopupWindow extends PopupWindow implements View.OnTouchListener {
    /**
     * 当店铺列表大于固定数量，RecyclerView设置固定高度
     */
    private static final int SHOP_ITEM_SIZE = 3;
    private Activity mContext;
    private TextView mSetTitleView;
    private ArrayList<CashVideoResp.AuditVideoListBean> mList;
    private int currentPlayPosition;
    private double maxLength = 3.5;

    @SuppressLint("ClickableViewAccessibility")
    public CashVideoPopupWindow(Activity activity, View topToPopupWindowView, int currentPlayPosition,
                                ArrayList<CashVideoResp.AuditVideoListBean> list, TextView mSetViewImg) {
        super();
        if (activity != null) {
            this.mContext = activity;
            this.currentPlayPosition = currentPlayPosition;
            this.mSetTitleView = mSetViewImg;
            this.mList = list;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewLayout = inflater.inflate(R.layout.cash_popwindow_video_list, null);
        setContentView(viewLayout);
        //宽高
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //动画
        setAnimationStyle(R.anim.pop_action_sheet_enter);
        //显示位置
        showAsDropDown(topToPopupWindowView);
        init();

        RecyclerView itemRecyclerView = viewLayout.findViewById(R.id.item_cash_recycler_view);

        if (mList.size() > SHOP_ITEM_SIZE) {
            itemRecyclerView.getLayoutParams().height =
                    (int) (mContext.getResources().getDimension(R.dimen.dp_152) * maxLength);
        }
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        itemRecyclerView.setAdapter(new CashRecyclerViewAdapter(mContext, mList));
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
        if (mSetTitleView != null) {
            mSetTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(mContext, R.drawable.ic_drop_down_black), null);
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
            int recyclerViewBottomHeight = mList.size() > SHOP_ITEM_SIZE ?
                    (int) (mContext.getResources().getDimension(R.dimen.dp_152) * maxLength) : (int) (mContext.getResources().getDimension(R.dimen.dp_152) * mList.size());
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

    private class CashRecyclerViewAdapter extends CommonListAdapter<CashVideoResp.AuditVideoListBean> {
        /**
         * @param context 上下文
         * @param list    列表数据
         */
        public CashRecyclerViewAdapter(Context context, ArrayList<CashVideoResp.AuditVideoListBean> list) {
            super(context, R.layout.cash_item_trade_details, list);
        }

        @Override
        public void convert(ViewHolder holder, CashVideoResp.AuditVideoListBean res) {
            holder.setText(R.id.tv_time, DateTimeUtils.secondToDate(res.getPurchaseTime(), "HH:mm:ss"));
            holder.setText(R.id.tv_amount, String.format("¥%s", res.getAmount()));
            holder.setText(R.id.tv_order_num, res.getOrderNo());
            holder.setText(R.id.tv_pos, res.getDeviceName());
            ImageView imgVideo = holder.getView(R.id.iv_preview_img);
            ImageView ivFlag = holder.getView(R.id.iv_left_flag);
            TextView tvTag = holder.getView(R.id.tv_exception_des);
            TextView tvLineTop = holder.getView(R.id.tv_left_top_line);
            TextView tvLineBottom = holder.getView(R.id.tv_left_bottom_line);
            ivFlag.setSelected(holder.getAdapterPosition() == currentPlayPosition);
            if (isShowing()) {
                Glide.with(mContext).load(res.getSnapshotUrl()).into(imgVideo);
            }
            if (res.getVideoType() == 1) {
                tvTag.setVisibility(View.GONE);
            } else {
                tvTag.setText(res.getDescription());
                tvTag.setVisibility(View.VISIBLE);
            }
            if (holder.getAdapterPosition() == mList.size() - 1) {
                tvLineBottom.setVisibility(View.INVISIBLE);
            } else if (holder.getAdapterPosition() == 0) {
                tvLineTop.setVisibility(View.INVISIBLE);
            } else {
                tvLineTop.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(v -> {
                //获取当前的点击的视频位置
                BaseNotification.newInstance().postNotificationName(CommonNotifications.cashVideoPlayPosition, holder.getAdapterPosition());
                dismiss();
                setImageBackground();
            });
        }
    }

}
