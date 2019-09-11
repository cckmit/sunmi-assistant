package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.order.OrderListActivity_;
import com.sunmi.assistant.utils.Utils;

import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;

/**
 * @author yinhui
 * @date 2019-07-22
 */
public abstract class BaseSmallCard<Model extends BaseSmallCard.BaseSmallModel, Resp>
        extends BaseRefreshCard<Model, Resp> {

    protected BaseSmallCard(Context context, DashboardContract.Presenter presenter,
                            int companyId, int shopId, int orderType) {
        super(context, presenter, companyId, shopId);
        setOnItemClickListener((adapter, holder, model, position) -> goToOrderList(context, orderType));
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_old_data_card;
    }

    @Override
    public int getSpanSize() {
        return 1;
    }

    @Override
    protected void onPrePeriodChange(Model model, int period) {
        if (model.isValid) {
            model.skipLoad = true;
            updateView();
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

        model.trendName = Utils.getTrendNameByPeriod(mContext, model.period);
        String trendData = model.getTrendData(getPeriod());

        TextView tvTitle = holder.getView(R.id.tv_dashboard_title);
        TextView tvData = holder.getView(R.id.tv_dashboard_data);
        TextView tvTrendName = holder.getView(R.id.tv_dashboard_trend_name);
        TextView tvTrendData = holder.getView(R.id.tv_dashboard_trend_data);

        tvTitle.setText(model.title);
        tvData.setText(String.format(Locale.getDefault(), getDataFormat(), model.getData(getPeriod())));
        tvTrendName.setText(model.trendName);

        if (TextUtils.equals(trendData, DATA_NONE)) {
            tvTrendData.setText(trendData);
            tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            tvTrendData.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.color_333338));
            return;
        }

        float trendDataFloat = Float.valueOf(trendData);
        String trendDataFormat = FORMAT_MAX_DOUBLE_DECIMAL.format(Math.abs(trendDataFloat * 100));
        tvTrendData.setText(holder.getContext().getResources()
                .getString(R.string.dashboard_data_format, trendDataFormat));

        if (TextUtils.equals(trendDataFormat, DATA_ZERO)) {
            tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            tvTrendData.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.color_333338));
        } else if (trendDataFloat > 0) {
            tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.dashboard_ic_trend_up, 0, 0, 0);
            tvTrendData.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.color_FF0000));
        } else {
            tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.dashboard_ic_trend_down, 0, 0, 0);
            tvTrendData.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.color_00B552));
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.GONE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

        model.trendName = Utils.getTrendNameByPeriod(mContext, model.period);

        TextView tvTitle = holder.getView(R.id.tv_dashboard_title);
        TextView tvData = holder.getView(R.id.tv_dashboard_data);
        TextView tvTrendName = holder.getView(R.id.tv_dashboard_trend_name);
        TextView tvTrendData = holder.getView(R.id.tv_dashboard_trend_data);

        tvTitle.setText(model.title);
        tvData.setText(DATA_NONE);
        tvTrendName.setText(model.trendName);
        tvTrendData.setText(DATA_NONE);
        tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        tvTrendData.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.color_333338));
    }

    private void goToOrderList(Context context, int orderType) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(getPeriod());
        OrderListActivity_.intent(context)
                .mTimeStart(periodTimestamp.first)
                .mTimeEnd(periodTimestamp.second)
                .mInitOrderType(orderType)
                .start();
    }

    /**
     * 卡片主数据数字格式
     *
     * @return String.format可用的格式字符串
     */
    protected abstract String getDataFormat();

    abstract static class BaseSmallModel extends BaseRefreshCard.BaseModel {
        String title;
        float dataToday;
        float dataWeek;
        float dataMonth;
        String trendName;
        String trendDataToday;
        String trendDataWeek;
        String trendDataMonth;

        BaseSmallModel(String title) {
            this.title = title;
        }

        float getData(int period) {
            if (period == Constants.TIME_PERIOD_TODAY) {
                return dataToday;
            } else if (period == Constants.TIME_PERIOD_WEEK) {
                return dataWeek;
            } else {
                return dataMonth;
            }
        }

        String getTrendData(int period) {
            if (period == Constants.TIME_PERIOD_TODAY) {
                return trendDataToday;
            } else if (period == Constants.TIME_PERIOD_WEEK) {
                return trendDataWeek;
            } else {
                return trendDataMonth;
            }
        }
    }
}
