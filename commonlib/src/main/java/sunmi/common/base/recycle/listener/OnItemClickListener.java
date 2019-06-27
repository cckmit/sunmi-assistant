package sunmi.common.base.recycle.listener;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;

/**
 * @author jacob
 * @since 19-4-18
 */
public interface OnItemClickListener<T> {

    /**
     * Called when a view has been clicked.
     *
     * @param holder   clicked view holder.
     * @param model    the item model attach to clicked view.
     * @param position position in list of clicked view.
     */
    void onClick(BaseRecyclerAdapter<T> adapter, BaseViewHolder<T> holder,
                 T model, int position);
}
