package com.sunmi.ipc.presenter;

import android.util.Pair;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.MotionVideoListContract;
import com.sunmi.ipc.model.MotionVideo;
import com.sunmi.ipc.model.MotionVideoListResp;
import com.sunmi.ipc.model.MotionVideoTimeSlotsResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public class MotionVideoListPresenter extends BasePresenter<MotionVideoListContract.View>
        implements MotionVideoListContract.Presenter {

    private static final long SECONDS_PER_DAY = 3600 * 24;
    private static final int PAGE_SIZE = 20;

    private int mDeviceId;
    private int mSource = IpcConstants.MOTION_DETECTION_SOURCE_ALL;

    private Calendar mCurrent;
    private int mPage = 1;
    private int mLoadedCount = 0;
    private boolean mHasMore = true;

    private Calendar temp = Calendar.getInstance();

    public MotionVideoListPresenter(int deviceId) {
        this.mDeviceId = deviceId;
        initTime();
    }

    private void initTime() {
        mCurrent = Calendar.getInstance();
        mCurrent.set(Calendar.HOUR_OF_DAY, 0);
        mCurrent.set(Calendar.MINUTE, 0);
        mCurrent.set(Calendar.SECOND, 0);
        mCurrent.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public Calendar getCurrent() {
        return mCurrent;
    }

    @Override
    public void loadTimeSlots(boolean openCalendar) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.add(Calendar.DATE, 1);
        long timeEnd = today.getTimeInMillis() / 1000 + SECONDS_PER_DAY;
        today.add(Calendar.MONTH, -3);
        long timeStart = today.getTimeInMillis() / 1000;

        IpcCloudApi.getInstance().getMotionTimeSlots(SpUtils.getCompanyId(), SpUtils.getShopId(),
                mDeviceId, timeStart, timeEnd, new RetrofitCallback<MotionVideoTimeSlotsResp>() {
                    @Override
                    public void onSuccess(int code, String msg, MotionVideoTimeSlotsResp data) {
                        List<Calendar> result = new ArrayList<>();
                        if (data == null || data.getTimeSlots() == null
                                || data.getTimeSlots().isEmpty()) {
                            if (isViewAttached()) {
                                mView.updateCalendar(result, openCalendar);
                            }
                            return;
                        }
                        List<Long> slots = data.getTimeSlots();
                        Calendar c = Calendar.getInstance();
                        for (Long slot : slots) {
                            c.setTimeInMillis(slot * 1000);
                            result.add((Calendar) c.clone());
                        }
                        if (isViewAttached()) {
                            mView.updateCalendar(result, openCalendar);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, MotionVideoTimeSlotsResp data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.toast_network_error);
                        }
                    }
                });
    }

    @Override
    public void load() {
        reset();
        Pair<Long, Long> timeRange = getTimeRange(mCurrent);
        load(mDeviceId, mSource, timeRange.first, timeRange.second, mPage, false);
    }

    @Override
    public void load(int source) {
        reset();
        this.mSource = source;
        Pair<Long, Long> timeRange = getTimeRange(mCurrent);
        load(mDeviceId, mSource, timeRange.first, timeRange.second, mPage, false);
    }

    @Override
    public void load(Calendar date) {
        reset();
        this.mCurrent = date;
        Pair<Long, Long> timeRange = getTimeRange(mCurrent);
        load(mDeviceId, mSource, timeRange.first, timeRange.second, mPage, false);
    }

    @Override
    public boolean loadMore() {
        if (!mHasMore) {
            return false;
        } else {
            Pair<Long, Long> timeRange = getTimeRange(mCurrent);
            load(mDeviceId, mSource, timeRange.first, timeRange.second, mPage, true);
            return true;
        }
    }

    private void reset() {
        mPage = 1;
        mLoadedCount = 0;
        mHasMore = true;
    }

    private Pair<Long, Long> getTimeRange(Calendar c) {
        long timeStart = mCurrent.getTimeInMillis() / 1000;
        long timeEnd = timeStart + SECONDS_PER_DAY;
        return new Pair<>(timeStart, timeEnd);
    }

    private void load(int deviceId, int source, long timeStart, long timeEnd, int pageNum, boolean isLoadMore) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        IpcCloudApi.getInstance().getMotionVideoList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                mDeviceId, source, timeStart, timeEnd, mPage, PAGE_SIZE, new RetrofitCallback<MotionVideoListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, MotionVideoListResp data) {
                        if (!isViewAttached()) {
                            return;
                        }
                        mView.hideLoadingDialog();
                        if (data == null || data.getMotionList() == null || data.getMotionList().isEmpty()) {
                            if (mLoadedCount == 0) {
                                mView.showEmpty();
                            } else {
                                ArrayList<MotionVideo> list = new ArrayList<>();
                                mHasMore = false;
                                if (isLoadMore) {
                                    mView.addData(list);
                                } else {
                                    mView.setData(list);
                                }
                            }
                            return;
                        }
                        List<MotionVideo> list = data.getMotionList();
                        mLoadedCount += list.size();
                        mHasMore = mLoadedCount < data.getCount();
                        if (isLoadMore) {
                            mView.addData(list);
                        } else {
                            mView.setData(list);
                        }
                        mPage++;
                    }

                    @Override
                    public void onFail(int code, String msg, MotionVideoListResp data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            if (mLoadedCount == 0) {
                                mView.showError(code);
                            } else {
                                mView.shortTip(R.string.toast_network_error);
                            }
                        }
                    }
                });
    }

}
