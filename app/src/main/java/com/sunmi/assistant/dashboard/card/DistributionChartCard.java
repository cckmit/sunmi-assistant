package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class DistributionChartCard extends BaseRefreshItem<DistributionChartCard.Model, Object> {


    public DistributionChartCard(Context context, DashboardContract.Presenter presenter, int source) {
        super(context, presenter, source);
        addOnViewClickListener(R.id.tv_dashboard_new_old, (adapter, holder, v, model, position) -> {
            model.type = Constants.DATA_TYPE_NEW_OLD;
            updateView();
        });
        addOnViewClickListener(R.id.tv_dashboard_gender, (adapter, holder, v, model, position) -> {
            model.type = Constants.DATA_TYPE_GENDER;
            updateView();
        });
        addOnViewClickListener(R.id.tv_dashboard_age, (adapter, holder, v, model, position) -> {
            model.type = Constants.DATA_TYPE_AGE;
            updateView();
        });
    }

    @Override
    protected Model createModel(Context context) {
        return new Model("", Constants.DATA_TYPE_NEW_OLD);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_distribution;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        callback.onSuccess();
        return null;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
        Context context = view.getContext();
        PieChart chart = holder.getView(R.id.view_dashboard_pie_chart);


        return holder;
    }

    @Override
    protected void setupModel(Model model, Object response) {
        model.isValid = true;
        model.type = Constants.DATA_TYPE_NEW_OLD;
        model.period = Constants.TIME_PERIOD_TODAY;
        List<PieEntry> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i < 5) {
                list.add(new PieEntry(0, "part" + i));
            } else {
                list.add(new PieEntry((float) Math.random() * 1000, "part" + i));
            }
        }
        model.dataSets.put(Constants.DATA_TYPE_NEW_OLD, list);
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i < 5) {
                list.add(new PieEntry(0, "part" + i));
            } else {
                list.add(new PieEntry((float) Math.random() * 1000, "part" + i));
            }
        }
        model.dataSets.put(Constants.DATA_TYPE_GENDER, list);
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i < 5) {
                list.add(new PieEntry(0, "part" + i));
            } else {
                list.add(new PieEntry((float) Math.random() * 1000, "part" + i));
            }
        }
        model.dataSets.put(Constants.DATA_TYPE_AGE, list);
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Get views
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView newOld = holder.getView(R.id.tv_dashboard_new_old);
        TextView gender = holder.getView(R.id.tv_dashboard_gender);
        TextView age = holder.getView(R.id.tv_dashboard_age);

        PieChart pie = holder.getView(R.id.view_dashboard_pie_chart);

        // Set button selected
        newOld.setSelected(model.type == Constants.DATA_TYPE_NEW_OLD);
        gender.setSelected(model.type == Constants.DATA_TYPE_GENDER);
        age.setSelected(model.type == Constants.DATA_TYPE_AGE);

        // Get data set from model
        List<PieEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }

        // Refresh data set
        PieDataSet set;
        PieData data = pie.getData();
        ArrayList<PieEntry> values = new ArrayList<>(dataSet);
        if (data != null && data.getDataSetCount() > 0) {
            set = (PieDataSet) data.getDataSetByIndex(0);
            set.setValues(values);
            data.notifyDataChanged();
            pie.notifyDataSetChanged();
        } else {
            set = new PieDataSet(values, "data");
            data = new PieData(set);
            pie.setData(data);
        }
        pie.animateY(300, Easing.EaseOutCubic);
    }

    public static class Model extends BaseRefreshItem.BaseModel {
        private String title;
        private int type;
        private SparseArray<List<PieEntry>> dataSets = new SparseArray<>(3);

        public Model(String title, int type) {
            this.title = title;
            this.type = type;
        }

        public void clear() {
            dataSets.clear();
        }
    }
}
