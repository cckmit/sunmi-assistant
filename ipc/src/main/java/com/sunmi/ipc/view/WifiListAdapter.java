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

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;

import java.util.List;

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
        setStatus(holder, data.get(position).getKey_mgmt());
        holder.ivSignal.setImageResource(R.drawable.ic_signal_3);
    }

    private void setStatus(@NonNull ViewHolder holder, String mgmt) {
        if (TextUtils.equals(mgmt, "NONE")) {
            holder.ivSecret.setImageDrawable(null);
        } else if (TextUtils.equals(mgmt, "WPA-PSK")) {
            holder.ivSecret.setImageResource(R.drawable.ic_lock);
        }
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
            tvName = view.findViewById(R.id.tv_ap_name);
            ivSecret = view.findViewById(R.id.iv_secret);
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
