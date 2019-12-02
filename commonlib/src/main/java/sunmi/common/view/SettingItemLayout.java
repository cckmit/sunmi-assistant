package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.commonlibrary.R;

/**
 * 列表项控件，用于设置列表或参数说明列表等
 *
 * @author yinhui
 * @date 2019-11-19
 */
public class SettingItemLayout extends FrameLayout {

    public static final int STYLE_SINGLE = 0;
    public static final int STYLE_MULTI = 1;
    public static final int STYLE_DEFAULT = -1;

    public static final int DIVIDER_NONE = 0;
    public static final int DIVIDER_TOP = 1;
    public static final int DIVIDER_BOTTOM = 2;
    public static final int DIVIDER_BOTH = 3;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_ARROW = 1;
    public static final int TYPE_CHECKED = 2;
    public static final int TYPE_SWITCH = 3;
    public static final int TYPE_IMAGE = 4;

    private static int sDimenSingleHeight;
    private static int sDimenMarginHorizontal;
    private static int sDimenMarginVertical;
    private static int sDimenMarginGap;

    private ConstraintLayout clContainer;

    private TextView tvEndContent;
    private TextView tvMiddleContent;

    private ImageView ivStartImage;
    private ImageView ivEndImage;
    private Switch scEndSwitch;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvSmallStartContent;
    private TextView tvSmallEndContent;
    private TextView tvTag;

    private View vTopDivider;
    private View vBottomDivider;

    private int type = TYPE_NONE;

    public SettingItemLayout(@NonNull Context context) {
        this(context, STYLE_DEFAULT);
    }

    public SettingItemLayout(@NonNull Context context, int style) {
        this(context, null, style);
    }

