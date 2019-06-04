package com.sunmi.cloudprinter.ui.adaper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.BlueDevice;

import java.util.List;

public class PrinterListAdapter extends RecyclerView.Adapter<PrinterListAdapter.ViewHolder> {

    private Context context;
    private List<BlueDevice> data;
    private OnItemClickListener listener;

    public PrinterListAdapter(Context context, List<BlueDevice> data) {
        this.context = context;
        this.data = data;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bluetooth, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(data.get(viewHolder.getAdapterPosition()));
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

    public interface OnItemClickListener {
        void onItemClick(BlueDevice blueDevice);
    }

}
