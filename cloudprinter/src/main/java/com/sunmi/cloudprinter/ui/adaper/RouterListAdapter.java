package com.sunmi.cloudprinter.ui.adaper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.Router;

import java.util.List;

public class RouterListAdapter extends RecyclerView.Adapter<RouterListAdapter.ViewHolder> {

    private List<Router> routers;
    private OnItemClickListener listener;

    public RouterListAdapter(List<Router> routers) {
        this.routers = routers;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, List<Router> data);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_router, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                listener.onItemClick(position, routers);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.routerName.setText(routers.get(i).getName());
        if (routers.get(i).isHasPwd()) {
            viewHolder.routerLocked.setVisibility(View.VISIBLE);
        }
        showRssi(viewHolder.ivRssi, routers.get(i).getRssi());
    }

    /**
     * -60 ~ 0   4
     * -70 ~ -60 3
     * -80 ~ -70 2
     * <-80      1
     */
    private void showRssi(ImageView view, int rssi) {
        if (rssi == 0) {
            view.setImageResource(R.mipmap.singal_0);
        } else if (rssi ==1) {
            view.setImageResource(R.mipmap.singal_1);
        } else if(rssi==2){
            view.setImageResource(R.mipmap.singal_2);
        }else if (rssi ==3){
            view.setImageResource(R.mipmap.singal_3);
        }else if (rssi ==4){
            view.setImageResource(R.mipmap.singal_4);
        }
    }

    @Override
    public int getItemCount() {
        return routers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView routerName;
        private ImageView routerLocked;
        private ImageView ivRssi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routerName = itemView.findViewById(R.id.tv_ap_name);
            routerLocked = itemView.findViewById(R.id.iv_secret);
            ivRssi = itemView.findViewById(R.id.iv_signal);
        }
    }
}
