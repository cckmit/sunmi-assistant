package com.sunmi.ipc.cash.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.CashTagManager;
import com.sunmi.ipc.cash.model.CashTagFilter;
import com.sunmi.ipc.model.CashTag;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.utils.ToastUtils;
import sunmi.common.view.TextLengthWatcher;

/**
 * @author yinhui
 * @date 2019-12-25
 */
public class CashTagAdapter extends SimpleArrayAdapter<CashTagFilter> {

    public static final int CUSTOM_TAG_MAX_LENGTH = 18;

    private View mRoot;
    private RecyclerView mRvList;
    private EditText mEtCustom;

    private CashTagFilter mSelected;

    @SuppressLint("InflateParams")
    public CashTagAdapter(Context context) {
        mRoot = LayoutInflater.from(context).inflate(R.layout.cash_video_dialog_abnormal_tag, null);
        mRvList = mRoot.findViewById(R.id.rvTagList);
        mEtCustom = mRoot.findViewById(R.id.etCustom);
        mEtCustom.addTextChangedListener(new TextLengthWatcher(mEtCustom, CUSTOM_TAG_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                ToastUtils.toastForShort(context, R.string.ipc_cash_tag_length_tip);
            }
        });
        initList(context);
        initRecyclerView(context);
    }

    private void initList(Context context) {
        List<CashTag> tags = CashTagManager.get(context).getTags();

        List<CashTagFilter> list = new ArrayList<>();
        list.add(new CashTagFilter(CashTagFilter.TAG_ID_NORMAL, context.getString(R.string.cash_tag_name_normal)));
        for (CashTag tag : tags) {
            list.add(new CashTagFilter(tag.getTag(), tag.getName()));
        }
        list.add(new CashTagFilter(CashTagFilter.TAG_ID_CUSTOM, context.getString(R.string.cash_tag_name_custom)));
        setData(list);
    }

    private void initRecyclerView(Context context) {
        int horizontalGap = (int) context.getResources().getDimension(R.dimen.dp_10);
        int verticalGap = (int) context.getResources().getDimension(R.dimen.dp_8);
        mRvList.setLayoutManager(new GridLayoutManager(context, 2));
        mRvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(horizontalGap, verticalGap, horizontalGap, verticalGap);
            }
        });
        mRvList.setAdapter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.cash_video_abnormal_tag_item;
    }

    @NonNull
    @Override
    public BaseViewHolder<CashTagFilter> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder<CashTagFilter> holder = super.onCreateViewHolder(parent, viewType);
        holder.setOnItemClickListener((h, model, position) -> {
            if (mSelected == model) {
                return;
            }
            if (mSelected != null) {
                mSelected.setChecked(false);
            }
            model.setChecked(true);
            mSelected = model;
            if (mEtCustom != null) {
                mEtCustom.setVisibility(model.getId() == CashTagFilter.TAG_ID_CUSTOM ? View.VISIBLE : View.INVISIBLE);
            }
            if (model.getId() != CashTagFilter.TAG_ID_CUSTOM) {
                ((InputMethodManager) h.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(h.itemView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            notifyDataSetChanged();
        });
        return holder;

    }

    @Override
    public void setupView(@NonNull BaseViewHolder<CashTagFilter> holder, CashTagFilter model, int position) {
        TextView itemView = (TextView) holder.itemView;
        itemView.setText(model.getName());
        itemView.setSelected(model.isChecked());
    }

    public View getRootView() {
        return mRoot;
    }

    public CashTagFilter getSelected() {
        if (mSelected.getId() == CashTagFilter.TAG_ID_CUSTOM) {
            mSelected.setDesc(mEtCustom.getText().toString());
        }
        return mSelected;
    }

    public void setSelected(int id) {
        List<CashTagFilter> data = getData();
        if (data == null || data.isEmpty()) {
            return;
        }
        for (CashTagFilter item : data) {
            if (item.getId() == id) {
                item.setChecked(true);
                mSelected = item;
            } else {
                item.setChecked(false);
            }
        }
        if (mSelected == null) {
            return;
        }
        if (mSelected.getId() == CashTagFilter.TAG_ID_CUSTOM) {
            mEtCustom.setVisibility(View.VISIBLE);
            mEtCustom.setText(mSelected.getDesc());
        } else {
            mEtCustom.setVisibility(View.INVISIBLE);
            mEtCustom.setText("");
        }
        notifyDataSetChanged();
    }

    public void setCustom(String desc) {
        List<CashTagFilter> data = getData();
        if (data == null || data.isEmpty()) {
            return;
        }
        for (CashTagFilter item : data) {
            if (item.getId() == CashTagFilter.TAG_ID_CUSTOM) {
                item.setChecked(true);
                mSelected = item;
            } else {
                item.setChecked(false);
            }
        }
        mSelected.setDesc(desc);
        if (mSelected == null) {
            return;
        }
        mEtCustom.setVisibility(View.VISIBLE);
        mEtCustom.setText(desc);
        notifyDataSetChanged();
    }

}
