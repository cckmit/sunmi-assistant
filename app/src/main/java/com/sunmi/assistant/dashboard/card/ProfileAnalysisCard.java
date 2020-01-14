package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.text.format.DateFormat;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerHistoryDetailResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class ProfileAnalysisCard extends BaseRefreshCard<ProfileAnalysisCard.Model, CustomerHistoryDetailResp> {

    private static final int NUM_10_THOUSANDS = 10000;
    private static final int MAX_ITEM_COUNT = 3;

    private static ProfileAnalysisCard sInstance;

    private String mAgeLabel;
    private String mMaleLabel;
    private String mFemaleLabel;

    private SparseArray<String> mAgeList;
    private CommonAdapter<Item> mAdapter;

    private ProfileAnalysisCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static ProfileAnalysisCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new ProfileAnalysisCard(presenter, source);
        } else {
            sInstance.mPresenter = presenter;
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
        mAgeLabel = context.getString(R.string.dashboard_card_age_label);
        mMaleLabel = context.getString(R.string.str_gender_male);
        mFemaleLabel = context.getString(R.string.str_gender_female);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_profile_analysis;
    }

    @Override
    protected Call<BaseResponse<CustomerHistoryDetailResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> time = Utils.getPeriodTimestamp(period);
        String start = DateFormat.format(DATE_FORMAT, time.first).toString();
        String end = DateFormat.format(DATE_FORMAT, time.second - 1).toString();
        if (mAgeList == null) {
            loadAgeList(companyId, shopId, start, end, callback);
        } else {
            loadDetail(companyId, shopId, start, end, callback);
        }
        return null;
    }

    private void loadAgeList(int companyId, int shopId, String start, String end, CardCallback callback) {
        IpcCloudApi.getInstance().getFaceAgeRange(companyId, shopId, new RetrofitCallback<FaceAgeRangeResp>() {
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
                    mAgeList.put(age.getCode(), age.getName());
                }
                loadDetail(companyId, shopId, start, end, callback);
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                callback.onFail(code, msg, null);
            }
        });
    }

    private void loadDetail(int companyId, int shopId, String start, String end, CardCallback callback) {
        SunmiStoreApi.getInstance().getHistoryCustomerDetail(companyId, shopId, start, end,
                new RetrofitCallback<CustomerHistoryDetailResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerHistoryDetailResp data) {
                        callback.onSuccess(code, msg, data);
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerHistoryDetailResp data) {
                        if (code == Constants.NO_CUSTOMER_DATA) {
                            callback.onSuccess(code, msg, data);
                        } else {
                            callback.onFail(code, msg, data);
                        }
                    }
                });
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();
        ListView lv = holder.getView(R.id.lv_dashboard_list);
        mAdapter = new DetailListAdapter(context);
        lv.setDividerHeight(0);
        lv.setAdapter(mAdapter);
        return holder;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model models, CustomerHistoryDetailResp response) {
        Model model = getModel();
        model.list.clear();
        if (response != null && response.getList() != null && !response.getList().isEmpty()) {
            List<CustomerHistoryDetailResp.Item> list = response.getList();
            List<Item> result = new ArrayList<>();
            int total = 0;
            for (CustomerHistoryDetailResp.Item item : list) {
                if (item.getMaleCount() == 0 && item.getFemaleCount() == 0) {
                    continue;
                }
                String ageName = mAgeList.get(item.getAgeRangeCode());
                String maleName = String.format("%s  |  %s%s", mMaleLabel, ageName, mAgeLabel);
                String femaleName = String.format("%s  |  %s%s", mFemaleLabel, ageName, mAgeLabel);
                result.add(new Item(model.period, item.getAgeRangeCode(), 1, maleName,
                        item.getMaleCount(), item.getMaleRegularCount(), item.getMaleUniqCount()));
                result.add(new Item(model.period, item.getAgeRangeCode(), 2, femaleName,
                        item.getFemaleCount(), item.getFemaleRegularCount(), item.getFemaleUniqCount()));
                total = total + item.getFemaleCount() + item.getMaleCount();
            }
            Collections.sort(result, (o1, o2) -> o2.count - o1.count);
            if (result.size() > MAX_ITEM_COUNT) {
                model.list.addAll(result.subList(0, 3));
            } else {
                model.list.addAll(result);
            }
            for (Item item : model.list) {
                item.setTotal(total);
            }
        }

        if (model.list.isEmpty()) {
            Item e = new Item();
            e.setError();
            model.list.add(e);
        }

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Test data
        // model.random(mAgeList, mAgeLabel, mMaleLabel, mFemaleLabel);

        Context context = holder.getContext();
        View view = holder.itemView;
        if (hasFloating()) {
            int paddingBottom = (int) context.getResources().getDimension(R.dimen.dp_80);
            view.setPaddingRelative(0, 0, 0, paddingBottom);
        } else {
            int paddingBottom = (int) context.getResources().getDimension(R.dimen.dp_32);
            view.setPaddingRelative(0, 0, 0, paddingBottom);
        }
        mAdapter.setDatas(model.list);
        mAdapter.notifyDataSetChanged();
        view.post(() -> {
            mAdapter.notifyDataSetChanged();
            holder.getView(R.id.lv_dashboard_list).requestLayout();
        });
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        if (model.list.isEmpty()) {
            model.list.add(new Item());
        }
        for (Item item : model.list) {
            item.setLoading();
        }
        setupView(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        if (model.list.isEmpty()) {
            model.list.add(new Item());
        }
        for (Item item : model.list) {
            item.setError();
        }
        setupView(holder, model, position);
    }

    private static class DetailListAdapter extends CommonAdapter<Item> {

        private String mNum10Thousands;
        private String mFrequencyDay;
        private String mFrequencyWeek;
        private String mFrequencyMonth;

        private DetailListAdapter(Context context) {
            super(context, R.layout.dashboard_item_profile_analysis_item);
            mNum10Thousands = context.getString(R.string.str_num_10_thousands);
            mFrequencyDay = context.getString(R.string.dashboard_card_customer_frequency_data_day);
            mFrequencyWeek = context.getString(R.string.dashboard_card_customer_frequency_data_week);
            mFrequencyMonth = context.getString(R.string.dashboard_card_customer_frequency_data_month);
        }

        @Override
        public void convert(ViewHolder holder, Item item) {
            if (item.state == Item.STATE_LOADING) {
                showLoading(holder);
                return;
            }
            showContent(holder);
            ImageView avatar = holder.getView(R.id.iv_dashboard_avatar);
            TextView title = holder.getView(R.id.tv_dashboard_title);
            TextView count = holder.getView(R.id.tv_dashboard_count);
            TextView ratio = holder.getView(R.id.tv_dashboard_ratio);
            TextView oldRatio = holder.getView(R.id.tv_dashboard_old_ratio);
            TextView frequency = holder.getView(R.id.tv_dashboard_frequency);
            if (item.state == Item.STATE_ERROR) {
                avatar.setImageResource(R.mipmap.dashboard_customer_avatar_error);
                title.setText(R.string.dashboard_card_customer_none);
                count.setText(DATA_ZERO);
                ratio.setText(DATA_ZERO_RATIO);
                oldRatio.setText(DATA_NONE);
                frequency.setText(DATA_NONE);
            } else {
                avatar.setImageResource(item.gender == 1 ?
                        R.mipmap.dashboard_customer_avatar_male : R.mipmap.dashboard_customer_avatar_female);
                title.setText(item.name);
                count.setText(item.count > NUM_10_THOUSANDS ?
                        FORMAT_THOUSANDS_DOUBLE_DECIMAL.format((float) item.count / NUM_10_THOUSANDS) + mNum10Thousands
                        : String.valueOf(item.count));
                ratio.setText(String.format(Locale.getDefault(), "%.0f%%",
                        (float) item.count * 100 / item.total));
                oldRatio.setText(String.format(Locale.getDefault(), "%.0f%%",
                        (float) item.oldCount * 100 / item.count));
                if (item.period == Constants.TIME_PERIOD_MONTH) {
                    frequency.setText(String.format(Locale.getDefault(), mFrequencyMonth, item.uniqueCount > 0 ?
                            (float) item.count * 100 / item.uniqueCount : 0f));
                } else if (item.period == Constants.TIME_PERIOD_WEEK) {
                    frequency.setText(String.format(Locale.getDefault(), mFrequencyWeek, item.uniqueCount > 0 ?
                            (float) item.count * 100 / item.uniqueCount : 0f));
                } else {
                    frequency.setText(String.format(Locale.getDefault(), mFrequencyDay, item.uniqueCount > 0 ?
                            (float) item.count * 100 / item.uniqueCount : 0f));
                }
            }
        }

        private void showContent(ViewHolder holder) {
            Group content = holder.getView(R.id.group_dashboard_content);
            Group skeleton = holder.getView(R.id.group_dashboard_skeleton);
            content.setVisibility(View.VISIBLE);
            skeleton.setVisibility(View.INVISIBLE);
        }

        private void showLoading(ViewHolder holder) {
            ImageView avatar = holder.getView(R.id.iv_dashboard_avatar);
            Group content = holder.getView(R.id.group_dashboard_content);
            Group skeleton = holder.getView(R.id.group_dashboard_skeleton);
            avatar.setImageResource(R.drawable.dashboard_bg_skeleton_circle);
            content.setVisibility(View.INVISIBLE);
            skeleton.setVisibility(View.VISIBLE);
        }
    }

    public static class Item {

        private static final int STATE_NORMAL = 0;
        private static final int STATE_LOADING = 1;
        private static final int STATE_ERROR = 2;

        private int period;
        private int age;
        private int gender;
        private String name;
        private int count;
        private int oldCount;
        private int uniqueCount;

        private int total;
        private int state;

        public Item() {
        }

        public Item(int period, int age, int gender, String name, int count, int oldCount, int uniqueCount) {
            this.period = period;
            this.age = age;
            this.gender = gender;
            this.name = name;
            this.count = count;
            this.oldCount = oldCount;
            this.uniqueCount = uniqueCount;
            this.state = STATE_NORMAL;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void setLoading() {
            this.state = STATE_LOADING;
        }

        public void setError() {
            this.state = STATE_ERROR;
        }

        @NotNull
        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private List<Item> list = new ArrayList<>(3);

        @Override
        public void init(int source) {
            list.clear();
        }

        public void random(SparseArray<String> ageList, String ageLabel, String maleLabel, String femaleLabel) {
            Random random = new Random();
            List<Pair<Integer, Integer>> pool = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 2; j++) {
                    pool.add(new Pair<>(i + 1, j + 1));
                }
            }
            list.clear();
            int itemCount = random.nextInt(5) + 1;
            int total = 0;
            for (int i = 0; i < itemCount; i++) {
                int size = pool.size();
                Pair<Integer, Integer> item = pool.remove(random.nextInt(10000) % size);
                String ageName = ageList.get(item.first);
                String genderName = item.second == 1 ? maleLabel : femaleLabel;
                String name = String.format("%s  |  %s%s", genderName, ageName, ageLabel);
                int count = random.nextInt(1000);
                total += count;
                list.add(new Item(Constants.TIME_PERIOD_WEEK, item.first, item.second, name, count, count * 3 / 4, count * 2 / 3));
            }
            Collections.sort(list, (o1, o2) -> o2.count - o1.count);
            if (list.size() > 3) {
                list.subList(3, list.size()).clear();
            }
            for (Item item : list) {
                item.setTotal(total);
            }
            int type = random.nextInt(10000) % 3;
            for (Item item : list) {
                if (type == 1) {
                    item.setLoading();
                } else if (type == 2) {
                    item.setError();
                }
            }
        }
    }
}
