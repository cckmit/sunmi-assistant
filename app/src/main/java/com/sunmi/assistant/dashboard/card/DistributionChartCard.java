package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.ui.PieChartLabelFormatter;
import com.sunmi.assistant.utils.Utils;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ConsumerAgeGenderResp;
import sunmi.common.model.ConsumerAgeNewOldResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class DistributionChartCard extends BaseRefreshItem<DistributionChartCard.Model, Object> implements OnChartValueSelectedListener {

    private static final int[] PIE_COLORS_NEW_OLD = {0xFF5A97FC, 0xFFFF8000};
    private static final int[] PIE_COLORS_GENDER = {0xFF4B7AFA, 0xFFFF6666};
    private static final int[] PIE_COLORS_AGE = {0xFFFFE14C, 0xFF99FF66, 0xFF4CC3FF, 0xFF4C88FF, 0xFF7F66FF, 0xFFBB7DFA, 0xFFFF6680, 0xFFFF9966};
    private static final int[] PIE_COLORS_EMPTY = {0xFFCED2D9};

    private PieChart mChart;
    private SparseArray<String> mAgeList;

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
        Pair<Long, Long> time = Utils.getPeriodTimestamp(period);
        String start = DateFormat.format(DATE_FORMAT, time.first).toString();
        String end = DateFormat.format(DATE_FORMAT, time.second).toString();
        if (mAgeList == null) {
            loadAgeList(companyId, shopId, start, end, callback);
        } else {
            loadNewOld(companyId, shopId, start, end, callback);
        }
        return null;
    }

    private void loadAgeList(int companyId, int shopId, String start, String end, CardCallback callback) {
        IpcCloudApi.getFaceAgeRange(companyId, shopId, new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                List<FaceAge> list = data.getAgeRangeList();
                Collections.sort(list, (o1, o2) -> (o1.getCode() - o2.getCode()));
                mAgeList = new SparseArray<>(list.size());
                for (FaceAge age : list) {
                    mAgeList.put(age.getCode(), age.getName());
                }
                loadNewOld(companyId, shopId, start, end, callback);
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                callback.onFail(code, msg, data);
            }
        });
    }

    private void loadNewOld(int companyId, int shopId, String start, String end, CardCallback callback) {
        SunmiStoreApi.getInstance().getConsumerByAgeNewOld(companyId, shopId, start, end,
                new RetrofitCallback<ConsumerAgeNewOldResp>() {
                    @Override
                    public void onSuccess(int code, String msg, ConsumerAgeNewOldResp data) {
                        Model model = getModel();
                        List<PieEntry> newOldList = model.dataSets.get(Constants.DATA_TYPE_NEW_OLD);
                        List<PieEntry> ageList = model.dataSets.get(Constants.DATA_TYPE_AGE);
                        newOldList.clear();
                        ageList.clear();
                        List<ConsumerAgeNewOldResp.CountListBean> list = data.getCountList();
                        int newCount = 0;
                        int oldCount = 0;
                        for (ConsumerAgeNewOldResp.CountListBean bean : list) {
                            newCount += bean.getStrangerCount();
                            oldCount += bean.getRegularCount();
                            int ageCount = bean.getRegularCount() + bean.getStrangerCount();
                            ageList.add(new PieEntry(ageCount, mAgeList.get(bean.getAgeRangeCode())));
                        }
                        String newName = mContext.getString(R.string.dashboard_chart_new);
                        String oldName = mContext.getString(R.string.dashboard_chart_old);
                        newOldList.add(new PieEntry(newCount, newName));
                        newOldList.add(new PieEntry(oldCount, oldName));
                        loadGender(companyId, shopId, start, end, callback);
                    }

                    @Override
                    public void onFail(int code, String msg, ConsumerAgeNewOldResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    private void loadGender(int companyId, int shopId, String start, String end, CardCallback callback) {
        SunmiStoreApi.getInstance().getConsumerByAgeGender(companyId, shopId, start, end,
                new RetrofitCallback<ConsumerAgeGenderResp>() {
                    @Override
                    public void onSuccess(int code, String msg, ConsumerAgeGenderResp data) {
                        Model model = getModel();
                        List<PieEntry> genderList = model.dataSets.get(Constants.DATA_TYPE_GENDER);
                        List<ConsumerAgeGenderResp.CountListBean> list = data.getCountList();
                        int maleCount = 0;
                        int femaleCount = 0;
                        for (ConsumerAgeGenderResp.CountListBean bean : list) {
                            maleCount += bean.getMaleCount();
                            femaleCount += bean.getFemaleCount();
                        }
                        String maleName = mContext.getString(R.string.dashboard_chart_male);
                        String femaleName = mContext.getString(R.string.dashboard_chart_female);
                        genderList.add(new PieEntry(maleCount, maleName));
                        genderList.add(new PieEntry(femaleCount, femaleName));
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, ConsumerAgeGenderResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
        Context context = view.getContext();
        mChart = holder.getView(R.id.view_dashboard_pie_chart);

        mChart.setTouchEnabled(true);
        mChart.setRotationEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawEntryLabels(false);
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(70f);
        mChart.setTransparentCircleRadius(0f);
        mChart.setExtraOffsets(10, 10, 10, 10);
        mChart.setCenterTextOffset(0, -5);
        mChart.setCenterTextSize(24);
        mChart.setCenterTextColor(ContextCompat.getColor(context, R.color.color_525866));

        mChart.setOnChartValueSelectedListener(this);

        return holder;
    }

    @Override
    protected void setupModel(Model model, Object response) {
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

        // Highlight largest data part
        int maxIndex = -1;
        float max = 0;
        for (int i = 0, size = dataSet.size(); i < size; i++) {
            float value = dataSet.get(i).getValue();
            if (max < value) {
                max = value;
                maxIndex = i;
            }
        }

        // Get color based by type
        int[] colors;
        boolean isEmpty = false;
        if (dataSet.isEmpty() || max <= 0) {
            colors = PIE_COLORS_EMPTY;
            isEmpty = true;
        } else if (model.type == Constants.DATA_TYPE_NEW_OLD) {
            colors = PIE_COLORS_NEW_OLD;
        } else if (model.type == Constants.DATA_TYPE_GENDER) {
            colors = PIE_COLORS_GENDER;
        } else {
            colors = PIE_COLORS_AGE;
        }

        // Handle empty data set
        if (isEmpty) {
            pie.setCenterText("0%");
            pie.setHighlightPerTapEnabled(false);
            mChart.setCenterTextOffset(0, 0);
        } else {
            pie.setHighlightPerTapEnabled(true);
            mChart.setCenterTextOffset(0, -5);
        }

        // Refresh data set
        PieDataSet set;
        PieData data = pie.getData();
        ArrayList<PieEntry> values = new ArrayList<>(dataSet);
        if (isEmpty) {
            values.add(new PieEntry(1f));
        }
        if (data != null && data.getDataSetCount() > 0) {
            set = (PieDataSet) data.getDataSetByIndex(0);
            set.setColors(colors);
            set.setDrawValues(!isEmpty);
            set.setValues(values);
            data.notifyDataChanged();
            pie.notifyDataSetChanged();
        } else {
            set = new PieDataSet(values, "data");
            set.setColors(colors);
            set.setDrawValues(!isEmpty);
            set.setDrawIcons(false);
            set.setUsingSliceColorAsValueLineColor(true);
            set.setSliceSpace(0f);
            set.setSelectionShift(6f);
            set.setValueLinePart1OffsetPercentage(135f);
            set.setValueLinePart1Length(0.45f);
            set.setValueLinePart2Length(1.3f);
            set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            set.setValueFormatter(new PieChartLabelFormatter());
            data = new PieData(set);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            pie.setData(data);
        }
        pie.animateY(300, Easing.EaseOutCubic);
        if (maxIndex >= 0 && !isEmpty) {
            pie.highlightValue(maxIndex, 0);
        } else {
            pie.highlightValues(null);
        }
    }

    private SpannableString createCenterText(Context context, String name, float value) {
        String title = context.getString(R.string.dashboard_chart_pie_hole_ratio, name);
        SpannableString s = new SpannableString(
                new StringBuilder(title).append("\n").append(Math.round(value * 100)).append("%"));
        s.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, title.length(), 0);
        s.setSpan(new ForegroundColorSpan(0xFF777E8C), 0, title.length(), 0);

        s.setSpan(new AbsoluteSizeSpan(48, true), title.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), title.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(0xFF525866), title.length(), s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry entry = (PieEntry) e;
        mChart.setCenterText(createCenterText(mContext, entry.getLabel(), entry.getValue()));
    }

    @Override
    public void onNothingSelected() {
        mChart.setCenterText("");
    }

    public static class Model extends BaseRefreshItem.BaseModel {
        private String title;
        private int type;
        private SparseArray<List<PieEntry>> dataSets = new SparseArray<>(3);

        public Model(String title, int type) {
            this.title = title;
            this.type = type;
            dataSets.put(Constants.DATA_TYPE_NEW_OLD, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_AGE, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_GENDER, new ArrayList<>());
        }

        public void clear() {
            dataSets.clear();
        }
    }
}
