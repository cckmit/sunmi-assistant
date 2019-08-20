package sunmi.common.base.recycle;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import sunmi.common.base.recycle.listener.OnItemClickListener;
import sunmi.common.base.recycle.listener.OnItemLongClickListener;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.base.recycle.listener.OnViewLongClickListener;

/**
 * @author yinhui
 * @date 2019-08-16
 */
public abstract class SimpleArrayAdapter<T> extends BaseArrayAdapter<T> {

    private int mLayoutId;
    private Type mType;

    public SimpleArrayAdapter(@LayoutRes int layoutId) {
        this(layoutId, null);
    }

    public SimpleArrayAdapter(@LayoutRes int layoutId, List<T> data) {
        super(data);
        mLayoutId = layoutId;
        mType = new Type();
        register(mType);
    }

    public BaseViewHolder<T> createView(@NonNull View view, @NonNull ItemType<T, BaseViewHolder<T>> type) {
        return new BaseViewHolder<>(view, type);
    }

    public abstract void setupView(@NonNull BaseViewHolder<T> holder, T model, int position);

    public void setOnItemClickListener(OnItemClickListener<T> l) {
        mType.setOnItemClickListener(l);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> l) {
        mType.setOnItemLongClickListener(l);
    }

    public void addOnViewClickListener(@IdRes int id, OnViewClickListener<T> l) {
        mType.addOnViewClickListener(id, l);
    }

    public void addOnViewLongClickListener(@IdRes int id, OnViewLongClickListener<T> l) {
        mType.addOnViewLongClickListener(id, l);
    }

    private class Type extends ItemType<T, BaseViewHolder<T>> {

        @Override
        public int getLayoutId(int type) {
            return mLayoutId;
        }

        @NonNull
        @Override
        public BaseViewHolder<T> onCreateViewHolder(@NonNull View view, @NonNull ItemType<T, BaseViewHolder<T>> type) {
            return createView(view, type);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, T model, int position) {
            setupView(holder, model, position);
        }
    }
}
