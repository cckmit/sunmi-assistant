package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.utils.CommonHelper;

/**
 * @author yangShiJie
 * @date 2019-10-11
 */
public class SettingItemEdittextLayout extends RelativeLayout {

    public RelativeLayout parentLayout;
    public ImageView ivMark;
    public TextView tvLeft;
    public TextView tvRight;
    public ClearableEditText etContent;
    public View divider;

    private Context mContext;
    private float height;
    private Drawable leftMark;
    private float leftImageSize;
    private String leftText = "";
    private float leftTextSize = 0;
    private int leftTextColor = 0;

    private String rightText = "";
    private float rightTextSize = 0;
    private int rightTextColor = 0;

    private String editTextContent, editTextHint;
    private float editTextSize;
    private int editTextColor;
    private int editTextHintColor;
    private int editTextInputType;
    private Drawable editTextBackground;
    private float editTextRightPadding;

    private boolean dividerShow;
    private int dividerColor;
    private float dividerHeight;

    private int defaultLeftTextColor = 0xFF525866;
    private int defaultRightTextColor = 0xFFA1A7B3;
    private int defaultEditTextColor = 0xFF303540;
    private int defaultEditTextHintColor = 0xFFA1A7B3;
    private int defaultDividerColor = 0x1A333C4F;

    public SettingItemEdittextLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public SettingItemEdittextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        attributeSet(attrs);
        initLayout();
    }

    public SettingItemEdittextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        attributeSet(attrs);
        initLayout();
    }

    private void initLayout() {
        View view = View.inflate(mContext, R.layout.view_edittext_setting_item, this);
        parentLayout = view.findViewById(R.id.rl_setting_item);
        ivMark = view.findViewById(R.id.iv_mark);
        tvLeft = view.findViewById(R.id.tv_left);
        tvRight = view.findViewById(R.id.tv_right);
        etContent = view.findViewById(R.id.et_content);
        divider = view.findViewById(R.id.divider);

        if (parentLayout != null) {
            parentLayout.getLayoutParams().height = (int) height;
        }
        ivMark.setImageDrawable(leftMark);
        if (leftImageSize > 0) {
            ivMark.getLayoutParams().width = (int) leftImageSize;
            ivMark.getLayoutParams().height = (int) leftImageSize;
        }
        //左侧textView
        tvLeft.setText(leftText);
        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        tvLeft.setTextColor(leftTextColor);
        //右侧textView
        tvRight.setText(rightText);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        tvRight.setTextColor(rightTextColor);
        //editText
        etContent.setText(editTextContent);
        etContent.setHint(editTextHint);
        etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextSize);
        etContent.setTextColor(editTextColor);
        etContent.setHintTextColor(editTextHintColor);
        etContent.setBackground(editTextBackground);
        etContent.setInputType(editTextInputType);
        if (editTextRightPadding > 0) {
            etContent.setPadding(0, 0, (int) editTextRightPadding, 0);
        }
        //分割线
        if (dividerShow) {
            divider.setVisibility(VISIBLE);
            divider.setBackgroundColor(dividerColor);
            ViewGroup.LayoutParams lp = divider.getLayoutParams();
            lp.height = (int) dividerHeight;
            divider.setLayoutParams(lp);
        } else {
            divider.setVisibility(GONE);
        }
    }

    private void attributeSet(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SettingItemEdittextLayout);
        height = a.getDimension(R.styleable.SettingItemEdittextLayout_parentHeight, CommonHelper.dp2px(mContext, 48));
        leftMark = a.getDrawable(R.styleable.SettingItemEdittextLayout_imageLeft);
        leftImageSize = a.getDimension(R.styleable.SettingItemEdittextLayout_imageLeftSize, CommonHelper.dp2px(mContext, 0));

        leftText = a.getString(R.styleable.SettingItemEdittextLayout_leftText);
        leftTextSize = a.getDimensionPixelSize(R.styleable.SettingItemEdittextLayout_leftTextSize, CommonHelper.dp2px(mContext, 16));
        leftTextColor = a.getColor(R.styleable.SettingItemEdittextLayout_leftTextColor, defaultLeftTextColor);

        rightText = a.getString(R.styleable.SettingItemEdittextLayout_rightText);
        rightTextSize = a.getDimensionPixelSize(R.styleable.SettingItemEdittextLayout_rightTextSize, CommonHelper.dp2px(mContext, 16));
        rightTextColor = a.getColor(R.styleable.SettingItemEdittextLayout_rightTextColor, defaultRightTextColor);

        editTextContent = a.getString(R.styleable.SettingItemEdittextLayout_editTextContent);
        editTextHint = a.getString(R.styleable.SettingItemEdittextLayout_editTextHint);
        editTextSize = a.getDimension(R.styleable.SettingItemEdittextLayout_editTextSize, CommonHelper.dp2px(mContext, 16));
        editTextColor = a.getColor(R.styleable.SettingItemEdittextLayout_editTextColor, defaultEditTextColor);
        editTextHintColor = a.getColor(R.styleable.SettingItemEdittextLayout_editTextHintColor, defaultEditTextHintColor);
        editTextBackground = a.getDrawable(R.styleable.SettingItemEdittextLayout_editTextBackground);
        editTextInputType = a.getInt(R.styleable.SettingItemEdittextLayout_editTextInputType, EditorInfo.TYPE_NULL);
        editTextRightPadding = a.getDimension(R.styleable.SettingItemEdittextLayout_editTextRightPadding, CommonHelper.dp2px(mContext, 20));

        dividerShow = a.getBoolean(R.styleable.SettingItemEdittextLayout_dividerShow, false);
        dividerColor = a.getColor(R.styleable.SettingItemEdittextLayout_dividerColor, defaultDividerColor);
        dividerHeight = a.getDimension(R.styleable.SettingItemEdittextLayout_dividerHeight, mContext.getResources().getDimension(R.dimen.dp_0_5));
        a.recycle();
    }

    public void setLeftText(String text) {
        leftText = text;
        tvLeft.setText(leftText);
    }

    public void setLeftTextSize(float size) {
        tvLeft.setTextSize(CommonHelper.dp2px(mContext, size));
    }

    public void setLeftTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    public TextView getRightText() {
        return tvRight;
    }

    public void setRightText(String text) {
        rightText = text;
        tvRight.setText(rightText);
    }

    public void setRightTextSize(float size) {
        tvRight.setTextSize(CommonHelper.dp2px(mContext, size));
    }

    public void setRightTextColor(int color) {
        tvRight.setTextColor(color);
    }

    public ClearableEditText getEditTextText() {
        return etContent;
    }

    public void setEditTextText(String text) {
        editTextContent = text;
        etContent.setText(editTextContent);
    }

    public void setEditTextSize(float size) {
        etContent.setTextSize(CommonHelper.dp2px(mContext, size));
    }

    public void setEditTextColor(int color) {
        etContent.setTextColor(color);
    }

    public void setEditTextHintColor(int color) {
        etContent.setHintTextColor(color);
    }
}