    public SettingItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, STYLE_DEFAULT);
    }

    public SettingItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, STYLE_DEFAULT);
    }

    public SettingItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int style) {
        super(context, attrs, defStyleAttr);
        initViews(context);
        setupAttr(context, attrs, defStyleAttr, 0, style);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int style) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context);
        setupAttr(context, attrs, defStyleAttr, defStyleRes, style);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_setting_item, this);
        clContainer = findViewById(R.id.cl_container);
        ivStartImage = findViewById(R.id.iv_start_image);
        ivEndImage = findViewById(R.id.iv_end_image);
        scEndSwitch = findViewById(R.id.sc_end_switch);
        tvTitle = findViewById(R.id.tv_title);
        tvEndContent = findViewById(R.id.tv_end_content);
        tvMiddleContent = findViewById(R.id.tv_content);
        tvSmallStartContent = findViewById(R.id.tv_small_start_content);
        tvSmallEndContent = findViewById(R.id.tv_small_end_content);
        tvTag = findViewById(R.id.tv_tag);
        vTopDivider = findViewById(R.id.top_divider);
        vBottomDivider = findViewById(R.id.bottom_divider);

        if (sDimenSingleHeight == 0) {
            sDimenSingleHeight = (int) getResources().getDimension(R.dimen.dp_48);
        }
        if (sDimenMarginHorizontal == 0) {
            sDimenMarginHorizontal = (int) getResources().getDimension(R.dimen.dp_20);
        }
        if (sDimenMarginVertical == 0) {
            sDimenMarginVertical = (int) getResources().getDimension(R.dimen.dp_16);
        }
        if (sDimenMarginGap == 0) {
            sDimenMarginGap = (int) getResources().getDimension(R.dimen.dp_10);
        }
    }

    private void setupAttr(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int style) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SettingItemLayout, defStyleAttr, defStyleRes);

        // 获取列表项样式，单行样式、多行样式。
        if (style == STYLE_DEFAULT) {
            style = a.getInt(R.styleable.SettingItemLayout_styleType, STYLE_SINGLE);
        }

        ViewGroup.LayoutParams containerLp = clContainer.getLayoutParams();
        switch (style) {
            case STYLE_SINGLE:
                tvContent = tvEndContent;
                containerLp.height = sDimenSingleHeight;
                break;
            case STYLE_MULTI:
                tvContent = tvMiddleContent;
                containerLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            default:
        }

        // 设置可用和可点击
        boolean enabled = a.getBoolean(R.styleable.SettingItemLayout_enabled, isEnabled());
        setEnabled(enabled);

        // 设置左边图片大小
        float startImageSize = a.getDimension(R.styleable.SettingItemLayout_startImageSize, -1);
        if (startImageSize > 0) {
            ViewGroup.LayoutParams startImageLp = ivStartImage.getLayoutParams();
            startImageLp.width = (int) startImageSize;
            startImageLp.height = (int) startImageSize;
        }

        // 设置左边图片
        Drawable image = a.getDrawable(R.styleable.SettingItemLayout_startImage);
        if (image != null) {
            ivStartImage.setVisibility(VISIBLE);
            ivStartImage.setImageDrawable(image);
        } else {
            ivStartImage.setVisibility(GONE);
        }

        // 设置右边元素
        type = a.getInteger(R.styleable.SettingItemLayout_type, TYPE_NONE);
        Drawable endImage = a.getDrawable(R.styleable.SettingItemLayout_endImage);
        float endImageSize = a.getDimension(R.styleable.SettingItemLayout_endImageSize, -1);
        switch (type) {
            case TYPE_NONE:
                break;
            case TYPE_ARROW:
                ivEndImage.setVisibility(VISIBLE);
                ivEndImage.setImageResource(R.drawable.ic_right_arrow_small);
                break;
            case TYPE_CHECKED:
                clContainer.setBackgroundResource(R.color.c_white);
                tvTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.text_common_checkable));
                ivEndImage.setVisibility(VISIBLE);
                ivEndImage.setImageResource(R.drawable.ic_right_check);
                break;
            case TYPE_SWITCH:
                scEndSwitch.setVisibility(VISIBLE);
                updateConstraintForEndSwitch();
                break;
            case TYPE_IMAGE:
                if (endImage != null) {
                    if (endImageSize > 0) {
                        ViewGroup.LayoutParams endImageLp = ivEndImage.getLayoutParams();
                        endImageLp.width = (int) endImageSize;
                        endImageLp.height = (int) endImageSize;
                    }
                    ivEndImage.setVisibility(VISIBLE);
                    ivEndImage.setImageDrawable(endImage);
                }
                break;
            default:
        }

        // 设置Switch状态
        boolean isChecked = a.getBoolean(R.styleable.SettingItemLayout_checked, false);
        setChecked(isChecked);

        // 设置标题
        String title = a.getString(R.styleable.SettingItemLayout_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(VISIBLE);
            tvTitle.setText(title);
        }

        // 设置内容（如果为单行列表项，则为右边辅助文字；如果为多行列表项，则为标题下方文字）
        String content = a.getString(R.styleable.SettingItemLayout_contentText);
        setTextIfExist(tvContent, content);

        // 多行列表项时，设置内容最大行数
        int maxLines = a.getInteger(R.styleable.SettingItemLayout_contentMaxLines, 1);
        tvMiddleContent.setMaxLines(maxLines);

        // 设置底部左边小字内容
        String smallStartContent = a.getString(R.styleable.SettingItemLayout_smallStartContent);
        setTextIfExist(tvSmallStartContent, smallStartContent);

        // 设置底部右边小字内容
        String smallEndContent = a.getString(R.styleable.SettingItemLayout_smallEndContent);
        setTextIfExist(tvSmallEndContent, smallEndContent);

        // 设置Tag标签
        String tag = a.getString(R.styleable.SettingItemLayout_tag);
        setTextIfExist(tvTag, tag);

        // 设置分割线
        int dividerType = a.getInteger(R.styleable.SettingItemLayout_showDivider, DIVIDER_NONE);
        vTopDivider.setVisibility((dividerType == DIVIDER_TOP || dividerType == DIVIDER_BOTH) ?
                VISIBLE : GONE);
        vBottomDivider.setVisibility((dividerType == DIVIDER_BOTTOM || dividerType == DIVIDER_BOTH) ?
                VISIBLE : GONE);

        a.recycle();
    }

    private void setTextIfExist(TextView view, String text) {
        if (!TextUtils.isEmpty(text)) {
            view.setVisibility(VISIBLE);
            view.setText(text);
        } else {
            view.setVisibility(GONE);
        }
    }

    private void setTextIfExist(TextView view, @StringRes int resId) {
        if (resId != 0) {
            view.setVisibility(VISIBLE);
            view.setText(resId);
        } else {
            view.setVisibility(GONE);
        }
    }

    private void setupGoneMargin(ConstraintSet set) {
        set.setGoneMargin(R.id.tv_title, ConstraintSet.START, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_title, ConstraintSet.BOTTOM, sDimenMarginVertical);
        set.setGoneMargin(R.id.tv_end_content, ConstraintSet.START, sDimenMarginGap);
        set.setGoneMargin(R.id.tv_end_content, ConstraintSet.END, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_content, ConstraintSet.START, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_content, ConstraintSet.END, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_content, ConstraintSet.BOTTOM, sDimenMarginVertical);
        set.setGoneMargin(R.id.tv_small_start_content, ConstraintSet.START, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_small_start_content, ConstraintSet.END, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_small_end_content, ConstraintSet.START, sDimenMarginHorizontal);
        set.setGoneMargin(R.id.tv_small_end_content, ConstraintSet.END, sDimenMarginHorizontal);
    }

    private void updateConstraintForEndImage() {
        ConstraintSet set = new ConstraintSet();
        set.clone(clContainer);
        setupGoneMargin(set);
        set.connect(R.id.tv_end_content, ConstraintSet.END, R.id.iv_end_image, ConstraintSet.START, sDimenMarginGap);
        set.connect(R.id.tv_content, ConstraintSet.END, R.id.iv_end_image, ConstraintSet.START, sDimenMarginGap);
        set.connect(R.id.tv_small_end_content, ConstraintSet.END, R.id.iv_end_image, ConstraintSet.START, sDimenMarginGap);
        set.applyTo(clContainer);
    }

    private void updateConstraintForEndSwitch() {
        ConstraintSet set = new ConstraintSet();
        set.clone(clContainer);
        setupGoneMargin(set);
        set.connect(R.id.tv_end_content, ConstraintSet.END, R.id.sc_end_switch, ConstraintSet.START, sDimenMarginGap);
        set.connect(R.id.tv_content, ConstraintSet.END, R.id.sc_end_switch, ConstraintSet.START, sDimenMarginGap);
        set.connect(R.id.tv_small_end_content, ConstraintSet.END, R.id.sc_end_switch, ConstraintSet.START, sDimenMarginGap);
        set.applyTo(clContainer);
    }

    public ImageView getStartImage() {
        return ivStartImage;
    }

    public ImageView getEndImage() {
        return ivEndImage;
    }

    public Switch getSwitch() {
        return scEndSwitch;
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public TextView getContent() {
        return tvContent;
    }

    public TextView getSmallStartContent() {
        return tvSmallStartContent;
    }

    public TextView getSmallEndContent() {
        return tvSmallEndContent;
    }

    public TextView getTagView() {
        return tvTag;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        clContainer.setEnabled(enabled);
        int count = clContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            clContainer.getChildAt(i).setEnabled(enabled);
        }
    }

    public void setType(int type) {
        if (this.type == type) {
            return;
        }
        this.type = type;
        ivStartImage.setVisibility(GONE);
        ivEndImage.setVisibility(GONE);
        scEndSwitch.setVisibility(GONE);
        tvContent.setVisibility(GONE);
        tvSmallStartContent.setVisibility(GONE);
        tvSmallEndContent.setVisibility(GONE);
        tvTag.setVisibility(GONE);
        switch (type) {
            case TYPE_NONE:
                clContainer.setBackgroundResource(R.drawable.bg_common_list_item);
                tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_common_main));
                updateConstraintForEndImage();
                break;
            case TYPE_ARROW:
                clContainer.setBackgroundResource(R.drawable.bg_common_list_item);
                tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_common_main));
                ivEndImage.setVisibility(VISIBLE);
                ivEndImage.setImageResource(R.drawable.ic_right_arrow_small);
                updateConstraintForEndImage();
                break;
            case TYPE_CHECKED:
                clContainer.setBackgroundResource(R.color.c_white);
                tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.text_common_checkable));
                ivEndImage.setVisibility(VISIBLE);
                ivEndImage.setImageResource(R.drawable.ic_right_check);
                updateConstraintForEndImage();
                break;
            case TYPE_SWITCH:
                clContainer.setBackgroundResource(R.drawable.bg_common_list_item);
                tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_common_main));
                scEndSwitch.setVisibility(VISIBLE);
                updateConstraintForEndSwitch();
                break;
            case TYPE_IMAGE:
                clContainer.setBackgroundResource(R.drawable.bg_common_list_item);
                tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_common_main));
                updateConstraintForEndImage();
                break;
            default:
        }
    }

    public void setStartImageDrawable(Drawable drawable) {
        if (drawable != null) {
            ivStartImage.setVisibility(VISIBLE);
            ivStartImage.setImageDrawable(drawable);
        } else {
            ivStartImage.setVisibility(GONE);
        }
    }

    public void setStartImageResource(@DrawableRes int resId) {
        if (resId != 0) {
            ivStartImage.setVisibility(VISIBLE);
            ivStartImage.setImageResource(resId);
        } else {
            ivStartImage.setVisibility(GONE);
        }
    }

    public void setStartImageSize(int width, int height) {
        ViewGroup.LayoutParams lp = ivStartImage.getLayoutParams();
        lp.width = width;
        lp.height = height;
    }

    public void setEndImageDrawable(Drawable drawable) {
        if (drawable != null) {
            ivEndImage.setVisibility(VISIBLE);
            ivEndImage.setImageDrawable(drawable);
        } else {
            ivEndImage.setVisibility(GONE);
        }
    }

    public void setEndImageResource(@DrawableRes int resId) {
        if (resId != 0) {
            ivEndImage.setVisibility(VISIBLE);
            ivEndImage.setImageResource(resId);
        } else {
            ivEndImage.setVisibility(GONE);
        }
    }

    public void setEndImageSize(int width, int height) {
        ViewGroup.LayoutParams lp = ivEndImage.getLayoutParams();
        lp.width = width;
        lp.height = height;
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(title);
    }

    public void setTitle(@StringRes int resId) {
        if (resId == 0) {
            return;
        }
        tvTitle.setText(resId);
    }

    public void setContent(String content) {
        setTextIfExist(tvContent, content);
    }

    public void setContentMaxLines(int lines) {
        tvMiddleContent.setMaxLines(lines);
    }

    public void setContent(@StringRes int resId) {
        setTextIfExist(tvContent, resId);
    }

    public void setSmallStartContent(String content) {
        setTextIfExist(tvSmallStartContent, content);
    }

    public void setSmallStartContent(@StringRes int resId) {
        setTextIfExist(tvSmallStartContent, resId);
    }

    public void setSmallEndContent(String content) {
        setTextIfExist(tvSmallEndContent, content);
    }

    public void setSmallEndContent(@StringRes int resId) {
        setTextIfExist(tvSmallEndContent, resId);
    }

    public void setTagText(String content) {
        setTextIfExist(tvTag, content);
    }

    public void setTagText(@StringRes int resId) {
        setTextIfExist(tvTag, resId);
    }

    public void toggle() {
        if (type == TYPE_SWITCH) {
            scEndSwitch.toggle();
        }
    }

    public void setChecked(boolean checked) {
        if (type == TYPE_CHECKED) {
            tvTitle.setSelected(checked);
            ivEndImage.setSelected(checked);
        } else if (type == TYPE_SWITCH) {
            scEndSwitch.setChecked(checked);
        }
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        if (listener == null) {
            return;
        }
        scEndSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onCheckedChanged(SettingItemLayout.this, isChecked));
    }

    public void setDivider(int divider) {
        vTopDivider.setVisibility((divider == DIVIDER_TOP || divider == DIVIDER_BOTH) ?
                VISIBLE : GONE);
        vBottomDivider.setVisibility((divider == DIVIDER_BOTTOM || divider == DIVIDER_BOTH) ?
                VISIBLE : GONE);
    }

    public CharSequence getTitleText() {
        return tvTitle.getText();
    }

    public CharSequence getContentText() {
        return tvContent.getText();
    }

    public boolean isChecked() {
        return scEndSwitch.isChecked();
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param view      The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(SettingItemLayout view, boolean isChecked);
    }

}
