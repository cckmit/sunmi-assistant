package com.sunmi.cloudprinter.ui.adaper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.BlueDevice;
import com.sunmi.cloudprinter.ui.Activity.SetPrinterActivity_;

import java.util.List;

import sunmi.common.view.dialog.CommonDialog;

public class BlueListAdapter extends RecyclerView.Adapter<BlueListAdapter.ViewHolder> {

    private Context context;
    private List<BlueDevice> data;

    public BlueListAdapter(Context context, List<BlueDevice> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bluetooth, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();
                new CommonDialog.Builder(context).setTitle(R.string.str_prompt).setMessage(R.string.str_tip_link_device)
                        .setCancelButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SetPrinterActivity_.intent(context).bleAddress(data.get(position)
                                        .getAddress()).start();
                            }
                        }).create().show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvBlueName.setText(data.get(i).getName());
        viewHolder.tvBlueAddress.setText(data.get(i).getAddress());
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBlueName;
        TextView tvBlueAddress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBlueName = itemView.findViewById(R.id.left_text);
            tvBlueAddress = itemView.findViewById(R.id.right_text);
        }
    }
}
