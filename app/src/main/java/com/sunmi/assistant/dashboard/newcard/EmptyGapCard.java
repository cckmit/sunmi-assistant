package com.sunmi.assistant.dashboard.newcard;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class EmptyGapCard extends BaseRefreshCard<EmptyGapCard.Model, Object> {

    private static EmptyGapCard sInstance;

    private EmptyGapCard(DashboardContract.Presenter presenter, int source) {
        super(presenter, source);
    }

    public static EmptyGapCard init(DashboardContract.Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new EmptyGapCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_gap;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        callback.onSuccess();
        return null;
    }

    @Override
    protected List<Model> createModel() {
        ArrayList<Model> models = new ArrayList<>();
        models.add(new Model());
        return models;
    }

    @Override
    protected void setupModel(List<Model> models, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        lp.height = model.height;
        holder.itemView.setBackgroundColor(model.color);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int color;
        private int height;

        public void setColor(int color) {
            this.color = color;
        }

        public void setHeight(int height) {
            this.height = height;
        }

    }
}
