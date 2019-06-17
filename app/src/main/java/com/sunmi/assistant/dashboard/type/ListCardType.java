package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.ListCard;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class ListCardType extends ItemType<ListCard, BaseViewHolder<ListCard>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_list;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<ListCard> onCreateViewHolder(@NonNull View view, @NonNull ItemType<ListCard, BaseViewHolder<ListCard>> type) {
        RankListAdapter adapter = new RankListAdapter(view.getContext());
        BaseViewHolder<ListCard> holder = new BaseViewHolder<>(view, type);
        ListView listView = holder.getView(R.id.lv_dashboard_list);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ListCard> holder, ListCard model, int position) {
        ListView listView = holder.getView(R.id.lv_dashboard_list);
        TextView title = holder.getView(R.id.tv_dashboard_title);
        title.setText(model.title);

        RankListAdapter adapter = (RankListAdapter) listView.getAdapter();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = CommonHelper.dp2px(holder.getContext(), 36.0f * model.list.size());
        listView.setLayoutParams(params);
        adapter.setDatas(model.list);
        adapter.notifyDataSetChanged();
    }
}
