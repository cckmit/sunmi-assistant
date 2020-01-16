package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerFrequencyAvgResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CacheManager;

/**
 * @author yinhui
 * @date 2020-01-15
 */
public class CustomerFrequencyAvg extends BaseRefreshCard<CustomerFrequencyAvg.Model, CustomerFrequencyAvgResp> {

    private static CustomerFrequencyAvg sInstance;

    private SparseArray<FaceAge> mAgeMap;
    private CommonAdapter<Item> mAdapter;

    private CustomerFrequencyAvg(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyAvg get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyAvg(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_customer_frequency_avg;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    protected Call<BaseResponse<CustomerFrequencyAvgResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        if (mAgeMap == null) {
            loadAgeList(companyId, shopId, period, callback);
        } else {
            loadAvg(companyId, shopId, period, callback);
        }
        return null;
    }

    private void loadAgeList(int companyId, int shopId, int period, CardCallback callback) {
        mAgeMap = CacheManager.get().get(CacheManager.CACHE_AGE_NAME);
        if (mAgeMap != null) {
            loadAvg(companyId, shopId, period, callback);
            return;
        }
        IpcCloudApi.getInstance().getFaceAgeRange(companyId, shopId, new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                if (data == null || data.getAgeRangeList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<FaceAge> list = data.getAgeRangeList();
                mAgeMap = new SparseArray<>(list.size());
                int size = 4;
                for (FaceAge age : list) {
                    mAgeMap.put(age.getCode(), age);
                    size += age.getName().length() * 2 + 8;
                }
                CacheManager.get().put(CacheManager.CACHE_AGE_NAME, mAgeMap, size);
                loadAvg(companyId, shopId, period, callback);
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                callback.onFail(code, msg, null);
            }
        });
    }

    private void loadAvg(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerFrequencyAvg(companyId, shopId, period, callback);
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyAvgResp response) {
        // Reset model data
        model.maleAvg = 0;
        model.femaleAvg = 0;
        if (model.ageMap == null) {
            model.ageMap = new SparseArray<>(mAgeMap.size());
            for (int i = 0, size = mAgeMap.size(); i < size; i++) {
                model.ageMap.put(mAgeMap.keyAt(i), new Item(mAgeMap.valueAt(i).getName()));
            }
        } else {
            for (int i = 0, size = model.ageMap.size(); i < size; i++) {
                Item item = model.ageMap.valueAt(i);
                item.max = 0f;
                item.maleFrequency = 0f;
                item.femaleFrequency = 0f;
            }
        }
        // Check response
        if (response == null || response.getFrequencyList() == null) {
            return;
        }
        // Arrange response data & setup model
        List<CustomerFrequencyAvgResp.Item> list = response.getFrequencyList();
        int maleTotal = 0;
        int maleUniqueTotal = 0;
        int femaleTotal = 0;
        int femaleUniqueTotal = 0;
        float max = 0f;
        for (CustomerFrequencyAvgResp.Item bean : list) {
            Item item = model.ageMap.get(bean.getAgeRangeCode());
            if (item == null) {
                continue;
            }
            float frequency = bean.getUniqPassengerCount() <= 0 ?
                    0f : (float) bean.getPassengerCount() / bean.getUniqPassengerCount();
            max = Math.max(max, frequency);
            if (bean.getGender() == Constants.GENDER_MALE) {
                item.maleFrequency = frequency;
                maleTotal += bean.getPassengerCount();
                maleUniqueTotal += bean.getUniqPassengerCount();
            } else {
                item.femaleFrequency = frequency;
                femaleTotal += bean.getPassengerCount();
                femaleUniqueTotal += bean.getUniqPassengerCount();
            }
        }
        for (int i = 0, size = model.ageMap.size(); i < size; i++) {
            Item item = model.ageMap.valueAt(i);
            item.max = max;
        }
        model.maleAvg = maleUniqueTotal == 0 ? 0f : (float) maleTotal / maleUniqueTotal;
        model.femaleAvg = femaleUniqueTotal == 0 ? 0f : (float) femaleTotal / femaleUniqueTotal;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();
        ListView lv = holder.getView(R.id.lv_dashboard_list);
        mAdapter = new AgeListAdapter(context);
        lv.setDividerHeight(0);
        lv.setAdapter(mAdapter);
        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Setup total avg value
        TextView maleTotal = holder.getView(R.id.tv_dashboard_male_avg);
        TextView femaleTotal = holder.getView(R.id.tv_dashboard_female_avg);
        maleTotal.setText(Utils.createFrequencyText(holder.getContext(), model.period, model.maleAvg, true));
        femaleTotal.setText(Utils.createFrequencyText(holder.getContext(), model.period, model.femaleAvg, true));

        int size = model.ageMap.size();
        List<Item> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(model.ageMap.valueAt(i));
        }
        mAdapter.setDatas(list);
        mAdapter.notifyDataSetChanged();
        holder.itemView.post(() -> {
            mAdapter.notifyDataSetChanged();
            holder.getView(R.id.lv_dashboard_list).requestLayout();
        });
    }

    private static class AgeListAdapter extends CommonAdapter<Item> {

        private Drawable barMaleDrawable;
        private Drawable barMaleHighlightDrawable;
        private Drawable barFemaleDrawable;
        private Drawable barFemaleHighlightDrawable;

        private AgeListAdapter(Context context) {
            super(context, R.layout.dashboard_item_customer_frequency_avg_item);
            barMaleDrawable = ContextCompat.getDrawable(context, R.drawable.dashboard_bar_bg_avg_frequency_male);
            barMaleHighlightDrawable = ContextCompat.getDrawable(context, R.drawable.dashboard_bar_bg_avg_frequency_male_highlight);
            barFemaleDrawable = ContextCompat.getDrawable(context, R.drawable.dashboard_bar_bg_avg_frequency_female);
            barFemaleHighlightDrawable = ContextCompat.getDrawable(context, R.drawable.dashboard_bar_bg_avg_frequency_female_highlight);
        }

        @Override
        public void convert(ViewHolder holder, Item item) {
            TextView tvName = holder.getView(R.id.tvName);
            TextView tvMale = holder.getView(R.id.tvMaleValue);
            ProgressBar barMale = holder.getView(R.id.barMale);
            TextView tvFemale = holder.getView(R.id.tvFemaleValue);
            ProgressBar barFemale = holder.getView(R.id.barFemale);

            tvName.setText(item.name);
            tvMale.setText(String.format(Locale.getDefault(), FORMAT_FLOAT_SINGLE_DECIMAL, item.maleFrequency));
            tvFemale.setText(String.format(Locale.getDefault(), FORMAT_FLOAT_SINGLE_DECIMAL, item.femaleFrequency));

            int max = (int) (item.max * 100);
            barMale.setMax(max);
            barMale.setProgressDrawable(item.isMaleHighlight ? barMaleHighlightDrawable : barMaleDrawable);
            barMale.setProgress((int) (item.maleFrequency * 100));

            barFemale.setMax(max);
            barFemale.setProgressDrawable(item.isFemaleHighlight ? barFemaleHighlightDrawable : barFemaleDrawable);
            barFemale.setProgress((int) (item.femaleFrequency * 100));
        }

    }

    public static class Item {
        private String name;
        private float max;
        private float maleFrequency;
        private float femaleFrequency;
        private boolean isMaleHighlight;
        private boolean isFemaleHighlight;

        private Item(String name) {
            this.name = name;
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private float maleAvg;
        private float femaleAvg;
        private SparseArray<Item> ageMap;
    }
}
