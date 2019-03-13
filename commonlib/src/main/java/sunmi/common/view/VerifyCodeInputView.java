package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.commonlibrary.R;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.utils.CommonHelper;

public class VerifyCodeInputView extends LinearLayout implements TextWatcher, View.OnKeyListener {

    private static final String TAG = "VerificationCodeInput";
    private final static String TYPE_NUMBER = "number";
    private final static String TYPE_TEXT = "text";
    private final static String TYPE_PASSWORD = "password";
    private final static String TYPE_PHONE = "phone";

    private int defaultColor = 0xff000000;
    private int box = 4;
    private int boxWidth = 80;
    private int boxHeight = 80;
    private float textSize = 0;
    private int textColor = 0;
    private int childHPadding = 14;
    private int childVPadding = 10;
    private String inputType = TYPE_NUMBER;
    private Drawable boxBgFocus = null;
    private Drawable boxBgNormal = null;
    private Listener listener;
    private boolean focus = false;
    private List<EditText> mEditTextList = new ArrayList<>();
    private int currentPosition = 0;

    public VerifyCodeInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeInputView);
        box = a.getInt(R.styleable.VerifyCodeInputView_box, 4);

        childHPadding = (int) a.getDimension(R.styleable.VerifyCodeInputView_child_h_padding, 0);
        childVPadding = (int) a.getDimension(R.styleable.VerifyCodeInputView_child_v_padding, 0);
        boxBgFocus = a.getDrawable(R.styleable.VerifyCodeInputView_box_bg_focus);
        boxBgNormal = a.getDrawable(R.styleable.VerifyCodeInputView_box_bg_normal);
        inputType = a.getString(R.styleable.VerifyCodeInputView_inputType);
        boxWidth = (int) a.getDimension(R.styleable.VerifyCodeInputView_child_width, boxWidth);
        boxHeight = (int) a.getDimension(R.styleable.VerifyCodeInputView_child_height, boxHeight);
        textSize = a.getDimensionPixelSize(R.styleable.VerifyCodeInputView_text_size, CommonHelper.dp2px(context, 14));
        textColor = a.getColor(R.styleable.VerifyCodeInputView_text_color, defaultColor);
        initViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initViews() {
        for (int i = 0; i < box; i++) {
            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(boxWidth, boxHeight);
            layoutParams.bottomMargin = childVPadding;
            layoutParams.topMargin = childVPadding;
            layoutParams.leftMargin = childHPadding;
            layoutParams.rightMargin = childHPadding;
            layoutParams.gravity = Gravity.BOTTOM;

            editText.setOnKeyListener(this);
            if (i == 0)
                setBg(editText, true);
            else setBg(editText, false);
            editText.setTextColor(textColor);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.CENTER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            if (TYPE_NUMBER.equals(inputType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (TYPE_PASSWORD.equals(inputType)) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (TYPE_TEXT.equals(inputType)) {
                editText.setKeyListener(new DigitsKeyListener() {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_TEXT_VARIATION_PASSWORD;
                    }

                    @NonNull
                    @Override
                    protected char[] getAcceptedChars() {
                        String dataID = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        return dataID.toCharArray();
                    }
                });
//                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            editText.setId(i);
            editText.setEms(1);
            editText.addTextChangedListener(this);
            addView(editText, i);
            mEditTextList.add(editText);
        }
    }

    public void clearText() {
        for (EditText editText : mEditTextList) {
            editText.setText("");
        }
        currentPosition = 0;
        if (mEditTextList.size() > 0)
            mEditTextList.get(currentPosition).requestFocus();
    }

    private void backFocus() {
        int count = getChildCount();
        EditText editText;
        for (int i = count - 1; i >= 0; i--) {
            editText = (EditText) getChildAt(i);
            if (editText.getText().length() == 1) {
                editText.requestFocus();
                setBg(mEditTextList.get(i), true);
                //setBg(mEditTextList.get(i-1),true);
                editText.setSelection(1);
                return;
            }
        }
    }

    private void focus() {
        int count = getChildCount();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            if (editText.getText().length() < 1) {
                editText.requestFocus();
                return;
            }
        }
    }

    private void setBg(EditText editText, boolean focus) {
        editText.setBackground(null);
        if (boxBgNormal != null && !focus) {
            editText.setBackground(boxBgNormal);
        } else if (boxBgFocus != null && focus) {
            editText.setBackground(boxBgFocus);
        }
    }

    private void setBg() {
        int count = getChildCount();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            if (boxBgNormal != null && !focus) {
                editText.setBackground(boxBgNormal);
            } else if (boxBgFocus != null && focus) {
                editText.setBackground(boxBgFocus);
            }
        }
    }

    private void checkAndCommit() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean full = true;
        for (int i = 0; i < box; i++) {
            EditText editText = (EditText) getChildAt(i);
            String content = editText.getText().toString();
            if (content.length() == 0) {
                full = false;
                break;
            } else {
                stringBuilder.append(content);
            }
        }
        if (full) {
            if (listener != null) {
                listener.onComplete(stringBuilder.toString());
//                setEnabled(false);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    public void setOnCompleteListener(Listener listener) {
        this.listener = listener;
    }

    @Override

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        if (count > 0) {
            View child = getChildAt(0);
            int cHeight = child.getMeasuredHeight();
            int cWidth = child.getMeasuredWidth();
            int maxH = cHeight + 2 * childVPadding;
            int maxW = (cWidth + childHPadding) * box + childHPadding;
            setMeasuredDimension(resolveSize(maxW, widthMeasureSpec), resolveSize(maxH, heightMeasureSpec));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            child.setVisibility(View.VISIBLE);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            int cl = (i) * (cWidth + childHPadding);
            int cr = cl + cWidth;
            int ct = childVPadding;
            int cb = ct + cHeight;
            child.layout(cl, ct, cr, cb);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start == 0 && count >= 1 && currentPosition != mEditTextList.size() - 1) {
            currentPosition++;
            mEditTextList.get(currentPosition).requestFocus();
            setBg(mEditTextList.get(currentPosition), true);
            setBg(mEditTextList.get(currentPosition - 1), false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
        } else {
            focus();
            checkAndCommit();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        EditText editText = (EditText) view;
        if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {
            int action = event.getAction();
            if (currentPosition != 0 && action == KeyEvent.ACTION_DOWN) {
                currentPosition--;
                mEditTextList.get(currentPosition).requestFocus();
                setBg(mEditTextList.get(currentPosition), true);
                setBg(mEditTextList.get(currentPosition + 1), false);
                mEditTextList.get(currentPosition).setText("");
            }
        }
        return false;
    }

    public interface Listener {
        void onComplete(String content);
    }

}