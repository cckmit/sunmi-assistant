package sunmi.common.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.commonlibrary.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yinhui
 */
public class DropdownMenu extends FrameLayout implements View.OnClickListener {

    private BaseAdapter mAdapter;
    private BaseViewHolder mTitle;

    private InternalMenuPopup mPopup;

    public DropdownMenu(@NonNull Context context) {
        this(context, null);
    }

    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPopup = new CustomPopup(getContext());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPopup = new CustomPopup(getContext());
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        mPopup.setTag(tag);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mPopup.setLayoutManager(manager);
    }

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        if (mTitle != null) {
            removeView(mTitle.itemView);
        }
        mTitle = adapter.getTitle();
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mPopup.setAdapter(adapter);
        adapter.initCurrent();
        addView(mTitle.itemView, lp);
        setOnClickListener(this);
    }

    public void setSelection(int position) {
        mAdapter.setCurrent(position);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPopup.dismiss(false);
    }

    @Override
    public void onClick(View v) {
        if (mPopup.isShowing()) {
            mPopup.dismiss(true);
        } else {
            mPopup.show(true);
        }
    }

    public void setPopupHelper(PopupHelper helper) {
        mPopup.setPopupHelper(helper);
    }

    public MenuPopup getPopup() {
        return mPopup;
    }

    public interface MenuPopup {
        /**
         * Show the popup
         */
        void show(boolean animated);

        /**
         * Dismiss the popup
         */
        void dismiss(boolean animated);

        /**
         * @return true if the popup is showing, false otherwise.
         */
        boolean isShowing();
    }

    /**
     * Implements some sort of popup selection interface for selecting a menu option.
     * Allows for different dropdown menu modes.
     */
    private interface InternalMenuPopup extends MenuPopup {

        void setLayoutManager(RecyclerView.LayoutManager manager);

        void setAdapter(BaseAdapter adapter);

        void setPopupHelper(PopupHelper helper);

        void setTag(Object tag);

        void initMenu();
    }

    public interface PopupHelper {
        void initMenu(View list);

        void show(View list, boolean animated);

        void dismiss(View list, boolean animated);
    }

    public interface OnItemClickListener<T> {
        void onItemSelected(BaseAdapter<T> adapter, T model, int position);
    }

    private static class CustomPopup implements InternalMenuPopup {

        boolean isShowing;
        private PopupHelper mHelper;
        private RecyclerView mRecyclerView;

        @SuppressLint("InflateParams")
        private CustomPopup(Context context) {
            mRecyclerView = new RecyclerView(context);
            mRecyclerView.setId(View.generateViewId());
            mRecyclerView.setBackgroundColor(context.getResources().getColor(R.color.c_white));
        }

        @Override
        public void setTag(Object tag) {
            mRecyclerView.setTag(tag);
        }

        @Override
        public void setLayoutManager(RecyclerView.LayoutManager manager) {
            mRecyclerView.setLayoutManager(manager);
        }

        @Override
        public void setAdapter(BaseAdapter adapter) {
            mRecyclerView.setAdapter(adapter);
            adapter.setPopup(this);
        }

        @Override
        public void setPopupHelper(PopupHelper helper) {
            mHelper = helper;
            initMenu();
        }

        @Override
        public void initMenu() {
            mHelper.initMenu(mRecyclerView);
            isShowing = false;
            mHelper.dismiss(mRecyclerView, false);
        }

        @Override
        public void show(boolean animated) {
            if (mHelper != null && !isShowing) {
                mHelper.show(mRecyclerView, animated);
                isShowing = true;
            }
        }

        @Override
        public void dismiss(boolean animated) {
            if (mHelper != null && isShowing) {
                mHelper.dismiss(mRecyclerView, animated);
                isShowing = false;
            }
        }

        @Override
        public boolean isShowing() {
            return isShowing;
        }
    }

    public static abstract class BaseAdapter<T>
            extends RecyclerView.Adapter<BaseViewHolder<T>> {

        DropdownMenu.OnItemClickListener<T> mListener;
        private InternalMenuPopup mPopup;
        @LayoutRes
        private int mItemRes;
        private BaseViewHolder<T> mTitleHolder;
        private T mInitData = null;
        private List<T> mData = new ArrayList<>();
        private T mCurrent;

        protected BaseAdapter(Context context, @LayoutRes int titleRes, @LayoutRes int itemRes) {
            this(context, titleRes, itemRes, null);
        }

        protected BaseAdapter(Context context, @LayoutRes int titleRes, @LayoutRes int itemRes,
                              List<T> data) {
            View title = LayoutInflater.from(context).inflate(titleRes, null);
            mTitleHolder = createTitle(title);
            this.mItemRes = itemRes;
            if (data != null && data.size() > 0) {
                this.mData = data;
            }
        }

        private void setPopup(InternalMenuPopup popup) {
            this.mPopup = popup;
        }

        @Override
        @NonNull
        public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(mItemRes, parent, false);
            return createItem(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
            T model = mData.get(position);
            holder.setUpView(model, position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public BaseViewHolder<T> getTitle() {
            return mTitleHolder;
        }

        public List<T> getData() {
            return mData;
        }

        private void initCurrent() {
            if (mInitData != null) {
                mCurrent = mInitData;
                mTitleHolder.setUpView(mInitData, -1);
            } else if (mData != null && mData.size() > 0) {
                mCurrent = mData.get(0);
                mTitleHolder.setUpView(mCurrent, 0);
                if (mListener != null) {
                    mListener.onItemSelected(this, mCurrent, 0);
                }
            }
        }

        public void setInitData(T data) {
            mInitData = data;
        }

        public void setData(@NonNull Collection<? extends T> data) {
            setData(data, 0);
        }

        public void setData(@NonNull Collection<? extends T> data, int selection) {
            mData.clear();
            mData.addAll(data);
            if (selection < 0) {
                initCurrent();
            } else if (mData.size() > selection) {
                setCurrent(selection);
            }
            notifyDataSetChanged();
            mPopup.initMenu();
        }

        public void addData(@NonNull T data) {
            mData.add(data);
            if (mData.size() == 1) {
                notifyDataSetChanged();
            } else {
                notifyItemInserted(mData.size());
            }
        }

        public void addData(@IntRange(from = 0) int position, @NonNull T data) {
            mData.add(position, data);
            if (mData.size() == 1) {
                notifyDataSetChanged();
            } else {
                notifyItemInserted(position);
            }
        }

        public void addData(@NonNull Collection<? extends T> data) {
            mData.addAll(data);
            if (mData.size() == data.size()) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(mData.size() - data.size(), data.size());
            }
        }

        public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> data) {
            mData.addAll(position, data);
            if (mData.size() == data.size()) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(position, data.size());
            }
        }

        public void remove(@IntRange(from = 0) int position) {
            mData.remove(position);
            if (mData.isEmpty()) {
                notifyDataSetChanged();
            } else {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size() - position);
            }
        }

        public T getCurrent() {
            return mCurrent;
        }

        public void setCurrent(int pos) {
            if (mCurrent != null && mCurrent.equals(mData.get(pos))) {
                return;
            }
            mCurrent = mData.get(pos);
            // Invoke callback of on item click.
            if (mListener != null) {
                mListener.onItemSelected(this, mCurrent, pos);
            }
            mTitleHolder.setUpView(mCurrent, pos);
        }

        protected abstract BaseTitleViewHolder<T> createTitle(View view);

        protected abstract BaseItemViewHolder<T> createItem(View view);

        DropdownMenu.OnItemClickListener<T> getOnItemClickListener() {
            return mListener;
        }

        public void setOnItemClickListener(DropdownMenu.OnItemClickListener<T> listener) {
            mListener = listener;
        }

    }

    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

        protected Context mContext;
        private SparseArray<View> mViews = new SparseArray<>();

        BaseViewHolder(View v) {
            super(v);
            mContext = v.getContext();
        }

        @SuppressWarnings("unchecked")
        protected <V extends View> V getView(int resId) {
            View view = mViews.get(resId);
            if (view == null) {
                view = itemView.findViewById(resId);
                if (view == null) {
                    return null;
                }
                mViews.put(resId, view);
            }
            return (V) view;
        }

        public abstract void setUpView(T model, int position);
    }

    public static abstract class BaseTitleViewHolder<T> extends BaseViewHolder<T> {

        protected BaseTitleViewHolder(View v) {
            super(v);
        }
    }

    public static abstract class BaseItemViewHolder<T> extends BaseViewHolder<T> {
        protected BaseItemViewHolder(View v, final BaseAdapter<T> adapter) {
            super(v);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get model at this position.
                    int pos = getLayoutPosition();
                    // Update current in adapter.
                    adapter.setCurrent(pos);
                    // Dismiss popup
                    adapter.mPopup.dismiss(true);
                }
            });
        }
    }
}
