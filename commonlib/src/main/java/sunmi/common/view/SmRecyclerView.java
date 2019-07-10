package sunmi.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Description:
 * Created by bruce on 2019/7/4.
 */
public class SmRecyclerView extends RecyclerView {
    public SmRecyclerView(@NonNull Context context) {
        super(context);
    }

    public SmRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(int deviderDrawableRes) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        if (deviderDrawableRes > 0) {
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            decoration.setDrawable(getResources().getDrawable(deviderDrawableRes));
            addItemDecoration(decoration);
        }
    }

}
