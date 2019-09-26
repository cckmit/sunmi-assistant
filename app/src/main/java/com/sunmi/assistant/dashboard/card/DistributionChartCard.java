package com.sunmi.assistant.dashboard.card;

import android.content.Context;
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
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.ui.PieChartMarkerView;
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
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class DistributionChartCard extends BaseRefreshItem<DistributionChartCard.Model, Object> {

    private static final int[] PIE_COLORS_NEW_OLD = {0xFF5A97FC, 0xFFFF8000};
    private static final int[] PIE_COLORS_GENDER = {0xFF4B7AFA, 0xFFFF6666};
    private static final int[] PIE_COLORS_AGE = {0xFFFADD4B, 0xFF45E6B0, 0xFF4BC0FA, 0xFF4B85FA, 0xFF7A62F5, 0xFFB87AF5, 0xFFFF6680, 0xFFFF884D};
    private static final int[] PIE_COLORS_EMPTY = {0xFFCED2D9};

    private PieChart mChart;
    private SparseArray<String> mAgeList;
    private OnPieSelectedListener mOnSelectedListener;

    public DistributionChartCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter);
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
        String start = DateFormat.format(DATE_FORMAT, time.first * 1000).toString();
        String end = DateFormat.format(DATE_FORMAT, time.second * 1000 - 1).toString();
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
                if (data == null) {
                    onFail(code, msg, null);
                    return;
                }
                List<FaceAge> list = data.getAgeRangeList();
                if (list == null) {
                    onFail(code, msg, data);
                    return;
                }
                Collections.sort(list, (o1, o2) -> (o1.getCode() - o2.getCode()));
                mAgeList = new SparseArray<>(list.size());
                for (FaceAge age : list) {
                    mAgeList.put(age.getCode(),
                            mContext.getString(R.string.dashboard_chart_age_label, age.getName()));
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
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        List<ConsumerAgeNewOldResp.CountListBean> list = data.getCountList();
                        if (list == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        Model model = getModel();
                        List<PieEntry> newOldList = model.dataSets.get(Constants.DATA_TYPE_NEW_OLD);
                        List<PieEntry> ageList = model.dataSets.get(Constants.DATA_TYPE_AGE);
                        newOldList.clear();
                        ageList.clear();
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
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        List<ConsumerAgeGenderResp.CountListBean> list = data.getCountList();
                        if (list == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        Model model = getModel();
                        List<PieEntry> genderList = model.dataSets.get(Constants.DATA_TYPE_GENDER);
                        genderList.clear();
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
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();
        mChart = holder.getView(R.id.view_dashboard_pie_chart);

        mChart.setTouchEnabled(true);
        mChart.setRotationEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawEntryLabels(false);
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(75f);
        mChart.setTransparentCircleRadius(0f);
        mChart.setExtraOffsets(10, 13, 10, 13);
        mChart.setCenterTextSize(24);
        mChart.setCenterTextColor(ContextCompat.getColor(context, R.color.color_525866));

        mOnSelectedListener = new OnPieSelectedListener(mChart);
        mChart.setOnChartValueSelectedListener(mOnSelectedListener);

        return holder;
    }

    @Override
    protected void setupModel(Model model, Object response) {
//        List<PieEntry> entries = model.dataSets.get(Constants.DATA_TYPE_NEW_OLD);
//        for (PieEntry entry : entries) {
//            entry.setY((int)(Math.random() * 1000));
//        }
//
//        model.dataSets.get(Constants.DATA_TYPE_GENDER).clear();
//
//        entries = model.dataSets.get(Constants.DATA_TYPE_AGE);
//        for (PieEntry entry : entries) {
//            entry.setY((int)(Math.random() * 1000));
//        }
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
        newOld.setTypeface(null, model.type == Constants.DATA_TYPE_NEW_OLD ? Typeface.BOLD : Typeface.NORMAL);
        gender.setSelected(model.type == Constants.DATA_TYPE_GENDER);
        gender.setTypeface(null, model.type == Constants.DATA_TYPE_GENDER ? Typeface.BOLD : Typeface.NORMAL);
        age.setSelected(model.type == Constants.DATA_TYPE_AGE);
        age.setTypeface(null, model.type == Constants.DATA_TYPE_AGE ? Typeface.BOLD : Typeface.NORMAL);

        // Get data set from model
        List<PieEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }
        LogCat.d(TAG, "Period=" + model.period + "; type=" + model.type + "\nData set:" + dataSet);

        // Highlight largest data part
        int maxIndex = -1;
        float max = 0;
        float total = 0;
        for (int i = 0, size = dataSet.size(); i < size; i++) {
            float value = dataSet.get(i).getValue();
            total += value;
            if (max < value) {
                max = value;
                maxIndex = i;
            }
        }
        mOnSelectedListener.setMax((int) total);

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
            values.clear();
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
            set.setSelectionInnerShift(6f);
            set.setUsingSliceColorAsHighlightShadowColor(true);
            set.setHighlightShadowColorAlpha(0.4f);
            set.setHighlightShadow(8f, 0f, 4f);
            set.setValueLineStartDrawCircles(true);
            set.setValueLineStartDrawCircleHole(true);
            set.setValueLineStartCircleHoleRadius(1f);
            set.setValueLineStartCircleRadius(2f);
            set.setValueLinePart1OffsetPercentage(160f);
            set.setValueLinePart1Length(0.45f);
            set.setValueLinePart2Offset(0);
            set.setValueLineAlignParent(true);
            set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            PieChartMarkerView marker = new PieChartMarkerView(holder.getContext());
            marker.setChartView(pie);
            set.setValueMarker(marker);
            set.setDrawValuesAbove(0.095f);
            data = new PieData(set);
            pie.setData(data);
        }
        pie.animateY(300, Easing.EaseOutCubic);
        if (maxIndex >= 0 && !isEmpty) {
            pie.highlightValue(maxIndex, 0);
        } else {
            pie.highlightValues(null);
        }
    }

    public static class OnPieSelectedListener implements OnChartValueSelectedListener {

        private PieChart chart;
        private int total;

        public OnPieSelectedListener(PieChart chart) {
            this.chart = chart;
        }

        public void setMax(int max) {
            this.total = max;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            PieEntry entry = (PieEntry) e;
            chart.setCenterText(createCenterText(chart.getContext(), entry.getLabel(), entry.getValue()));
        }

        @Override
        public void onNothingSelected() {
            chart.setCenterText("");
        }

        private SpannableString createCenterText(Context context, String name, float value) {
            String title = context.getString(R.string.dashboard_chart_pie_hole_ratio, name);
            int percent = total > 0 ? Math.round(value / total * 100) : 0;
            SpannableString s = new SpannableString(
                    new StringBuilder(title).append("\n").append(percent).append("%"));
            s.setSpan(new AbsoluteSizeSpan(32, true), 0, title.length(), 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, title.length(), 0);
            s.setSpan(new ForegroundColorSpan(0xFF777E8C), 0, title.length(), 0);

            s.setSpan(new AbsoluteSizeSpan(60, true), title.length(), s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), title.length(), s.length(), 0);
            s.setSpan(new ForegroundColorSpan(0xFF525866), title.length(), s.length(), 0);
            return s;
        }

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
