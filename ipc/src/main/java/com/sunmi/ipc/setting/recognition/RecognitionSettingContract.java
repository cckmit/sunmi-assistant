package com.sunmi.ipc.setting.recognition;

import android.content.Context;

import com.sunmi.ipc.view.IpcVideoView;

import sunmi.common.base.BaseView;
import sunmi.common.model.SunmiDevice;

/**
 * @author jacob
 */
public interface RecognitionSettingContract {

    int STEP_1_POSITION = 0;
    int STEP_2_RECOGNITION_ZOOM = 1;
    int STEP_3_FOCUS = 2;
    int STEP_4_LINE = 3;

    interface View extends BaseView {

        /**
         * 获取Context
         *
         * @return 上下文
         */
        Context getContext();

        /**
         * 根据步骤切换视图
         *
         * @param step    步骤Index
         */
        void updateViewsStepTo(int step);

        /**
         * 设置按钮是否可用
         *
         * @param isPlus True：+按钮，False：-按钮
         * @param enable 是否可用
         */
        void updateControlBtnEnable(boolean isPlus, boolean enable);

        /**
         * 完成设置
         */
        void complete();

        /**
         * 显示网络错误对话框
         */
        void showErrorDialog();

        /**
         * 取消网络错误对话框
         */
        void dismissErrorDialog();
    }

    interface Presenter {

        /**
         * 获取IPC设备结果处理回调
         *
         * @return 回调
         */
        IpcVideoView.ResultCallback getCallback();

        /**
         * 初始化设置步骤
         *
         * @param device 设备信息
         */
        void init(SunmiDevice device);

        /**
         * 获取IPC状态
         */
        void updateState();

        /**
         * 设置人脸位置
         *
         * @param x 人脸坐标x
         * @param y 人脸坐标y
         */
        void face(int x, int y);

        /**
         * 放大画面大小（变焦）
         * @param isZoomIn 是否放大
         */
        void zoom(boolean isZoomIn);

        /**
         * 重置画面大小（变焦）
         */
        void zoomReset();

        /**
         * 调节画面对焦
         *
         * @param isPlus 是否正向调节
         */
        void focus(boolean isPlus);

        /**
         * 重置对焦
         */
        void focusReset();

        /**
         * 设置进店划线
         *
         * @param start 划线起始点
         * @param end   划线终点
         */
        void line(int[] start, int[] end);
    }

}
