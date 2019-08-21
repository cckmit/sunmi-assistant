package com.sunmi.ipc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.TimeBean;
import com.sunmi.ipc.model.VideoTimeSlotBean;

import java.util.List;

import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 * Created by bruce on 2019/6/11.
 */
public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<TimeBean> list;
    private List<VideoTimeSlotBean> apCloudList;//组合时间轴
    private Context context;

    DateAdapter(Context context, List<TimeBean> list, List<VideoTimeSlotBean> apCloudList) {
        this.context = context;
        this.list = list;
        this.apCloudList = apCloudList;
    }

    @NonNull
    @Override
    public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_time, viewGroup, false);
        return new DateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.ViewHolder viewHolder, int i) {
        TimeBean bean = list.get(i);
        long date = bean.getDate();
        String str = DateTimeUtils.secondToDate(date, "HH:mm:ss");
        String minuteSecond = str.substring(3, 8);
        String hour = str.substring(3, 5);

        //当前时间线的高度
        ViewGroup.LayoutParams lp = viewHolder.tvLine.getLayoutParams();
//        lp.width = CommonHelper.dp2px(context, 1f);
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dp_1);
        if (hour.contains("00")) {
            lp.height = CommonHelper.dp2px(context, 8);
            viewHolder.tvLine.setVisibility(View.VISIBLE);

        } else {
            lp.height = CommonHelper.dp2px(context, 4);
            viewHolder.tvLine.setVisibility(showShortTimeLine(minuteSecond)
                    ? View.VISIBLE : View.INVISIBLE);
        }
        viewHolder.tvLine.setLayoutParams(lp);
        //渲染
        viewHolder.rlItem.setBackgroundResource(R.color.transparent);
        if (apCloudList != null) {
            for (int j = 0; j < apCloudList.size(); j++) {
                if (date > apCloudList.get(j).getStartTime() && date < apCloudList.get(j).getEndTime()) {
                    viewHolder.rlItem.setBackgroundResource(R.color.colorOrangeLight);
//                        if (apCloudList.get(j).isApPlay())
//                            viewHolder.rlItem.setBackgroundResource(R.color.colorOrangeLight);
//                        else
//                            viewHolder.rlItem.setBackgroundResource(R.color.c_green);
                }
            }
        }
    }

    private boolean showShortTimeLine(String minuteSecond) {
        return minuteSecond.contains("10:00") || minuteSecond.contains("20:00")
                || minuteSecond.contains("30:00") || minuteSecond.contains("40:00")
                || minuteSecond.contains("50:00");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLine;
        RelativeLayout rlItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLine = itemView.findViewById(R.id.tv_line);
            rlItem = itemView.findViewById(R.id.rl_item);
        }
    }

}

