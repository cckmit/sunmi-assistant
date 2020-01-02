package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.utils.CommonHelper;

/**
 * @author yangShiJie
 * @date 2019-10-11
 */
public class SettingItemEditTextLayout extends RelativeLayout {

    public RelativeLayout parentLayout;
    public ImageView ivMark;
    public TextView tvLeft;
    public TextView tvRight;
    public ClearableEditText etContent;
    public View divider;
    private boolean isInterceptTouchEvent;

    private Context mContext;

    public SettingItemEditTextLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public SettingItemEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
        attributeSet(attrs);
    }

    public SettingItemEditTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
        attributeSet(attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isInterceptTouchEvent;
    }

    private void initLayout() {
        View view = View.inflate(mContext, R.layout.view_edittext_setting_item, this);
        parentLayout = view.findViewById(R.id.rl_setting_item);
        ivMark = view.findViewById(R.id.iv_mark);
        tvLeft = view.findViewById(R.id.tv_left);
        tvRight = view.findViewById(R.id.tv_right);
        etContent = view.findViewById(R.id.et_content);
        divider = view.findViewById(R.id.divider);
    }

    private void attributeSet(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SettingItemEditTextLayout);
        float height = a.getDimension(R.styleable.SettingItemEditTextLayout_parentHeight, CommonHelper.dp2px(mContext, 48));

        if (parentLayout != null) {
            parentLayout.getLayoutParams().height = (int) height;
        }

        Drawable leftMark = a.getDrawable(R.styleable.SettingItemEditTextLayout_imageLeft);
        ivMark.setImageDrawable(leftMark);

        float leftImageSize = a.getDimension(R.styleable.SettingItemEditTextLayout_imageLeftSize, CommonHelper.dp2px(mContext, 0));
        if (leftImageSize > 0) {
            ivMark.getLayoutParams().width = (int) leftImageSize;
            ivMark.getLayoutParams().height = (int) leftImageSize;
        }

        //左侧textView
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_leftText)) {
            tvLeft.setVisibility(VISIBLE);
            tvLeft.setText(a.getString(R.styleable.SettingItemEditTextLayout_leftText));
            if (a.hasValue(R.styleable.SettingItemEditTextLayout_leftTextSize)) {
                tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        a.getDimensionPixelSize(R.styleable.SettingItemEditTextLayout_leftTextSize, CommonHelper.dp2px(mContext, 16)));
            }

            if (a.hasValue(R.styleable.SettingItemEditTextLayout_leftTextColor)) {
                int defaultLeftTextColor = 0xFF525866;
                tvLeft.setTextColor(a.getColor(R.styleable.SettingItemEditTextLayout_leftTextColor, defaultLeftTextColor));
            }
        }

        //右侧textView
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_rightText)) {
            tvRight.setVisibility(VISIBLE);
            tvRight.setText(a.getString(R.styleable.SettingItemEditTextLayout_rightText));
            if (a.hasValue(R.styleable.SettingItemEditTextLayout_rightTextSize)) {
                tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        a.getDimensionPixelSize(R.styleable.SettingItemEditTextLayout_rightTextSize, CommonHelper.dp2px(mContext, 16)));
            }
            if (a.hasValue(R.styleable.SettingItemEditTextLayout_rightTextColor)) {
                int defaultRightTextColor = 0xFFA1A7B3;
                tvRight.setTextColor(a.getColor(R.styleable.SettingItemEditTextLayout_rightTextColor, defaultRightTextColor));
            }
        }
        //editText
        etContent.setText(a.getString(R.styleable.SettingItemEditTextLayout_editTextContent));
        etContent.setHint(a.getString(R.styleable.SettingItemEditTextLayout_editTextHint));
        etContent.setInputType(a.getInt(R.styleable.SettingItemEditTextLayout_editTextInputType, EditorInfo.TYPE_NULL));
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_editTextSize)) {
            etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    a.getDimension(R.styleable.SettingItemEditTextLayout_editTextSize, CommonHelper.dp2px(mContext, 16)));
        }
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_editTextColor)) {
            int defaultEditTextColor = 0xFF303540;
            etContent.setTextColor(a.getColor(R.styleable.SettingItemEditTextLayout_editTextColor, defaultEditTextColor));
        }
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_editTextHintColor)) {
            int defaultEditTextHintColor = 0xFFA1A7B3;
            etContent.setHintTextColor(a.getColor(R.styleable.SettingItemEditTextLayout_editTextHintColor, defaultEditTextHintColor));
        }
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_editable)) {
            isInterceptTouchEvent = true;
            etContent.setFocusable(false);
            tvRight.setVisibility(VISIBLE);
            tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow_small, 0);
        }
        if (a.hasValue(R.styleable.SettingItemEditTextLayout_editTextRightPadding)) {
            etContent.setPadding(0, 0, (int) a.getDimension(R.styleable.SettingItemEditTextLayout_editTextRightPadding,
                    CommonHelper.dp2px(mContext, 20)), 0);
        }

        boolean dividerShow = a.getBoolean(R.styleable.SettingItemEditTextLayout_dividerShow, false);
        //分割线
        if (dividerShow) {
            divider.setVisibility(VISIBLE);
            if (a.hasValue(R.styleable.SettingItemEditTextLayout_dividerColor)) {
                int defaultDividerColor = 0x1A333C4F;
                divider.setBackgroundColor(a.getColor(R.styleable.SettingItemEditTextLayout_dividerColor, defaultDividerColor));
            }
            if (a.hasValue(R.styleable.SettingItemEditTextLayout_dividerHeight)) {
                float dividerHeight = a.getDimension(R.styleable.SettingItemEditTextLayout_dividerHeight, mContext.getResources().getDimension(R.dimen.dp_0_5));
                ViewGroup.LayoutParams lp = divider.getLayoutParams();
                lp.height = (int) dividerHeight;
                divider.setLayoutParams(lp);
            }
        }
        a.recycle();
    }

    /**
     * 设置左边文字
     *
     * @param text 内容
     */
    public void setLeftText(String text) {
        tvLeft.setText(text);
    }

    /**
     * 设置左边文字大小
     *
     * @param size 文字大小
     */
    public void setLeftTextSize(float size) {
        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 设置左边文字颜色
     *
     * @param color 颜色
     */
    public void setLeftTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    /**
     * 设置右边文字
     *
     * @param resId 字符串资源id
     */
    public void setRightText(int resId) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(resId);
    }

    /**
     * 设置右边文字
     *
     * @param text 内容
     */
    public void setRightText(String text) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(text);
    }

    /**
     * 设置右边文字大小
     *
     * @param size 文字大小
     */
    public void setRightTextSize(int size) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(size));
    }

    /**
     * 设置右边文字颜色
     *
     * @param color 颜色
     */
    public void setRightTextColor(int color) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setTextColor(color);
    }

    public ClearableEditText getEditText() {
        return etContent;
    }

    /**
     * 获取输入框内的内容
     */
    public String getEditTextText() {
        if (etContent.getText() != null) {
            return etContent.getText().toString();
        } else return "";
    }

    /**
     * 设置输入框的文字
     *
     * @param text 内容
     */
    public void setEditTextText(String text) {
        etContent.setText(text);
    }

    /**
     * 设置输入框的光标位置
     *
     * @param selection 光标位置
     */
    public void setSelection(int selection) {
        etContent.setSelection(selection);
    }

    /**
     * 设置输入框的提示文字
     *
     * @param resId 提示文案的字符串资源id
     */
    public void setEditTextHint(int resId) {
        etContent.setHint(resId);
    }

    /**
     * 获取焦点弹出键盘
     */
    public void showSoftKeyBoard() {
        etContent.setFocusable(true);
        etContent.setFocusableInTouchMode(true);
        etContent.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) etContent
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etContent, 0);
    }

    /**
     * 设置 输入监听
     */
    public void addTextChangedListener(TextWatcher watcher) {
        etContent.addTextChangedListener(watcher);
    }

}
