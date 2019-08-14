package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.utils.CommonHelper;

/**
 * Class  Name: SettingItemLayout
 * Description: 设置项
 * Created by bruce on 16/7/20
 */
public class SettingItemLayout extends RelativeLayout {

    private Context mContext;

    public RelativeLayout parentLayout;
    public ImageView ivLeft;
    private TextView tvLeft;
    private TextView tvRight;
    public ImageView ivRight;
    public ImageView ivRightTip;
    public ImageView ivToTextLeftImage;

    private float height;
    private String leftText = "";
    private String rightText = "";
    private float leftTextSize = 0;
    private float rightTextSize = 0;
    private int leftTextColor = 0;
    private int rightTextColor = 0;
    private Drawable leftImage;
    private float leftImageSize;
    private Drawable rightImage;
    private float leftPadding;
    private float rightPadding;
    private int defaultColor = 0xff000000;

    public SettingItemLayout(Context context) {
       this(context,null);
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setCustomAttributes(attrs);
        initLayout();
    }

    private void initLayout() {
        View view = View.inflate(mContext, R.layout.view_setting_item, this);
        parentLayout = view.findViewById(R.id.setting_item);
        tvLeft = view.findViewById(R.id.left_text);
        tvRight = view.findViewById(R.id.right_text);
        ivLeft = view.findViewById(R.id.left_image);
        ivRight = view.findViewById(R.id.right_image);
        ivRightTip = view.findViewById(R.id.right_tip_image);
        ivToTextLeftImage = view.findViewById(R.id.to_right_text_left_image);

        if (parentLayout != null) {
            ViewGroup.LayoutParams pp = parentLayout.getLayoutParams();
            parentLayout.getLayoutParams();
            pp.height = (int) height;
            parentLayout.setLayoutParams(pp);
        }
        tvLeft.setText(leftText);
        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        tvLeft.setTextColor(leftTextColor);
        ivLeft.setImageDrawable(leftImage);
        if (leftImageSize > 0f) {
            ViewGroup.LayoutParams layoutParams = ivLeft.getLayoutParams();
            layoutParams.height = (int) leftImageSize;
            layoutParams.width = (int) leftImageSize;
            ivLeft.setLayoutParams(layoutParams);
        }
        ivLeft.setPadding((int) leftPadding, 0, 0, 0);
        tvRight.setText(rightText);
//        if (rightTextSize == 14.0f)
//            tvRight.setTextSize(rightTextSize);
//        else
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        tvRight.setTextColor(rightTextColor);
        ivRight.setImageDrawable(rightImage);
        ivRight.setPadding(0, 0, (int) rightPadding, 0);
    }

    /**
     * @param attrs
     */
    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SettingItemLayout);
        height = a.getDimension(R.styleable.SettingItemLayout_parentHeight, CommonHelper.dp2px(mContext, 45));
        leftText = a.getString(R.styleable.SettingItemLayout_leftText);
        rightText = a.getString(R.styleable.SettingItemLayout_rightText);
        leftTextSize = a.getDimensionPixelSize(R.styleable.SettingItemLayout_leftTextSize, CommonHelper.dp2px(mContext, 16));
        rightTextSize = a.getDimensionPixelSize(R.styleable.SettingItemLayout_rightTextSize, CommonHelper.dp2px(mContext, 14));
        leftTextColor = a.getColor(R.styleable.SettingItemLayout_leftTextColor, defaultColor);
        rightTextColor = a.getColor(R.styleable.SettingItemLayout_rightTextColor, defaultColor);
        leftImage = a.getDrawable(R.styleable.SettingItemLayout_imageLeft);
        leftImageSize = a.getDimension(R.styleable.SettingItemLayout_imageLeftSize, CommonHelper.dp2px(mContext, 0));
        rightImage = a.getDrawable(R.styleable.SettingItemLayout_imageRight);
        leftPadding = a.getDimension(R.styleable.SettingItemLayout_leftPadding, CommonHelper.dp2px(mContext, 1));
        rightPadding = a.getDimension(R.styleable.SettingItemLayout_rightPadding, CommonHelper.dp2px(mContext, 11));
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

    public void setRightText(String text) {
        rightText = text;
        tvRight.setText(rightText);
    }

    public void setRightTextColor(int color) {
        tvRight.setTextColor(color);
    }

    public void setRightTextBackground(int resId) {
        tvRight.setBackgroundResource(resId);
    }

    public ImageView getRightTipImage() {
        return ivRightTip;
    }

    public void setRightTipImage(int resId) {
        ivRightTip.setVisibility(VISIBLE);
        ivRightTip.setImageResource(resId);
    }

    public ImageView getIvToTextLeftImage() {
        return ivToTextLeftImage;
    }

    public void setIvToTextLeftImage(int resId) {
        ivToTextLeftImage.setVisibility(VISIBLE);
        ivToTextLeftImage.setImageResource(resId);
    }

    public void setLeftImage(Drawable drawable) {
        leftImage = drawable;
        ivLeft.setImageDrawable(leftImage);
    }

    public void setLeftImageBitmap(Bitmap bitmap) {
        ivLeft.setImageBitmap(bitmap);
    }

    public ImageView getRightImage() {
        return ivRight;
    }

    public TextView getRightText() {
        return tvRight;
    }

    public void setRightImage(Drawable drawable) {
        rightImage = drawable;
        ivRight.setImageDrawable(rightImage);
    }

}
