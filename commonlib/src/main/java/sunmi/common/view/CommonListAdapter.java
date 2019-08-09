package sunmi.common.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shijie.yang on 2017/9/21.
 */
public abstract class CommonListAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mList;
    protected LayoutInflater mInflater;

    /**
     * @param context  上下文
     * @param layoutId layout
     * @param list     列表数据
     */
    public CommonListAdapter(Context context, int layoutId, List<T> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mList = list;
    }

    public List<T> getData() {
        return mList;
    }

    public void setData(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //通过实体类获取
//        T bean=mList.get(position);
//        convert(holder, bean);
        convert(holder, mList.get(position));
    }

    public abstract void convert(ViewHolder holder, T t);

    /**
     * 列表数据的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

}