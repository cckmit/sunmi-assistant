package com.github.mikephil.charting.components;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

/**
 * @author yinhui
 * @date 2019-09-11
 */
public class PieMarkerView extends MarkerView {

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public PieMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    public void refreshContent(Entry e, boolean isLeft) {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

}
