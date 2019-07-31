package com.sunmi.ipc.setting.recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.view.DoorLineView;
import com.sunmi.ipc.view.IpcVideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.CommonDialog;

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

    @ViewById(resName = "iv_setting_face_case")
    ImageView mFaceCase;
    private Rect mFacePos = new Rect();

    @ViewById(resName = "v_line_draw")
    DoorLineView mLineView;

    private CommonDialog mNetworkDialog;

    @Extra
    SunmiDevice mDevice;

    private int mStepIndex;
    private boolean mIsTipShow;

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPresenter = new RecognitionSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.init(mDevice);
        video.init(mDevice.getUid(), 16, 9, mPresenter.getCallback());
        updateViewStepTo(RecognitionSettingContract.STEP_1_POSITION, true);
        initFaceCase();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFaceCase() {
        mFaceCase.setOnTouchListener(new FaceCaseTouch());
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
    @UiThread
    public void updateViewStepTo(int step, boolean showTip) {
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
        updateViewStepTo(mStepIndex, false);
    }

    @Click(resName = "iv_setting_back")
    void onBack() {
        if (mIsTipShow) {
            return;
        }
        if (mStepIndex == RecognitionSettingContract.STEP_1_POSITION) {
            finish();
        } else {
            updateViewStepTo(--mStepIndex, true);
        }
    }

    @Click(resName = "tv_setting_next")
    void onNext() {
        if (mIsTipShow) {
            return;
        }
        if (mStepIndex == RecognitionSettingContract.STEP_1_POSITION) {
            showLoadingDialog();
            mPresenter.updateState();
        } else if (mStepIndex == RecognitionSettingContract.STEP_4_LINE) {
            showLoadingDialog();
//            mPresenter.line();
        } else {
            updateViewStepTo(++mStepIndex, true);
        }
    }

    @Click(resName = "btn_setting_btn_plus")
    void onPlusClick() {
        showLoadingDialog(getString(R.string.ipc_recognition_loading));
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            mPresenter.zoomIn();
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            mPresenter.focus(true);
        } else {
            hideLoadingDialog();
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_minus")
    void onMinusClick() {
        showLoadingDialog(getString(R.string.ipc_recognition_loading));
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            mPresenter.zoomOut();
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            mPresenter.focus(false);
        } else {
            hideLoadingDialog();
            LogCat.e(TAG, "Step of recognition ERROR when minus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_reset")
    void onResetClick() {
        showLoadingDialog(getString(R.string.ipc_recognition_loading));
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            mPresenter.zoomReset();
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            mPresenter.focusReset();
        } else {
            hideLoadingDialog();
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    private void updateViewPosition(boolean isTipShow) {
        mTvNext.setText(getString(R.string.str_next));
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        showTitle(!isTipShow, getString(R.string.ipc_recognition_tip_position));
        showControlBtn(false, false);
        showTip(isTipShow, getString(R.string.ipc_recognition_tip_position));
    }

    private void updateViewZoom(boolean isTipShow) {
        mTvNext.setText(getString(R.string.str_next));
        mFaceCase.setVisibility(isTipShow ? View.INVISIBLE : View.VISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        showTitle(false, null);
        showControlBtn(!isTipShow, true);
        showTip(isTipShow, getString(R.string.ipc_recognition_tip_zoom));
    }

    private void updateViewFocus(boolean isTipShow) {
        mTvNext.setText(getString(R.string.str_next));
        mFaceCase.setVisibility(isTipShow ? View.INVISIBLE : View.VISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        showTitle(false, null);
        showControlBtn(!isTipShow, false);
        showTip(isTipShow, getString(R.string.ipc_recognition_tip_focus));
    }

    private void updateViewLine(boolean isTipShow) {
        mTvNext.setText(getString(R.string.str_complete));
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(isTipShow ? View.INVISIBLE : View.VISIBLE);
        showTitle(!isTipShow, getString(R.string.ipc_recognition_line_start));
        showControlBtn(false, false);
        showTip(isTipShow, getString(R.string.ipc_recognition_tip_line));
    }

    private void showTitle(boolean enable, String title) {
        if (enable) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    @UiThread
    public void enableControlBtn(boolean isPlus, boolean enable) {
        if (isPlus) {
            mBtnPlus.setEnabled(enable);
        } else {
            mBtnMinus.setEnabled(enable);
        }
    }

    private void showControlBtn(boolean enable, boolean isZoom) {
        if (enable) {
            mBtnPlus.setVisibility(View.VISIBLE);
            mBtnMinus.setVisibility(View.VISIBLE);
            mBtnReset.setVisibility(View.VISIBLE);
            if (isZoom) {
                mBtnPlus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_zoom_in));
                mBtnMinus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_zoom_out));
            } else {
                mBtnPlus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_focus_plus));
                mBtnMinus.setBackground(ContextCompat.getDrawable(this, R.mipmap.setting_recognition_focus_minus));
            }
        } else {
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
        }
    }

    private void showTip(boolean enable, String content) {
        mIsTipShow = enable;
        if (enable) {
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
            mTvTipContent.setText(content);
        } else {
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    @UiThread
    public void showErrorDialog() {
        if (mNetworkDialog == null) {
            mNetworkDialog = new CommonDialog.Builder(this)
                    .setTitle(R.string.ipc_setting_tip)
                    .setMessage(R.string.ipc_recognition_network_error)
                    .setConfirmButton(R.string.str_confirm)
                    .create();
        }
        mNetworkDialog.show();
    }

    @Override
    @UiThread
    public void dismissErrorDialog() {
        if (mNetworkDialog != null) {
            mNetworkDialog.dismiss();
        }
    }

    private class FaceCaseTouch implements View.OnTouchListener {

        private static final int TOUCH_DELAY = 500;

        private Rect boundary = new Rect();
        private Rect current = new Rect();
        private int width;
        private int height;
        private float x;
        private float y;
        private long downTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mStepIndex != RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    boundary.set(video.getLeft(), video.getTop(), video.getRight(), video.getBottom());
                    width = v.getMeasuredWidth();
                    height = v.getMeasuredHeight();
                    x = event.getX();
                    y = event.getY();
                    downTime = System.currentTimeMillis();
                    LogCat.d(TAG, "Boundary: " + boundary + "; Down: x=" + x + "; y=" + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - x;
                    float dy = event.getY() - y;
                    int l = (int) (v.getX() + dx);
                    int r = l + width;
                    int t = (int) (v.getY() + dy);
                    int b = t + height;
                    if (l < boundary.left) {
                        l = boundary.left;
                        r = boundary.left + width;
                    }
                    if (r > boundary.right) {
                        r = boundary.right;
                        l = boundary.right - width;
                    }
                    if (t < boundary.top) {
                        t = boundary.top;
                        b = boundary.top + height;
                    }
                    if (b > boundary.bottom) {
                        b = boundary.bottom;
                        t = boundary.bottom - height;
                    }
                    current.set(l, t, r, b);
                    LogCat.d(TAG, String.format(Locale.getDefault(), "Move: ┌ %3d ┐", t));
                    LogCat.d(TAG, String.format(Locale.getDefault(), "    %3d   %3d", l, r));
                    LogCat.d(TAG, String.format(Locale.getDefault(), "      └ %3d ┘", b));
                    v.setX(l);
                    v.setY(t);
                    break;
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - downTime > TOUCH_DELAY) {
                        int x = (current.left + current.right) >> 1;
                        int y = (current.top + current.bottom) >> 1;
                        int xRelative = (x - video.getLeft()) * 100 / video.getWidth();
                        int yRelative = (x - video.getTop()) * 100 / video.getHeight();
                        mPresenter.face(xRelative, yRelative);
                        showLoadingDialog(getString(R.string.ipc_recognition_loading));
                    }
                default:
                    break;
            }
            return true;
        }
    }

}
