package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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
    public TextView ivToTextLeft;
    private View divider;

    private Drawable leftImage;
    private Drawable rightImage;

    public SettingItemLayout(Context context) {
        this(context, null);
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews();
        setCustomAttributes(attrs);
    }

    private void initViews() {
        View view = View.inflate(mContext, R.layout.view_setting_item, this);
        parentLayout = view.findViewById(R.id.setting_item);
        tvLeft = view.findViewById(R.id.left_text);
        tvRight = view.findViewById(R.id.right_text);
        ivLeft = view.findViewById(R.id.left_image);
        ivRight = view.findViewById(R.id.right_image);
        ivRightTip = view.findViewById(R.id.right_tip_image);
        ivToTextLeft = view.findViewById(R.id.to_right_text_left_image);
        divider = view.findViewById(R.id.divider);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SettingItemLayout);

        float height = a.getDimension(R.styleable.SettingItemLayout_parentHeight, -1);
        if (parentLayout != null && height != -1) {
            ViewGroup.LayoutParams pp = parentLayout.getLayoutParams();
            parentLayout.getLayoutParams();
            pp.height = (int) height;
            parentLayout.setLayoutParams(pp);
        }

        String leftText = a.getString(R.styleable.SettingItemLayout_leftText);
        if (!TextUtils.isEmpty(leftText)) {
            tvLeft.setText(leftText);
        }
        float leftTextSize = a.getDimensionPixelSize(R.styleable.SettingItemLayout_leftTextSize, -1);
        if (leftTextSize != -1) {
            tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        }
        if (a.hasValue(R.styleable.SettingItemLayout_leftTextColor)) {
            int leftTextColor = a.getColor(R.styleable.SettingItemLayout_leftTextColor, -1);
            tvLeft.setTextColor(leftTextColor);
        }

        String rightText = a.getString(R.styleable.SettingItemLayout_rightText);
        if (!TextUtils.isEmpty(rightText)) {
            tvRight.setText(rightText);
        }
        float rightTextSize = a.getDimensionPixelSize(R.styleable.SettingItemLayout_rightTextSize, -1);
        if (rightTextSize != -1) {
            tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        }
        if (a.hasValue(R.styleable.SettingItemLayout_rightTextColor)) {
            int rightTextColor = a.getColor(R.styleable.SettingItemLayout_rightTextColor, -1);
            tvRight.setTextColor(rightTextColor);
        }
        String toLeftText = a.getString(R.styleable.SettingItemLayout_toTextLeftText);
        if (!TextUtils.isEmpty(toLeftText)) {
            ivToTextLeft.setText(rightText);
        }
        float leftPadding = a.getDimension(R.styleable.SettingItemLayout_leftPadding, -1);
        leftImage = a.getDrawable(R.styleable.SettingItemLayout_imageLeft);
        if (leftImage != null) {
            ivLeft.setVisibility(VISIBLE);
            float leftImageSize = a.getDimension(R.styleable.SettingItemLayout_imageLeftSize, -1);
            ivLeft.setImageDrawable(leftImage);
            if (leftImageSize != -1) {
                ViewGroup.LayoutParams layoutParams = ivLeft.getLayoutParams();
                layoutParams.height = (int) leftImageSize;
                layoutParams.width = (int) leftImageSize;
                ivLeft.setLayoutParams(layoutParams);
            }
            if (leftPadding != -1) {
                ivLeft.setPadding((int) leftPadding, 0, 0, 0);
            }
        } else {
            if (leftPadding != -1) {
                tvLeft.setPadding((int) leftPadding, 0, 0, 0);
            }
        }

        rightImage = a.getDrawable(R.styleable.SettingItemLayout_imageRight);
        if (rightImage != null) {
            ivRight.setVisibility(VISIBLE);
            float rightPadding = a.getDimension(R.styleable.SettingItemLayout_rightPadding, -1);
            ivRight.setImageDrawable(rightImage);
            if (rightPadding != -1) {
                ivRight.setPadding(0, 0, (int) rightPadding, 0);
            }
        }

        boolean dividerShow = a.getBoolean(R.styleable.SettingItemLayout_dividerShow, false);
        if (a.hasValue(R.styleable.SettingItemLayout_dividerColor)) {
            int dividerColor = a.getColor(R.styleable.SettingItemLayout_dividerColor, -1);
            divider.setBackgroundColor(dividerColor);
        }
        float dividerHeight = a.getDimension(R.styleable.SettingItemLayout_dividerHeight, -1);

        if (dividerShow) {
            divider.setVisibility(VISIBLE);
            if (dividerHeight != -1) {
                ViewGroup.LayoutParams lp = divider.getLayoutParams();
                lp.height = (int) dividerHeight;
                divider.setLayoutParams(lp);
            }
        }
        a.recycle();
    }

    public void setLeftText(String text) {
        tvLeft.setText(text);
    }

    public void setLeftTextSize(float size) {
        tvLeft.setTextSize(CommonHelper.dp2px(mContext, size));
    }

    public void setLeftTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    public void setRightText(String text) {
        tvRight.setText(text);
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

    public TextView getIvToTextLeft() {
        return ivToTextLeft;
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
