package com.sunmi.ipc.setting.recognition;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
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
    IpcVideoView mVideoView;
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

    @Extra
    SunmiDevice mDevice;
    @Extra
    float mVideoRatio;

    private int mStepIndex;
    private float[] mLineStart;
    private float[] mLineEnd;

    private SparseArray<String> mResTitle = new SparseArray<>(4);
    private SparseArray<String> mResNext = new SparseArray<>(4);
    private SparseArray<String> mResTip = new SparseArray<>(4);
    private SparseArray<String> mResLineTitle = new SparseArray<>(3);
    private Drawable mResZoomIn;
    private Drawable mResZoomOut;
    private Drawable mResFocusPlus;
    private Drawable mResFocusMinus;
    private String mResLoading;

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPresenter = new RecognitionSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.init(mDevice);
        initViews();
        updateViewsStepTo(RecognitionSettingContract.STEP_1_POSITION);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        if (mVideoRatio <= 0) {
            mVideoRatio = 16f / 9f;
        }
        mVideoView.init(mDevice.getUid(), mVideoRatio, mPresenter.getCallback());
        mFaceCase.setOnTouchListener(new FaceCaseTouch());
        mLineView.setStateChangeListener(new DoorLineStateChangeListener());
        mResTitle.put(RecognitionSettingContract.STEP_1_POSITION, getString(R.string.ipc_recognition_tip_position));
        mResTitle.put(RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM, "");
        mResTitle.put(RecognitionSettingContract.STEP_3_FOCUS, "");
        mResTitle.put(RecognitionSettingContract.STEP_4_LINE, getString(R.string.ipc_recognition_line_start));
        String next = getString(R.string.str_next);
        mResNext.put(RecognitionSettingContract.STEP_1_POSITION, next);
        mResNext.put(RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM, next);
        mResNext.put(RecognitionSettingContract.STEP_3_FOCUS, next);
        mResNext.put(RecognitionSettingContract.STEP_4_LINE, getString(R.string.str_complete));
        mResTip.put(RecognitionSettingContract.STEP_1_POSITION, getString(R.string.ipc_recognition_tip_position));
        mResTip.put(RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM, getString(R.string.ipc_recognition_tip_zoom));
        mResTip.put(RecognitionSettingContract.STEP_3_FOCUS, getString(R.string.ipc_recognition_tip_focus));
        mResTip.put(RecognitionSettingContract.STEP_4_LINE, getString(R.string.ipc_recognition_tip_line));
        mResLineTitle.put(DoorLineView.STATE_INIT, getString(R.string.ipc_recognition_line_start));
        mResLineTitle.put(DoorLineView.STATE_START, getString(R.string.ipc_recognition_line_end));
        mResLineTitle.put(DoorLineView.STATE_END, getString(R.string.ipc_recognition_line_end));
        mResZoomIn = ContextCompat.getDrawable(this, R.drawable.setting_recognition_zoom_in);
        mResZoomOut = ContextCompat.getDrawable(this, R.drawable.setting_recognition_zoom_out);
        mResFocusPlus = ContextCompat.getDrawable(this, R.drawable.setting_recognition_focus_plus);
        mResFocusMinus = ContextCompat.getDrawable(this, R.drawable.setting_recognition_focus_minus);
        mResLoading = getString(R.string.ipc_recognition_loading);
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    @Override
    @UiThread
    public void updateViewsStepTo(int step) {
        mStepIndex = step;
        updateTitle(mResTitle.get(step), mResNext.get(step));
        updateTip(mResTip.get(step));
        updateTipShow(true);
        updateControlBtnShow(false);
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        switch (step) {
            case RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM:
                updateControlBtn(mResZoomIn, mResZoomOut);
                mPresenter.updateControlBtnEnable(true);
                break;
            case RecognitionSettingContract.STEP_3_FOCUS:
                updateControlBtn(mResFocusPlus, mResFocusMinus);
                mPresenter.updateControlBtnEnable(false);
                break;
            default:
        }
    }

    @Click(resName = "btn_setting_tip_ok")
    void onTipOkClick() {
        dismissTip(mStepIndex);
    }

    @Click(resName = "iv_setting_back")
    void onBack() {
        if (mStepIndex == RecognitionSettingContract.STEP_1_POSITION) {
            finish();
        } else {
            updateViewsStepTo(--mStepIndex);
        }
    }

    @Click(resName = "tv_setting_next")
    void onNext() {
        if (mStepIndex == RecognitionSettingContract.STEP_1_POSITION) {
            showLoadingDialog();
            mPresenter.updateState();
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            showLoadingDialog();
            mPresenter.checkSdState();
        } else if (mStepIndex == RecognitionSettingContract.STEP_4_LINE) {
            showLoadingDialog();
            int[] start = new int[2];
            int[] end = new int[2];
            start[0] = (int) (mLineStart[0] * 1920 / mVideoView.getWidth());
            start[1] = (int) (mLineStart[1] * 1080 / mVideoView.getHeight());
            end[0] = (int) (mLineEnd[0] * 1920 / mVideoView.getWidth());
            end[1] = (int) (mLineEnd[1] * 1080 / mVideoView.getHeight());
            mPresenter.line(start, end);
        } else {
            updateViewsStepTo(++mStepIndex);
        }
    }

    @Click(resName = "btn_setting_btn_plus")
    void onPlusClick() {
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showLoadingDialog(mResLoading);
            mPresenter.zoom(true);
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            showLoadingDialog(mResLoading);
            mPresenter.focus(true);
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_minus")
    void onMinusClick() {
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showLoadingDialog(mResLoading);
            mPresenter.zoom(false);
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            showLoadingDialog(mResLoading);
            mPresenter.focus(false);
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when minus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_reset")
    void onResetClick() {
        if (mStepIndex == RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showLoadingDialog(mResLoading);
            mPresenter.zoomReset();
        } else if (mStepIndex == RecognitionSettingContract.STEP_3_FOCUS) {
            showLoadingDialog(mResLoading);
            mPresenter.focusReset();
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    private void dismissTip(int step) {
        updateTipShow(false);
        updateControlBtnShow(false);
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        switch (step) {
            case RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM:
            case RecognitionSettingContract.STEP_3_FOCUS:
                mFaceCase.setVisibility(View.VISIBLE);
                updateControlBtnShow(true);
                break;
            case RecognitionSettingContract.STEP_4_LINE:
                Rect boundary = new Rect(0, Math.max(0, mTvTitle.getBottom() - mVideoView.getTop()),
                        mVideoView.getWidth(), mVideoView.getHeight());
                mLineView.init(boundary);
                mLineView.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    private void updateTitle(String title, String nextText) {
        mTvTitle.setText(title);
        mTvNext.setText(nextText);
    }

    private void updateTip(String content) {
        mTvTipContent.setText(content);
    }

    private void updateControlBtn(Drawable plus, Drawable minus) {
        mBtnPlus.setBackground(plus);
        mBtnMinus.setBackground(minus);
    }

    private void updateTitleShow(boolean show) {
        mTvTitle.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateControlBtnShow(boolean show) {
        if (show) {
            mBtnPlus.setVisibility(View.VISIBLE);
            mBtnMinus.setVisibility(View.VISIBLE);
            mBtnReset.setVisibility(View.VISIBLE);
        } else {
            mBtnPlus.setVisibility(View.INVISIBLE);
            mBtnMinus.setVisibility(View.INVISIBLE);
            mBtnReset.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTipShow(boolean show) {
        updateBackEnable(!show);
        updateNextEnable(!show);
        updateTitleShow(!show);
        if (show) {
            mTipMask.setVisibility(View.VISIBLE);
            mBtnTipOk.setVisibility(View.VISIBLE);
            mTvTipContent.setVisibility(View.VISIBLE);
        } else {
            mTipMask.setVisibility(View.INVISIBLE);
            mBtnTipOk.setVisibility(View.INVISIBLE);
            mTvTipContent.setVisibility(View.INVISIBLE);
        }
    }

    private void updateBackEnable(boolean enable) {
        mIvBack.setEnabled(enable);
    }

    private void updateNextEnable(boolean enable) {
        mTvNext.setEnabled(enable);
    }

    @Override
    @UiThread
    public void updateControlBtnEnable(boolean isPlus, boolean enable) {
        if (isPlus) {
            mBtnPlus.setEnabled(enable);
        } else {
            mBtnMinus.setEnabled(enable);
        }
    }

    @Override
    public void complete() {
        shortTip(R.string.ipc_recognition_complete);
        finish();
    }

    @Override
    @UiThread
    public void showErrorDialog(@StringRes int content) {
        hideLoadingDialog();
        new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_tip)
                .setMessage(content)
                .setConfirmButton(R.string.str_confirm)
                .create().show();
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            hideLoadingDialog();
        } else {
            onBack();
        }
    }

    private class FaceCaseTouch implements View.OnTouchListener {

        private static final int TOUCH_DELAY = 50;

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
                    boundary.set(mVideoView.getLeft(), Math.max(mVideoView.getTop(), mTvTitle.getBottom()),
                            Math.min(mVideoView.getRight(), mBtnReset.getLeft()), mVideoView.getBottom());
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
                    v.setX(l);
                    v.setY(t);
                    break;
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - downTime > TOUCH_DELAY) {
                        int x = (current.left + current.right) >> 1;
                        int y = (current.top + current.bottom) >> 1;
                        int xRelative = (x - mVideoView.getLeft()) * 100 / mVideoView.getWidth();
                        int yRelative = (x - mVideoView.getTop()) * 100 / mVideoView.getHeight();
                        mPresenter.face(xRelative, yRelative);
                        showLoadingDialog(mResLoading);
                    }
                default:
                    break;
            }
            return true;
        }
    }

    private class DoorLineStateChangeListener implements DoorLineView.OnStateChangeListener {

        @Override
        public void onStateChanged(int state, float[] lineStart, float[] lineEnd) {
            updateTitle(mResLineTitle.get(state), mResNext.get(RecognitionSettingContract.STEP_4_LINE));
            switch (state) {
                case DoorLineView.STATE_INIT:
                case DoorLineView.STATE_START:
                    updateNextEnable(false);
                    break;
                case DoorLineView.STATE_END:
                    mLineStart = lineStart;
                    mLineEnd = lineEnd;
                    updateNextEnable(true);
                    break;
                default:
            }
        }
    }

}
