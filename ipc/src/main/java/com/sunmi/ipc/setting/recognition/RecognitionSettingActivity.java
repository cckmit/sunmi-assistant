package com.sunmi.ipc.setting.recognition;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.view.IpcVideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-07-25
 */
@EActivity(resName = "ipc_setting_recognition_activity")
public class RecognitionSettingActivity extends BaseMvpActivity<RecognitionSettingPresenter>
        implements RecognitionSettingContract.View {

    @ViewById(resName = "sv_setting_video")
    IpcVideoView video;
    @ViewById(resName = "iv_setting_back")
    ImageView mIvBack;
    @ViewById(resName = "tv_setting_title")
    TextView mTvTitle;
    @ViewById(resName = "tv_setting_next")
    TextView mTvNext;
    @ViewById(resName = "btn_setting_btn_plus")
    Button mBtnPlus;
    @ViewById(resName = "btn_setting_btn_minus")
    Button mBtnMinus;
    @ViewById(resName = "btn_setting_btn_reset")
    Button mBtnReset;
    @ViewById(resName = "v_setting_tip_mask")
    View mTipMask;
    @ViewById(resName = "tv_setting_tip_content")
    TextView mTvTipContent;
    @ViewById(resName = "btn_setting_tip_ok")
    Button mBtnTipOk;

    @Extra
    SunmiDevice mDevice;

    private int mStepIndex;

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        video.init(mDevice.getUid(), 16, 9);
        stepTo(RecognitionSettingContract.STEP_1_POSITION, true);
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void stepTo(int step, boolean showTip) {
        mStepIndex = step;
        switch (step) {
            case RecognitionSettingContract.STEP_1_POSITION:
                updateViewPosition(showTip);
                break;
            case RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM:
                updateViewZoom(showTip);
                break;
            case RecognitionSettingContract.STEP_3_FOCUS:
                updateViewFocus(showTip);
                break;
            case RecognitionSettingContract.STEP_4_LINE:
                updateViewLine(showTip);
                break;
            default:
                LogCat.e(TAG, "Unsupported step code:" + step);
        }
    }

    @Click(resName = "btn_setting_tip_ok")
    void onTipOkClick() {
        stepTo(mStepIndex, false);
    }

    @Click(resName = "iv_setting_back")
    void onBack() {

    }

    @Click(resName = "tv_setting_next")
    void onNext() {
        stepTo(++mStepIndex, true);
    }

    @Click(resName = "btn_setting_btn_plus")
    void onPlusClick() {

    }

    @Click(resName = "btn_setting_btn_minus")
    void onMinusClick() {

    }

    @Click(resName = "btn_setting_btn_reset")
    void onResetClick() {

    }

    private void updateViewPosition(boolean showTip) {
        mTvNext.setText(getString(R.string.str_next));
        if (showTip) {
            mTvTitle.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
            mTvTipContent.setText(getString(R.string.ipc_recognition_tip_position));
        } else {
            mTvTitle.setVisibility(View.VISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
            mTvTitle.setText(getString(R.string.ipc_recognition_tip_position));
        }
    }

    private void updateViewZoom(boolean showTip) {
        mTvNext.setText(getString(R.string.str_next));
        if (showTip) {
            mTvTitle.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
            mTvTipContent.setText(getString(R.string.ipc_recognition_tip_zoom));
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.VISIBLE);
            mBtnMinus.setVisibility(View.VISIBLE);
            mBtnReset.setVisibility(View.VISIBLE);
            mBtnPlus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_zoom_in));
            mBtnMinus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_zoom_out));
        }
    }

    private void updateViewFocus(boolean showTip) {
        mTvNext.setText(getString(R.string.str_next));
        if (showTip) {
            mTvTitle.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
            mTvTipContent.setText(getString(R.string.ipc_recognition_tip_focus));
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.VISIBLE);
            mBtnMinus.setVisibility(View.VISIBLE);
            mBtnReset.setVisibility(View.VISIBLE);
            mBtnPlus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_focus_plus));
            mBtnMinus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_focus_minus));
        }
    }

    private void updateViewLine(boolean showTip) {
        mTvNext.setText(getString(R.string.str_next));
        if (showTip) {
            mTvTitle.setVisibility(View.INVISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
            mTvTipContent.setText(getString(R.string.ipc_recognition_tip_line));
        } else {
            mTvTitle.setVisibility(View.VISIBLE);
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
            mTvTitle.setText(getString(R.string.ipc_recognition_line_start));
        }
    }

}
