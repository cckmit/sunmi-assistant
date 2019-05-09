package com.sunmi.ipc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.ipc.R;

import java.util.List;

import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.SunmiDevUtils;

/**
 * Description:
 * Created by bruce on 2019/4/17.
 */
public class IPCListAdapter extends RecyclerView.Adapter<IPCListAdapter.ViewHolder> {

    private Context context;
    private List<SunmiDevice> data;


    public IPCListAdapter(Context context, List<SunmiDevice> data) {
        this.context = context;
        this.data = data;
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
        setChecked(holder, position);
        holder.ivDevice.setImageResource(SunmiDevUtils.setSearchLogo(data.get(position).getModel()));
        if (TextUtils.isEmpty(data.get(position).getName())) {
            holder.tvName.setText(String.format("%s-%s", data.get(position).getModel(), data.get(position).getDeviceid()));
        } else
            holder.tvName.setText(data.get(position).getModel());
    }

    private void setChecked(@NonNull ViewHolder holder, int position) {
        if (data.size() == 1 && position == 0) {
            holder.checkBox.setEnabled(false);
        } else {
            holder.checkBox.setEnabled(true);
        }
    }

//    private void setIcon(@NonNull ViewHolder holder, int position) {
//        if (TextUtils.equals("FS1", data.get(position).getModel())) {
//            holder.ivDevice.setImageResource(R.mipmap.item_fs);
//        } else if (TextUtils.equals("SS1", data.get(position).getModel())) {
//            holder.ivDevice.setImageResource(R.mipmap.item_ss);
//        } else {
//            holder.ivDevice.setImageResource(SunmiDevUtils.setSearchLogo(data.get(position).getModel()));
//        }
//    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        RelativeLayout rootView;
        ImageView ivDevice;
        ImageView ivStatus;
        TextView tvName;
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rl_root);
            ivDevice = view.findViewById(R.id.iv_device);
            ivStatus = view.findViewById(R.id.iv_status);
            tvName = view.findViewById(R.id.tv_name);
            checkBox = view.findViewById(R.id.cb_item);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            data.get(getAdapterPosition()).setSelected(isChecked);
        }
    }

}
