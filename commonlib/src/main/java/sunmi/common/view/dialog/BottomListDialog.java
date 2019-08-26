package sunmi.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.commonlibrary.R;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;

/**
 * @author yinhui
 * @date 2019-08-23
 */
public class BottomListDialog extends Dialog {

    private BottomListDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * builder class for creating a custom dialog
     */
    public static class Builder<T> extends BottomDialog.Builder {

        private SimpleArrayAdapter<T> mAdapter;
        private OnItemClickListener<T> mListener;
        private float maxHeightCount = 6.5f;
        private boolean dividerShow = true;
        private int dividerDrawable = -1;
        private int dividerColor = 0x1A000000;
        private int dividerSize;

        public Builder(Context context) {
            super(context);
            dividerSize = (int) context.getResources().getDimension(R.dimen.dp_0_5);
        }

        public Builder<T> setAdapter(SimpleArrayAdapter<T> adapter) {
            this.mAdapter = adapter;
            return this;
        }

        public Builder<T> setOnItemClickListener(OnItemClickListener<T> listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * 设置对话框最大高度，单位是Item数量，可以非整数。
         *
         * @param count 最大高度时展示的Item数量
         * @return 建造者
         */
        public Builder<T> setMaxHeight(float count) {
            this.maxHeightCount = count;
            return this;
        }

        public Builder<T> setDividerShow(boolean isShow) {
            this.dividerShow = isShow;
            return this;
        }

        public Builder<T> setDividerDrawable(@DrawableRes int drawableResId) {
            this.dividerDrawable = drawableResId;
            return this;
        }

        public Builder<T> setDividerColor(@ColorInt int color) {
            this.dividerColor = color;
            return this;
        }

        public Builder<T> setDividerSize(float size) {
            this.dividerSize = (int) size;
            return this;
        }

        public Builder<T> setDividerSize(@DimenRes int dimenResId) {
            this.dividerSize = (int) context.getResources().getDimension(dimenResId);
            return this;
        }

        @Override
        protected void setupContent(final Dialog dialog, FrameLayout content) {
            if (mAdapter == null) {
                return;
            }
            if (mListener != null) {
                mAdapter.setOnItemClickListener(new sunmi.common.base.recycle.listener.OnItemClickListener<T>() {
                    @Override
                    public void onClick(BaseRecyclerAdapter<T> adapter, BaseViewHolder<T> holder,
                                        T model, int position) {
                        dialog.dismiss();
                        mListener.onClick(dialog, holder, model);
                    }
                });
            }
            RecyclerView recycler = new RecyclerView(context);
            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(mAdapter);
            Drawable drawable = dividerDrawable == -1 ? null : ContextCompat.getDrawable(context, dividerDrawable);
            if (dividerShow) {
                DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                if (drawable != null) {
                    decoration.setDrawable(drawable);
                } else {
                    GradientDrawable divider = new GradientDrawable();
                    divider.setShape(GradientDrawable.RECTANGLE);
                    divider.setSize(ViewGroup.LayoutParams.MATCH_PARENT, dividerSize);
                    divider.setColor(dividerColor);
                    decoration.setDrawable(divider);
                }
                recycler.addItemDecoration(decoration);
            }
            content.addView(recycler, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }


    }

    private static class ListLayoutManager extends LinearLayoutManager {

        private float maxHeight;

        private ListLayoutManager(Context context, float maxHeight) {
            super(context);
            this.maxHeight = maxHeight;
        }

        @Override
        public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state,
                              int widthSpec, int heightSpec) {
            if (getChildCount() == 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }
            View firstChildView = recycler.getViewForPosition(0);
            measureChild(firstChildView, widthSpec, heightSpec);
            int itemHeight = firstChildView.getMeasuredHeight();
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), getChildCount() > (int) maxHeight ?
                    (int) (itemHeight * maxHeight) : itemHeight * getChildCount());
        }
    }

    public interface OnItemClickListener<T> {
        /**
         * This method will be invoked when a item in the list is clicked.
         *
         * @param dialog the dialog that received the click
         * @param holder the view holder of the item that received the click
         * @param model  the model bound to the item that received clicked.
         */
        void onClick(DialogInterface dialog, BaseViewHolder<T> holder, T model);
    }

}
