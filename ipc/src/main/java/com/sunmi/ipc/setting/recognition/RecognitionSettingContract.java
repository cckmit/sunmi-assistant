package com.sunmi.ipc.setting.recognition;

import android.content.Context;

import sunmi.common.base.BaseView;

/**
 * @author jacob
 */
public interface RecognitionSettingContract {

    int STEP_1_POSITION = 1;
    int STEP_2_RECOGNITION_ZOOM = 2;
    int STEP_3_FOCUS = 3;
    int STEP_4_LINE = 4;

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
        void stepTo(int step, boolean showTip);
    }

    interface Presenter {

        /**
         * 初始化设置步骤
         */
        void init();

        /**
         * 下一步
         */
        void next();
    }
}
