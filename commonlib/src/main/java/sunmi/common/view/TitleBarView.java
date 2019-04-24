package sunmi.common.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.utils.Utils;

public class TitleBarView extends RelativeLayout {
    private LayoutInflater inflater;
    protected Context mContext;
    private View layout;

    private ImageView leftImage; // 左边的图片
    private ImageView leftImage2;//左边第二张图片
    protected ImageView rightImage; // 右边的图片

    protected LinearLayout backLayout;
    private LinearLayout rightLayout;

    private TextView midTitle; // 页面标题

    protected TextView leftText;// 左边的文字TextView
    private TextView rightText; // 右边的文字TextView
    private TextView rightText1;// 右边的文字TextView1

    private RelativeLayout midTab;
    private TextView tab1;
    private TextView tab2;

    private View divider;

    private boolean isAdjustResize;
    private View statusBar;

    public TitleBarView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;
        if (isInEditMode()) {
            //显示一个IDE编辑状态下标题栏
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.dp_65)));
            addView(textView);
        } else {
            inflater = LayoutInflater.from(context);
            layout = inflater.inflate(R.layout.layout_title_bar, this, true);
            layout.setPadding(0, Utils.getStatusBarHeight(context), 0, 0);
            initView();
        }
        readViewAttributeSet(attrs);
    }

    private void initView() {
        leftImage = layout.findViewById(R.id.img_back);
        rightImage = layout.findViewById(R.id.img_right);
        rightText = layout.findViewById(R.id.txt_right);
        midTitle = layout.findViewById(R.id.txt_title);
        leftText = layout.findViewById(R.id.txt_left);
        rightText1 = layout.findViewById(R.id.txt_right_1);

        backLayout = layout.findViewById(R.id.ll_back_layout);
        rightLayout = layout.findViewById(R.id.ll_right_layout);

        midTab = layout.findViewById(R.id.title_tab);
        tab1 = layout.findViewById(R.id.tv_tab_1);
        tab2 = layout.findViewById(R.id.tv_tab_2);
        statusBar = layout.findViewById(R.id.view_status_bar);
        divider = layout.findViewById(R.id.divider);
    }

    private void readViewAttributeSet(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.TitleBarView, 0, 0);
        int leftIconId = typedArray.getResourceId(R.styleable.TitleBarView_leftIcon, -1);
        if (leftIconId != -1) {
            setLeftImageResource(leftIconId);
        }

        String titleText = typedArray.getString(R.styleable.TitleBarView_titleText);
        if (!TextUtils.isEmpty(titleText)) {
            midTitle.setText(titleText);
        }

        int titleTextColor = typedArray.getColor(R.styleable.TitleBarView_titleTextColor, -1);
        if (typedArray.hasValue(R.styleable.TitleBarView_titleTextColor)) {
            midTitle.setTextColor(titleTextColor);
        }
        float titleTextSize = typedArray.getDimensionPixelSize(R.styleable.TitleBarView_titleTextSize, -1);
        if (titleTextSize != -1) {
            midTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }

        int statusBarColor = typedArray.getResourceId(R.styleable.TitleBarView_statusBarColor, -1);
        if (statusBarColor != -1) {
            setTitleColor(statusBarColor);
        }

        String leftTextStr = typedArray.getString(R.styleable.TitleBarView_leftText);
        if (!TextUtils.isEmpty(leftTextStr)) {
            getLeftTextView().setText(leftTextStr);
        }
        int leftTextColor = typedArray.getColor(R.styleable.TitleBarView_leftTextColor, -1);
        if (leftTextColor != -1) {
            getLeftTextView().setTextColor(leftTextColor);
        }
        String rightTextStr = typedArray.getString(R.styleable.TitleBarView_rightText);
        if (!TextUtils.isEmpty(rightTextStr)) {
            setRightTextViewText(rightTextStr);
        }

        int rightIconId = typedArray.getResourceId(R.styleable.TitleBarView_rightIcon, -1);
        if (rightIconId != -1) {
            setRightImageDrawable(rightIconId);
        }

        boolean showDivider = typedArray.getBoolean(R.styleable.TitleBarView_dividerShow, true);
        setDividerVisible(showDivider);

        int dividerColor = typedArray.getColor(R.styleable.TitleBarView_dividerColor, -1);
        if (typedArray.hasValue(R.styleable.TitleBarView_dividerColor)) {
            divider.setBackgroundColor(dividerColor);
        }
        //状态栏
        isAdjustResize = typedArray.getBoolean(R.styleable.TitleBarView_topview_isAdjustResize, false);
        if (isAdjustResize) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager((Activity) mContext);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.c_black);
            }
        } else {
            WindowManager.LayoutParams winParams = ((Activity) mContext).getWindow().getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((winParams.flags & bits) != 0) {
                statusBar.setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, StyleManager.statusBarHeight));
            }
        }

        //点击事件
        boolean leftDefaultClickEnable = typedArray.getBoolean(R.styleable.TitleBarView_leftDefaultClickEnable, false);
        if (leftDefaultClickEnable) {
            if (leftIconId != -1) {
                setLeftImgOnClickListener();
            }
            if (!TextUtils.isEmpty(leftTextStr)) {
                setLeftTextOnClickListener();
            }
        }
        typedArray.recycle();
    }

    /**
     * 设置标题的内容
     */
    public TitleBarView setAppTitle(int resourceId) {
        midTitle.setText(mContext.getResources().getString(resourceId));
        return this;
    }

    /**
     * 设置标题的内容
     */
    public TitleBarView setAppTitle(String title) {
        midTitle.setText(title);
        return this;
    }

    /**
     * 设置标题文字颜色
     */
    public TitleBarView setAppTitleTextColor(int colorId) {
        midTitle.setTextColor(getResources().getColor(colorId));
        leftText.setTextColor(getResources().getColor(colorId));
        return this;
    }

    /**
     * 得到标题
     */
    public TextView getAppTitle() {
        return midTitle;
    }

    /**
     * 设置返回按钮图片资源
     */
    public TitleBarView setLeftImageResource(int resourseID) {
        isLeftTextViewShow(false);
        leftImage.setImageResource(resourseID);
        return this;
    }

    /**
     * 设置返回按钮图片资源
     */
    public TitleBarView setLeftImageDrawable(Drawable drawable) {
        isLeftTextViewShow(false);
        leftImage.setImageDrawable(drawable);
        return this;
    }

    /**
     * 设置左边图片的显示状态
     */
    public TitleBarView setLeftImageVisibility(int visibility) {
        backLayout.setVisibility(visibility);
        return this;
    }

    /**
     * 获取左边 的控件
     */
    public ImageView getLeftImg() {
        return leftImage;
    }

    public TitleBarView setLeftImgOnClickListener() {
        backLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        return this;
    }

    /**
     * 左侧第二张图片点击效果
     */
    public TitleBarView setLeftImg2OnClickListener(int imgResId, OnClickListener onClickListener) {
        layout.findViewById(R.id.img_btn2).setBackgroundResource(imgResId);
        layout.findViewById(R.id.ll_img_btn2).setVisibility(VISIBLE);
        layout.findViewById(R.id.ll_img_btn2).setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 左侧文字点击效果
     */
    public TitleBarView setLeftTextOnClickListener() {
        leftText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        return this;
    }

    public LinearLayout getLeftLayout() {
        return backLayout;
    }

    /**
     * 得到左边的TextView
     */
    public TextView getLeftTextView() {
        isLeftTextViewShow(true);
        return leftText;
    }

    /**
     * 设置左边TextView的内容
     */
    public TitleBarView setLeftText(int source) {
        isLeftTextViewShow(true);
        leftText.setText(mContext.getResources().getString(source));
        return this;
    }

    /**
     * 设置左边TextView的内容
     */
    public TitleBarView setLeftText(String title) {
        isLeftTextViewShow(true);
        leftText.setText(title);
        return this;
    }

    /**
     * 左边是否是TextView显示
     */
    private void isLeftTextViewShow(boolean isShow) {
        if (isShow) {
            backLayout.setVisibility(View.GONE);
            leftText.setVisibility(View.VISIBLE);
        } else {
            backLayout.setVisibility(View.VISIBLE);
            leftText.setVisibility(View.GONE);
        }
    }

    /**
     * 得到右边的图片
     */
    public ImageView getRightImg() {
        isRightTextViewShow(false);
        return rightImage;
    }

    public View getRightLayout() {
        return rightLayout;
    }

    /**
     * 设置右边图片的显示状态
     */
    public TitleBarView setRightImageVisibility(int visibility) {
        rightLayout.setVisibility(visibility);
        return this;
    }

    /**
     * 得到右边的TextView
     */
    public TextView getRightTextView() {
        isRightTextViewShow(true);
        return rightText;
    }

    /**
     * 得到右边的TextView1
     */
    public TextView getRightTextView1() {
        return rightText1;
    }

    /**
     * 设置右边TextView的内容
     */
    public TitleBarView setRightTextViewText(int source) {
        isRightTextViewShow(true);
        rightText.setText(mContext.getResources().getString(source));
        return this;
    }

    /**
     * 设置右边TextView的内容
     */
    public TitleBarView setRightTextViewText(String title) {
        isRightTextViewShow(true);
        rightText.setText(title);
        return this;
    }

    public TitleBarView setRightTextViewColor(int resId) {
        rightText.setTextColor(getResources().getColor(resId));
        return this;
    }

    /**
     * 右侧文字点击事件
     */
    public TextView getRightText() {
        return rightText;
    }

    /**
     * 设置右边TextView是否可以点击
     */
    public TitleBarView setRightTextViewEnable(boolean isEnable) {
        rightText.setEnabled(isEnable);
        return this;
    }

    /**
     * 设置右边图片的图片
     */
    public TitleBarView setRightImageDrawable(int id) {
        isRightTextViewShow(false);
        rightImage.setImageResource(id);
        return this;
    }

    /**
     * 设置右边图片的图片
     */
    public TitleBarView setRightImageDrawable(Drawable drawable) {
        isRightTextViewShow(false);
        rightImage.setImageDrawable(drawable);
        return this;
    }

    /**
     * 初始化标准头部
     */
    public void initCommonTop(int strRes) {
        setAppTitle(strRes);
        setLeftImgOnClickListener();
        setLeftTextOnClickListener();
        setLeftImageResource(R.mipmap.ic_back);
    }

    /**
     * 右边是否是TextView显示
     */
    public void isRightTextViewShow(boolean isShow) {
        if (isShow) {
            rightLayout.setVisibility(View.GONE);
            rightText.setVisibility(View.VISIBLE);
        } else {
            rightLayout.setVisibility(View.VISIBLE);
            rightText.setVisibility(View.GONE);
        }
    }

    /**
     * 中间的tab
     */
    public RelativeLayout getMiddleTab() {
        return midTab;
    }

    /**
     * 中间的tab1
     */
    public TextView getTab1() {
        return tab1;
    }

    /**
     * 中间的tab2
     */
    public TextView getTab2() {
        return tab2;
    }

    public TitleBarView setTitleColor(int colorId) {
        (layout.findViewById(R.id.rl_topview)).setBackgroundResource(colorId);
        (layout.findViewById(R.id.view_status_bar)).setBackgroundResource(colorId);
        if (isAdjustResize && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager((Activity) mContext);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }
        return this;
    }

    /**
     * 设置topView 背景色，不影状态栏
     */
    public TitleBarView setTitleColorWithoutStatus(@ColorInt int colorId) {
        (layout.findViewById(R.id.rl_topview)).setBackgroundColor(colorId);
        return this;
    }

    public TitleBarView setDividerVisible(boolean isVisible) {
        if (isVisible) {
            divider.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.GONE);
        }
        return this;
    }

    private void closeActivity() {//todo
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

}

