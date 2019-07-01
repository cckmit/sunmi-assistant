package sunmi.common.base.recycle.listener;

import android.view.View;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;

/**
 * @author yinhui
 * @since 19-4-18
 */
public interface OnViewLongClickListener<T> {

    /**
     * Called when a view has been clicked.
     *
     * @param holder   clicked view holder.
     * @param model    the item model attach to clicked view.
     * @param position position in list of clicked view.
     */
    boolean onLongClick(BaseRecyclerAdapter<T> adapter, BaseViewHolder<T> holder,
                        View v, T model, int position);
}
