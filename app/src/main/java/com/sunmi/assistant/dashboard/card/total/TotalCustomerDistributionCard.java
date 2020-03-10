package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.ui.chart.PieChartMarkerView;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CountListBean;
import sunmi.common.model.CustomerDistributionResp;
import sunmi.common.model.Interval;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CacheManager;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TotalCustomerDistributionCard extends BaseRefreshCard<TotalCustomerDistributionCard.Model, Object> {

    private static final int NUM_10_THOUSANDS = 10000;

    private static final int[] PIE_COLORS_NEW_OLD = {0xFF5A97FC, 0xFFFF8000};
    private static final int[] PIE_COLORS_GENDER = {0xFF4B7AFA, 0xFFFF6666};
    private static final int[] PIE_COLORS_AGE = {0xFFFADD4B, 0xFF45E6B0, 0xFF4BC0FA, 0xFF4B85FA, 0xFF7A62F5, 0xFFB87AF5, 0xFFFF6680, 0xFFFF884D};
    private static final int[] PIE_COLORS_EMPTY = {0xFFCED2D9};

    private static TotalCustomerDistributionCard sInstance;

    private String mAgeLabel;
    private String mNewLabel;
    private String mOldLabel;
    private String mMaleLabel;
    private String mFemaleLabel;

    private PieChart mChart;
    private SparseArray<FaceAge> mAgeList;
    private OnPieSelectedListener mOnSelectedListener;

    private TotalCustomerDistributionCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static TotalCustomerDistributionCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new TotalCustomerDistributionCard(presenter, condition, period, periodTime);
        } else {
            sInstance.reset(presenter, condition, period, periodTime);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
        mAgeLabel = context.getString(R.string.dashboard_unit_age);
        mNewLabel = context.getString(R.string.dashboard_var_customer_new);
        mOldLabel = context.getString(R.string.dashboard_var_customer_old);
        mMaleLabel = context.getString(R.string.dashboard_var_male);
        mFemaleLabel = context.getString(R.string.dashboard_var_female);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_total_customer_distribution;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
        String time = Utils.formatTime(Utils.FORMAT_API_DATE, periodTime.start);
        if (mAgeList == null) {
            loadAgeList(companyId, period, time, callback);
        } else {
            loadNewOld(companyId, period, time, callback);
        }
        return null;
    }

    private void loadAgeList(int companyId, int period, String time, CardCallback callback) {
        mAgeList = CacheManager.get().get(CacheManager.CACHE_AGE_NAME);
        if (mAgeList != null) {
            loadNewOld(companyId, period, time, callback);
            return;
        }
        IpcCloudApi.getInstance().getFaceAgeRange(new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                if (data == null || data.getAgeRangeList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<FaceAge> list = data.getAgeRangeList();
                mAgeList = new SparseArray<>(list.size());
                int size = 4;
                for (FaceAge age : list) {
                    mAgeList.put(age.getCode(), age);
                    size += age.getName().length() * 2 + 8;
                }
                CacheManager.get().put(CacheManager.CACHE_AGE_NAME, mAgeList, size);
                loadNewOld(companyId, period, time, callback);
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                callback.onFail(code, msg, data);
            }
        });
    }

    private void loadNewOld(int companyId, int period, String time, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalCustomerNewOldDistribution(companyId, time, period,
                new RetrofitCallback<CustomerDistributionResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerDistributionResp data) {
                        if (data == null || data.getCountList() == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        List<CountListBean> list = data.getCountList();
                        Model model = getModel();
                        List<PieEntry> newOldList = model.dataSets.get(Constants.DATA_TYPE_NEW_OLD);
                        List<PieEntry> ageList = model.dataSets.get(Constants.DATA_TYPE_AGE);
                        newOldList.clear();
                        ageList.clear();
                        int newCount = 0;
                        int oldCount = 0;
                        for (CountListBean item : list) {
                            newCount += item.getStrangerUniqCount();
                            oldCount += item.getRegularUniqCount();
                            int ageCount = item.getStrangerUniqCount() + item.getRegularUniqCount();
                            ageList.add(new PieEntry(ageCount, mAgeList.get(item.getAgeRangeCode()).getName() + mAgeLabel));
                        }
                        newOldList.add(new PieEntry(newCount, mNewLabel));
                        newOldList.add(new PieEntry(oldCount, mOldLabel));
                        loadGender(companyId, period, time, callback);
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerDistributionResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    private void loadGender(int companyId, int period, String time, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalCustomerGenderDistribution(companyId, time, period,
                new RetrofitCallback<CustomerDistributionResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerDistributionResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        List<CountListBean> list = data.getCountList();
                        if (list == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        Model model = getModel();
                        List<PieEntry> genderList = model.dataSets.get(Constants.DATA_TYPE_GENDER);
                        genderList.clear();
                        int maleCount = 0;
                        int femaleCount = 0;
                        for (CountListBean item : list) {
                            int gender = item.getGender();
                            if (gender == Constants.GENDER_MALE) {
                                maleCount += item.getUniqCount();
                            } else {
                                femaleCount += item.getUniqCount();
                            }
                        }
                        genderList.add(new PieEntry(maleCount, mMaleLabel));
                        genderList.add(new PieEntry(femaleCount, mFemaleLabel));
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerDistributionResp data) {
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
        mChart.setRotationEnabled(true);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawEntryLabels(false);
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(75f);
        mChart.setTransparentCircleRadius(0f);
        mChart.setExtraOffsets(10, 13, 10, 13);
        mChart.setCenterTextSize(20);
        mChart.setCenterTextColor(ContextCompat.getColor(context, R.color.text_normal));

        mOnSelectedListener = new OnPieSelectedListener(mChart);
        mChart.setOnChartValueSelectedListener(mOnSelectedListener);

        holder.addOnClickListener(R.id.tv_dashboard_new_old, (h, model, position) -> {
            model.type = Constants.DATA_TYPE_NEW_OLD;
            updateViews();
        });
        holder.addOnClickListener(R.id.tv_dashboard_gender, (h, model, position) -> {
            model.type = Constants.DATA_TYPE_GENDER;
            updateViews();
        });
        holder.addOnClickListener(R.id.tv_dashboard_age, (h, model, position) -> {
            model.type = Constants.DATA_TYPE_AGE;
            updateViews();
        });
        holder.addOnClickListener(R.id.btn_more, (holder1, model, position) -> {
            // TODO: add get more.
        });

        return holder;
    }

    @Override
    protected Model createModel() {
        return new Model("", Constants.DATA_TYPE_NEW_OLD);
    }

    @Override
    protected void setupModel(Model model, Object response) {
        // Test data
//        models.get(0).random();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Get views
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView newOld = holder.getView(R.id.tv_dashboard_new_old);
        TextView gender = holder.getView(R.id.tv_dashboard_gender);
        TextView age = holder.getView(R.id.tv_dashboard_age);

        PieChart pie = holder.getView(R.id.view_dashboard_pie_chart);
        pie.setRotationAngle(270);

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
            pie.setCenterText(Utils.formatPercent(0f, false, true));
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
            setupDataSet(pie, set, colors, isEmpty);
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

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        PieChart pie = holder.getView(R.id.view_dashboard_pie_chart);
        model.period = mPeriod;
        model.dataSets.get(model.type).clear();
        pie.setCenterText("");
        PieDataSet set;
        PieData data = pie.getData();
        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(1f));
        if (data != null && data.getDataSetCount() > 0) {
            set = (PieDataSet) data.getDataSetByIndex(0);
            set.setColors(PIE_COLORS_EMPTY);
            set.setDrawValues(false);
            set.setValues(values);
            data.notifyDataChanged();
            pie.notifyDataSetChanged();
        } else {
            set = new PieDataSet(values, "data");
            setupDataSet(pie, set, PIE_COLORS_EMPTY, true);
            data = new PieData(set);
            pie.setData(data);
        }
        pie.highlightValues(null);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        PieChart pie = holder.getView(R.id.view_dashboard_pie_chart);
        model.period = mPeriod;
        model.dataSets.get(model.type).clear();
        pie.setCenterText("");
        PieDataSet set;
        PieData data = pie.getData();
        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(1f));
        if (data != null && data.getDataSetCount() > 0) {
            set = (PieDataSet) data.getDataSetByIndex(0);
            set.setColors(PIE_COLORS_EMPTY);
            set.setDrawValues(false);
            set.setValues(values);
            data.notifyDataChanged();
            pie.notifyDataSetChanged();
        } else {
            set = new PieDataSet(values, "data");
            setupDataSet(pie, set, PIE_COLORS_EMPTY, true);
            data = new PieData(set);
            pie.setData(data);
        }
        pie.highlightValues(null);
    }

    private void setupDataSet(PieChart pie, PieDataSet set, int[] colors, boolean isEmpty) {
        set.setColors(colors);
        set.setDrawValues(!isEmpty);
        set.setDrawIcons(false);
        set.setUsingSliceColorAsValueLineColor(true);
        set.setSliceSpace(0f);
        set.setSelectionShift(5f);
        set.setSelectionInnerShift(5f);
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
        PieChartMarkerView marker = new PieChartMarkerView(pie.getContext());
        marker.setChartView(pie);
        set.setValueMarker(marker);
        set.setDrawValuesAbove(0.095f);
    }

    public static class OnPieSelectedListener implements OnChartValueSelectedListener {

        private PieChart chart;
        private int total;
        private String totalTitle;

        public OnPieSelectedListener(PieChart chart) {
            this.chart = chart;
            this.totalTitle = chart.getContext().getString(R.string.str_num_people_total);
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
            chart.setCenterText(createTotalText(chart.getContext(), total));
        }

        private SpannableString createCenterText(Context context, String name, float value) {
            if (name == null) {
                return new SpannableString("");
            }
            CharSequence percent = Utils.formatPercent(total > 0 ? value / total : 0f, false, true);
            SpannableString s = new SpannableString(
                    new StringBuilder(name).append("\n").append(percent));

            int titleLength = name.length();
            s.setSpan(new RelativeSizeSpan(0.6f), 0, titleLength, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, titleLength, 0);
            s.setSpan(new ForegroundColorSpan(0xFF777E8C), 0, titleLength, 0);

            s.setSpan(new StyleSpan(Typeface.BOLD), titleLength, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(0xFF525866), titleLength, s.length(), 0);

            s.setSpan(new RelativeSizeSpan(0.6f), s.length() - 1, s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), s.length() - 1, s.length(), 0);
            return s;
        }

        private SpannableString createTotalText(Context context, float value) {
            CharSequence countStr = Utils.formatNumber(context, total, false, false);
            int unit = CommonHelper.isGooglePlay() || total <= Utils.THRESHOLD_10THOUSAND ? 0 : 1;

            SpannableString s = new SpannableString(
                    new StringBuilder(totalTitle).append("\n").append(countStr));

            int titleLength = totalTitle.length();
            s.setSpan(new RelativeSizeSpan(0.6f), 0, titleLength, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, titleLength, 0);
            s.setSpan(new ForegroundColorSpan(0xFF777E8C), 0, titleLength, 0);

            s.setSpan(new StyleSpan(Typeface.BOLD), titleLength, s.length() - unit, 0);
            s.setSpan(new ForegroundColorSpan(0xFF525866), titleLength, s.length(), 0);

            if (unit != 0) {
                s.setSpan(new RelativeSizeSpan(0.6f), s.length() - unit, s.length(), 0);
                s.setSpan(new StyleSpan(Typeface.NORMAL), s.length() - unit, s.length(), 0);
            }
            return s;
        }

    }

    public static class Model extends BaseRefreshCard.BaseModel {
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

        @Override
        public void init(DashboardCondition condition) {
            for (int i = 0, size = dataSets.size(); i < size; i++) {
                int key = dataSets.keyAt(i);
                dataSets.get(key).clear();
            }
        }

        public void random() {
            List<PieEntry> entries = dataSets.get(Constants.DATA_TYPE_NEW_OLD);
            for (PieEntry entry : entries) {
                entry.setY((int) (Math.random() * 1000));
            }

            dataSets.get(Constants.DATA_TYPE_GENDER).clear();

            entries = dataSets.get(Constants.DATA_TYPE_AGE);
            for (PieEntry entry : entries) {
                entry.setY((int) (Math.random() * 1000));
            }
        }
    }
}
