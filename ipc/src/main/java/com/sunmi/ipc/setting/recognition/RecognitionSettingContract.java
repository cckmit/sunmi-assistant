package com.sunmi.ipc.setting.recognition;

import android.content.Context;

import com.sunmi.ipc.view.IpcVideoView;

import sunmi.common.base.BaseView;

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
         * @param showTip 是否显示Tips
         */
        void updateViewStepTo(int step, boolean showTip);
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
         */
        void init();

        /**
         * 设置人脸位置
         *
         * @param coordinate 人脸坐标
         */
        void face(int[] coordinate);

        /**
         * 放大画面大小（变焦）
         */
        void zoomIn();

        /**
         * 缩小画面大小（变焦）
         */
        void zoomOut();

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
