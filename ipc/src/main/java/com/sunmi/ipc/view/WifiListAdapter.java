package com.sunmi.ipc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.ipc.IPCCall;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;

import java.util.List;

import sunmi.common.view.dialog.InputDialog;

/**
 * Description:
 * Created by bruce on 2019/1/11.
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {

    private Context context;
    private List<WifiListResp.ScanResultsBean> data;

    private OnItemClickListener onItemClickListener;

    public WifiListAdapter(Context context, List<WifiListResp.ScanResultsBean> data) {
        this.context = context;
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_wifi, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.tvName.setText(data.get(position).getSsid());
        setStatus(holder, position);
    }

    private void setStatus(@NonNull ViewHolder holder, int position) {
//        if (data.get(position).getStatus() == 0) {
//            holder.tvStatus.setText(R.string.status_inactive);
//        } else if (data.get(position).getStatus() == 1) {
//            holder.tvStatus.setText(R.string.status_online);
//        } else if (data.get(position).getStatus() == 2) {
//            holder.tvStatus.setText(R.string.status_offline);
//        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rootView;
        TextView tvName;
        ImageView ivSecret;
        ImageView ivSignal;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rl_root);
            rootView.setOnClickListener(this);
            ivSecret = view.findViewById(R.id.iv_secret);
            tvName = view.findViewById(R.id.tv_ap_name);
            ivSignal = view.findViewById(R.id.iv_signal);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(data.get(getAdapterPosition()).getSsid(),
                        data.get(getAdapterPosition()).getKey_mgmt());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String ssid, String mgmt);
    }

}
