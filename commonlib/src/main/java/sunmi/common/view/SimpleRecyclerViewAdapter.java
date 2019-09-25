package sunmi.common.view;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.base.BaseApplication;

/**
 * Description:
 * Created by bruce on 2019/4/15.
 */
public class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {
    private int mResource;
    private int[] mIcons;
    private String[] mNames;
    private OnItemClickListener onItemClickListener;

    public SimpleRecyclerViewAdapter(@LayoutRes int resource, @IdRes int[] mIcons, String[] mNames) {
        this.mResource = resource;
        this.mIcons = mIcons;
        this.mNames = mNames;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(mResource, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvName.setTextColor(ContextCompat.getColor(BaseApplication.getContext(),
                i == mNames.length - 1 ? R.color.color_999999 : R.color.color_333333));
        viewHolder.tvName.setText(mNames[i]);
        viewHolder.imageView.setImageResource(mIcons[i]);
    }

    @Override
    public int getItemCount() {
        return mNames.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rootView;
        TextView tvName;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rl_root);
            rootView.setOnClickListener(this);
            tvName = rootView.findViewById(R.id.tv_name);
            imageView = rootView.findViewById(R.id.iv_pic);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}