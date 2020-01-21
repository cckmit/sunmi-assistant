package com.sunmi.ipc.view.activity.setting;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.ScreenAdjustSettingContract;
import com.sunmi.ipc.presenter.ScreenAdjustSettingPresenter;
import com.sunmi.ipc.service.P2pService;
import com.sunmi.ipc.view.DoorLineView;
import com.sunmi.ipc.view.IpcVideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
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
@EActivity(resName = "ipc_setting_screen_adjust_activity")
public class ScreenAdjustSettingActivity extends BaseMvpActivity<ScreenAdjustSettingPresenter>
        implements ScreenAdjustSettingContract.View, SurfaceHolder.Callback,
        P2pService.OnPlayStatusChangedListener {

    private static final int STANDARD_VIDEO_WIDTH = 1920;
    private static final int STANDARD_VIDEO_HEIGHT = 1080;
    private static final int FACE_CASE_SIZE = 140;
    private static final int LINE_POINT_GAP_LIMIT = 100;

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
    @ViewById(resName = "v_line_draw")
    DoorLineView mLineView;

    @Extra
    SunmiDevice mDevice;
    @Extra
    boolean isFromLive;
    @Extra
    float mVideoRatio;

    private int mStepIndex;

    private SparseArray<String> mResTitle = new SparseArray<>(4);
    private SparseArray<String> mResNext = new SparseArray<>(4);
    private SparseArray<String> mResTip = new SparseArray<>(4);
    private SparseArray<String> mResLineTitle = new SparseArray<>(3);
    private Drawable mResZoomIn;
    private Drawable mResZoomOut;
    private Drawable mResFocusPlus;
    private Drawable mResFocusMinus;
    private String mResLoading;

    private boolean isBind;
    private P2pService p2pService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            P2pService.MyBinder myBinder = (P2pService.MyBinder) binder;
            p2pService = myBinder.getService();
            p2pPrepare();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPresenter = new ScreenAdjustSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.init(mDevice);
        bindService(new Intent(context, P2pService.class)
                .putExtra("uid", mDevice.getUid()), conn, BIND_AUTO_CREATE);
        initViews();
        updateViewsStepTo(ScreenAdjustSettingContract.STEP_1_POSITION);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        if (mVideoRatio <= 0) {
            mVideoRatio = 16f / 9f;
        }
        mVideoView.init(mVideoRatio, this);
        mFaceCase.setOnTouchListener(new FaceCaseTouch());
        mLineView.setStateChangeListener(new DoorLineStateChangeListener());
        mResTitle.put(ScreenAdjustSettingContract.STEP_1_POSITION, getString(R.string.ipc_recognition_tip_position));
        mResTitle.put(ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM, getString(R.string.ipc_recognition_tip_zoom));
        mResTitle.put(ScreenAdjustSettingContract.STEP_3_FOCUS, getString(R.string.ipc_recognition_tip_focus));
        mResTitle.put(ScreenAdjustSettingContract.STEP_4_LINE, getString(R.string.ipc_recognition_line_start));
        String next = getString(R.string.str_next);
        mResNext.put(ScreenAdjustSettingContract.STEP_1_POSITION, next);
        mResNext.put(ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM, next);
        mResNext.put(ScreenAdjustSettingContract.STEP_3_FOCUS, next);
        mResNext.put(ScreenAdjustSettingContract.STEP_4_LINE, getString(R.string.str_complete));
        mResTip.put(ScreenAdjustSettingContract.STEP_1_POSITION, getString(R.string.ipc_recognition_tip_position));
        mResTip.put(ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM, getString(R.string.ipc_recognition_tip_zoom));
        mResTip.put(ScreenAdjustSettingContract.STEP_3_FOCUS, getString(R.string.ipc_recognition_tip_focus));
        mResTip.put(ScreenAdjustSettingContract.STEP_4_LINE, getString(R.string.ipc_recognition_tip_line));
        mResLineTitle.put(DoorLineView.STATE_INIT, getString(R.string.ipc_recognition_line_start));
        mResLineTitle.put(DoorLineView.STATE_START, getString(R.string.ipc_recognition_line_end));
        mResLineTitle.put(DoorLineView.STATE_END, getString(R.string.ipc_recognition_line_end));
        mResZoomIn = ContextCompat.getDrawable(this, R.drawable.adjust_zoom_in);
        mResZoomOut = ContextCompat.getDrawable(this, R.drawable.adjust_zoom_out);
        mResFocusPlus = ContextCompat.getDrawable(this, R.drawable.adjust_focus_plus);
        mResFocusMinus = ContextCompat.getDrawable(this, R.drawable.adjust_focus_minus);
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
        updateTip(mResTip.get(step), 0);
        updateTipShow(true);
        updateControlBtnShow(false);
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        switch (step) {
            case ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM:
                updateControlBtn(mResZoomIn, mResZoomOut);
                mPresenter.updateControlBtnEnable(true);
                break;
            case ScreenAdjustSettingContract.STEP_3_FOCUS:
                updateControlBtn(mResFocusPlus, mResFocusMinus);
                mPresenter.updateControlBtnEnable(false);
                break;
            case ScreenAdjustSettingContract.STEP_4_LINE:
                updateTipShow(false);
                Rect boundary = new Rect(0, Math.max(0, mTvTitle.getBottom() - mVideoView.getTop()),
                        mVideoView.getWidth(), mVideoView.getHeight());
                mLineView.init(boundary);
                mLineView.setVisibility(View.VISIBLE);
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
        if (mStepIndex == ScreenAdjustSettingContract.STEP_1_POSITION) {
            stopPlay();
            finish();
        } else {
            updateViewsStepTo(--mStepIndex);
        }
    }

    @Click(resName = "tv_setting_next")
    void onNext() {
        if (mStepIndex == ScreenAdjustSettingContract.STEP_1_POSITION) {
            showLoadingDialog();
            mPresenter.updateState();
        } else if (mStepIndex == ScreenAdjustSettingContract.STEP_4_LINE) {
            Pair<DoorLineView.Point, DoorLineView.Point> points = mLineView.getPoints();
            DoorLineView.Point start = points.first.getX() < points.second.getX() ?
                    points.first : points.second;
            DoorLineView.Point end = points.first.getX() < points.second.getX() ?
                    points.second : points.first;

            int[] lineStart = {(int) (start.getX() * STANDARD_VIDEO_WIDTH / mVideoView.getWidth()),
                    (int) (start.getY() * STANDARD_VIDEO_HEIGHT / mVideoView.getHeight())};
            int[] lineEnd = {(int) (end.getX() * STANDARD_VIDEO_WIDTH / mVideoView.getWidth()),
                    (int) (end.getY() * STANDARD_VIDEO_HEIGHT / mVideoView.getHeight())};

            if (lineEnd[0] - lineStart[0] < LINE_POINT_GAP_LIMIT) {
                shortTip(R.string.ipc_recognition_line_error);
                return;
            }

            showLoadingDialog();
            mPresenter.line(lineStart, lineEnd);
        } else {
            updateViewsStepTo(++mStepIndex);
        }
    }

    @Click(resName = "btn_setting_btn_plus")
    void onPlusClick() {
        if (mStepIndex == ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showDarkLoading(mResLoading);
            mPresenter.zoom(true);
        } else if (mStepIndex == ScreenAdjustSettingContract.STEP_3_FOCUS) {
            showDarkLoading(mResLoading);
            mPresenter.focus(true);
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_minus")
    void onMinusClick() {
        if (mStepIndex == ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showDarkLoading(mResLoading);
            mPresenter.zoom(false);
        } else if (mStepIndex == ScreenAdjustSettingContract.STEP_3_FOCUS) {
            showDarkLoading(mResLoading);
            mPresenter.focus(false);
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when minus clicked.");
        }
    }

    @Click(resName = "btn_setting_btn_reset")
    void onResetClick() {
        if (mStepIndex == ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM) {
            showDarkLoading(mResLoading);
            mPresenter.zoomReset();
        } else if (mStepIndex == ScreenAdjustSettingContract.STEP_3_FOCUS) {
            showDarkLoading(mResLoading);
            mPresenter.focusReset();
        } else {
            LogCat.e(TAG, "Step of recognition ERROR when plus clicked.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopPlay();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (p2pService != null) {
            p2pService.startDecode();
        }
    }

    private void pausePlay() {
        if (p2pService != null) {
            p2pService.setNeedReinitialize(true);
            p2pService.stopRunning();
        }
    }

    private void stopPlay() {
        if (!isFromLive && p2pService != null) {
            p2pService.release();
            p2pService = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    void p2pPrepare() {
        p2pService.init(mVideoView.getHolder().getSurface(), this);
        initP2pLive();
    }

    //开始直播
    @Background
    void initP2pLive() {
        if (p2pService != null) {
            if (isFromLive) {
                p2pService.startPlay();
            } else {
                p2pService.initP2pLive();
            }
        }
    }

    private void dismissTip(int step) {
        updateTipShow(false);
        updateControlBtnShow(false);
        mFaceCase.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        switch (step) {
            case ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM:
            case ScreenAdjustSettingContract.STEP_3_FOCUS:
                updateFaceCaseSize();
                mFaceCase.setVisibility(View.VISIBLE);
                updateControlBtnShow(true);
                break;
            case ScreenAdjustSettingContract.STEP_4_LINE:
                mLineView.setVisibility(View.VISIBLE);
                if (mLineView.getState() != DoorLineView.DRAG_STATE_END) {
                    mTvNext.setEnabled(false);
                }
                break;
            default:
        }
    }

    private void updateFaceCaseSize() {
        ViewGroup.LayoutParams lp = mFaceCase.getLayoutParams();
        int width = mVideoView.getWidth();
        int realSize = width * FACE_CASE_SIZE / STANDARD_VIDEO_WIDTH;
        lp.width = realSize;
        lp.height = realSize;
        mFaceCase.setLayoutParams(lp);
    }

    private void updateTitle(String title, String nextText) {
        mTvTitle.setText(title);
        mTvNext.setText(nextText);
    }

    private void updateTip(String content, int imageRes) {
        if (imageRes == 0) {
            mTvTipContent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            mTvTipContent.setCompoundDrawablePadding(0);
            mTvTipContent.setBackgroundResource(R.drawable.setting_bg_tip_text);
        } else {
            mTvTipContent.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                    imageRes, 0, 0);
            mTvTipContent.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_16));
            mTvTipContent.setBackground(null);
        }
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
        new CommonDialog.Builder(context)
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

    @Override
    public void onPlayFail() {
        showErrorDialog(R.string.network_error);
    }

    @Override
    public void onPlayStarted() {
        hideLoadingDialog();
    }

    @Override
    public void onPlayFinished() {

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
            if (mStepIndex != ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM) {
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
                        showDarkLoading(mResLoading);
                    }
                default:
                    break;
            }
            return true;
        }
    }

    private class DoorLineStateChangeListener implements DoorLineView.OnStateChangeListener {

        @Override
        public void onStateChanged(int state) {
            updateTitle(mResLineTitle.get(state), mResNext.get(ScreenAdjustSettingContract.STEP_4_LINE));
            switch (state) {
                case DoorLineView.STATE_INIT:
                    updateTipShow(true);
                    updateTip(getString(R.string.ipc_recognition_line_tip1), R.mipmap.adjust_line_tip1);
                    mTvTitle.setText(R.string.ipc_recognition_line_title1);
                    updateNextEnable(false);
                    break;
                case DoorLineView.STATE_START:
                    updateTipShow(true);
                    updateTip(getString(R.string.ipc_recognition_line_tip2), R.mipmap.adjust_line_tip2);
                    mTvTitle.setText(R.string.ipc_recognition_line_title2);
                    updateNextEnable(false);
                    break;
                case DoorLineView.STATE_END:
                    updateTipShow(true);
                    updateTip(getString(R.string.ipc_recognition_line_tip3), R.mipmap.adjust_line_tip3);
                    mTvTitle.setText(R.string.ipc_recognition_line_title3);
                    updateNextEnable(true);
                default:
            }
        }

        @Override
        public boolean isLineInvalid(DoorLineView.Point start, DoorLineView.Point end) {
            float x1 = start.getX() * STANDARD_VIDEO_WIDTH / mVideoView.getWidth();
            float x2 = end.getX() * STANDARD_VIDEO_WIDTH / mVideoView.getWidth();
            boolean invalid = Math.abs(x1 - x2) < LINE_POINT_GAP_LIMIT;
            if (invalid) {
                shortTip(R.string.ipc_recognition_line_error);
            }
            return invalid;
        }
    }

}
