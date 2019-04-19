package com.sunmi.ipc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.ipc.R;

import java.util.List;

import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/4/17.
 */
public class IPCListAdapter extends RecyclerView.Adapter<IPCListAdapter.ViewHolder> {

    private Context context;
    private List<SunmiDevice> data;

    private OnItemClickListener onItemClickListener;

    public IPCListAdapter(Context context, List<SunmiDevice> data) {
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
                .inflate(R.layout.item_device, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        if (data.size() == 1 && position == 0) {
            holder.checkBox.setEnabled(false);
        } else holder.checkBox.setEnabled(true);
        if (TextUtils.equals("FS1", data.get(position).getModel())) {
            holder.ivDevice.setImageResource(R.mipmap.item_fs);
        } else if (TextUtils.equals("SS1", data.get(position).getModel())) {
            holder.ivDevice.setImageResource(R.mipmap.item_ss);
        }
        holder.tvName.setText(data.get(position).getName());
//        setStatus(holder, data.get(position).getKey_mgmt());
    }

    private void setStatus(@NonNull ViewHolder holder, String mgmt) {
        if (TextUtils.equals(mgmt, "NONE")) {
            holder.ivStatus.setImageDrawable(null);
        } else if (TextUtils.equals(mgmt, "WPA-PSK")) {
            holder.ivStatus.setImageResource(R.mipmap.ic_lock);
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
        ImageView ivDevice;
        ImageView ivStatus;
        TextView tvName;
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rl_root);
            rootView.setOnClickListener(this);
            ivDevice = view.findViewById(R.id.iv_device);
            ivStatus = view.findViewById(R.id.iv_status);
            tvName = view.findViewById(R.id.tv_name);
            checkBox = view.findViewById(R.id.cb_item);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
            }
//                onItemClickListener.onItemClick(data.get(getAdapterPosition()).getSsid(),
//                        data.get(getAdapterPosition()).getKey_mgmt());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String ssid, String mgmt);
    }

}
