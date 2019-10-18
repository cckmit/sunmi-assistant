package sunmi.common.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-12.
 */
public class LabelTextView extends AppCompatTextView {


    public LabelTextView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs,0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

    }

}
