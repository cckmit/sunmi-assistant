package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.MotionVideo;

import java.util.Calendar;
import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public interface MotionVideoListContract {

    interface View extends BaseView {

        void updateCalendar(List<Calendar> selected, boolean open);

        void setData(List<MotionVideo> data);

        void addData(List<MotionVideo> data);

        void showError(int code);

        void showEmpty();
    }

    interface Presenter {

        void loadTimeSlots(boolean openCalendar);

        void load();

        void load(int source);

        void load(Calendar date);

        boolean loadMore();
    }

}
