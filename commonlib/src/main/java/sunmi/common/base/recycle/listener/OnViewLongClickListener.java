package sunmi.common.base.recycle.listener;

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
    boolean onLongClick(BaseViewHolder<T> holder, T model, int position);
}
