package sunmi.common.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * @author yinhui
 * @date 2019-08-13
 */
public abstract class TextLengthWatcher implements TextWatcher {

    private final EditText view;
    private final int length;

    public TextLengthWatcher(EditText view, int maxLength) {
        this.view = view;
        this.length = maxLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            return;
        }
        String str = s.toString().trim();
        if (str.length() > length) {
            onLengthExceed(view, str);
            view.setText(str.substring(0, length));
            view.setSelection(length);
        }
    }

    public void onLengthExceed(EditText view, String content) {
    }
}
