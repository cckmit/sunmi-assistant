package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.assistant.R;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-19.
 */
public class MsgTabAdapter extends RecyclerView.Adapter<MsgTabAdapter.ViewHolder> {

    private Context context;
    private List<String> msgTitle;
    private List<Integer> msgCount;
    private OnItemClickListener listener;
    private int selectPosition = 0;

    public MsgTabAdapter(Context context, List<String> msgTitle, List<Integer> msgCount) {
        this.context = context;
        this.msgTitle = msgTitle;
        this.msgCount = msgCount;
    }

    public interface OnItemClickListener {
        void onClick(String data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_msg_tab,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemView.setSelected(i == selectPosition);
        viewHolder.tvMsgTitle.setText(msgTitle.get(i));
        int count = msgCount.get(i);
        if (count <= 0) {
            viewHolder.tvMsgCount.setText("");
        } else if (count > 99) {
            viewHolder.tvMsgCount.setText(context.getString(R.string.str_msg_count, "99+"));
        } else {
            viewHolder.tvMsgCount.setText(context.getString(R.string.str_msg_count, String.valueOf(count)));
        }
    }

    @Override
    public int getItemCount() {
        return msgTitle.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvMsgTitle;
        TextView tvMsgCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsgTitle = itemView.findViewById(R.id.tv_msg_title);
            tvMsgCount = itemView.findViewById(R.id.tv_msg_count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                selectPosition = getAdapterPosition();
                listener.onClick(msgTitle.get(selectPosition));
                notifyDataSetChanged();
            }
        }
    }
}
