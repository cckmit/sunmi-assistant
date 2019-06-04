package sunmi.common.view;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

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
        viewHolder.tvName.setText(mNames[i]);
        viewHolder.imageView.setImageResource(mIcons[i]);
    }

    @Override
    public int getItemCount() {
        return mIcons.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rootView;
        TextView tvName;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rl_root);
            rootView.setOnClickListener(this);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                if (rootView.getChildAt(i) instanceof TextView) {
                    tvName = (TextView) rootView.getChildAt(i);
                } else if (rootView.getChildAt(i) instanceof ImageView) {
                    imageView = (ImageView) rootView.getChildAt(i);
                }
            }
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