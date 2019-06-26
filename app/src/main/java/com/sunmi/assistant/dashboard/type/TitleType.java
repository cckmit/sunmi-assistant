package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.Title;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class TitleType extends ItemType<Title, BaseViewHolder<Title>> {

    private static final String TAG = "TitleType";

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_title;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Title> holder, Title model, int position) {
        TextView companyName = holder.getView(R.id.tv_dashboard_company_name);
        TextView shopName = holder.getView(R.id.tv_dashboard_shop_name);
        if (!TextUtils.isEmpty(model.companyName)) {
            companyName.setText(model.companyName);
            holder.getView(R.id.pb_dashboard_company_loading).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(model.shopName)) {
            shopName.setText(model.shopName);
            holder.getView(R.id.pb_dashboard_shop_loading).setVisibility(View.GONE);
        }
    }

}
