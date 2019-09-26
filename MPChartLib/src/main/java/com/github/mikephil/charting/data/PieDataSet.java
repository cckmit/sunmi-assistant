
package com.github.mikephil.charting.data;

import android.util.Log;

import com.github.mikephil.charting.components.PieMarkerView;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PieDataSet extends DataSet<PieEntry> implements IPieDataSet {

    /**
     * the space in pixels between the chart-slices, default 0f
     */
    private float mSliceSpace = 0f;
    private boolean mAutomaticallyDisableSliceSpacing;

    /**
     * indicates the selection distance of a pie slice
     */
    private float mShift = 18f;
    private float mInnerShift = 0f;

    private ValuePosition mXValuePosition = ValuePosition.INSIDE_SLICE;
    private ValuePosition mYValuePosition = ValuePosition.INSIDE_SLICE;
    private boolean mUsingSliceColorAsValueLineColor = false;
    private boolean mUsingSliceColorAsHighlightShadowColor = true;
    private float mHighlightShadowRadius = 0;
    private float[] mHighlightShadowOffset = new float[2];
    private int mHighlightShadowColor = 0xff000000;
    private float mHighlightShadowColorAlpha = 1f;
    private int mValueLineColor = 0xff000000;
    private float mValueLineWidth = 1.0f;
    private boolean mValueLineStartDrawCircles = false;
    private boolean mValueLineStartDrawCircleHole = false;
    private float mValueLineStartCircleRadius = 8f;
    private float mValueLineStartCircleHoleRadius = 4f;
    private float mValueLinePart1OffsetPercentage = 75.f;
    private float mValueLinePart1Length = 0.3f;
    private float mValueLinePart2Length = 0.4f;
    private boolean mValueLineVariableLength = true;
    private boolean mValueLineAlignParent = false;
    private float mValueLinePart2Offset = 12;
    private float mDrawValuesAbove = 0f;
    private PieMarkerView mValueMarker;

    public PieDataSet(List<PieEntry> yVals, String label) {
        super(yVals, label);
//        mShift = Utils.convertDpToPixel(12f);
    }

    @Override
    public DataSet<PieEntry> copy() {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add(mValues.get(i).copy());
        }
        PieDataSet copied = new PieDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(PieDataSet pieDataSet) {
        super.copy(pieDataSet);
    }

    @Override
    protected void calcMinMax(PieEntry e) {

        if (e == null)
            return;

        calcMinMaxY(e);
    }

    /**
     * Sets the space that is left out between the piechart-slices in dp.
     * Default: 0 --> no space, maximum 20f
     *
     * @param spaceDp
     */
    public void setSliceSpace(float spaceDp) {

        if (spaceDp > 20)
            spaceDp = 20f;
        if (spaceDp < 0)
            spaceDp = 0f;

        mSliceSpace = Utils.convertDpToPixel(spaceDp);
    }

    @Override
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * When enabled, slice spacing will be 0.0 when the smallest value is going to be
     * smaller than the slice spacing itself.
     *
     * @param autoDisable
     */
    public void setAutomaticallyDisableSliceSpacing(boolean autoDisable) {
        mAutomaticallyDisableSliceSpacing = autoDisable;
    }

    /**
     * When enabled, slice spacing will be 0.0 when the smallest value is going to be
     * smaller than the slice spacing itself.
     *
     * @return
     */
    @Override
    public boolean isAutomaticallyDisableSliceSpacingEnabled() {
        return mAutomaticallyDisableSliceSpacing;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     *
     * @param shift
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    @Override
    public float getSelectionShift() {
        return mShift;
    }

    @Override
    public ValuePosition getXValuePosition() {
        return mXValuePosition;
    }

    public void setXValuePosition(ValuePosition xValuePosition) {
        this.mXValuePosition = xValuePosition;
    }

    @Override
    public ValuePosition getYValuePosition() {
        return mYValuePosition;
    }

    public void setYValuePosition(ValuePosition yValuePosition) {
        this.mYValuePosition = yValuePosition;
    }

    /**
     * When valuePosition is OutsideSlice, use slice colors as line color if true
     */
    @Override
    public boolean isUsingSliceColorAsValueLineColor() {
        return mUsingSliceColorAsValueLineColor;
    }

    public void setUsingSliceColorAsValueLineColor(boolean usingSliceColorAsValueLineColor) {
        this.mUsingSliceColorAsValueLineColor = usingSliceColorAsValueLineColor;
    }

    /**
     * When valuePosition is OutsideSlice, indicates line color
     */
    @Override
    public int getValueLineColor() {
        return mValueLineColor;
    }

    public void setValueLineColor(int valueLineColor) {
        this.mValueLineColor = valueLineColor;
    }

    /**
     * When valuePosition is OutsideSlice, indicates line width
     */
    @Override
    public float getValueLineWidth() {
        return mValueLineWidth;
    }

    public void setValueLineWidth(float valueLineWidth) {
        this.mValueLineWidth = valueLineWidth;
    }

    /**
     * When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size
     */
    @Override
    public float getValueLinePart1OffsetPercentage() {
        return mValueLinePart1OffsetPercentage;
    }

    public void setValueLinePart1OffsetPercentage(float valueLinePart1OffsetPercentage) {
        this.mValueLinePart1OffsetPercentage = valueLinePart1OffsetPercentage;
    }

    /**
     * When valuePosition is OutsideSlice, indicates length of first half of the line
     */
    @Override
    public float getValueLinePart1Length() {
        return mValueLinePart1Length;
    }

    public void setValueLinePart1Length(float valueLinePart1Length) {
        this.mValueLinePart1Length = valueLinePart1Length;
    }

    /**
     * When valuePosition is OutsideSlice, indicates length of second half of the line
     */
    @Override
    public float getValueLinePart2Length() {
        return mValueLinePart2Length;
    }

    public void setValueLinePart2Length(float valueLinePart2Length) {
        this.mValueLinePart2Length = valueLinePart2Length;
    }

    /**
     * When valuePosition is OutsideSlice, this allows variable line length
     */
    @Override
    public boolean isValueLineVariableLength() {
        return mValueLineVariableLength;
    }

    public void setValueLineVariableLength(boolean valueLineVariableLength) {
        this.mValueLineVariableLength = valueLineVariableLength;
    }

    @Override
    public boolean isValueLineStartDrawCircles() {
        return mValueLineStartDrawCircles;
    }

    public void setValueLineStartDrawCircles(boolean enabled) {
        this.mValueLineStartDrawCircles = enabled;
    }

    @Override
    public boolean isValueLineStartDrawCircleHole() {
        return mValueLineStartDrawCircleHole;
    }

    public void setValueLineStartDrawCircleHole(boolean enabled) {
        this.mValueLineStartDrawCircleHole = enabled;
    }

    @Override
    public float getValueLineStartCircleRadius() {
        return mValueLineStartCircleRadius;
    }

    public void setValueLineStartCircleRadius(float circleRadius) {
        if (circleRadius >= 0f) {
            mValueLineStartCircleRadius = Utils.convertDpToPixel(circleRadius);
        } else {
            Log.e("PieDataSet", "Circle radius cannot be above 0");
        }
    }

    @Override
    public float getValueLineStartCircleHoleRadius() {
        return mValueLineStartCircleHoleRadius;
    }

    public void setValueLineStartCircleHoleRadius(float circleHoleRadius) {
        if (circleHoleRadius >= 0f) {
            mValueLineStartCircleHoleRadius = Utils.convertDpToPixel(circleHoleRadius);
        } else {
            Log.e("PieDataSet", "Circle radius cannot be above 0");
        }
    }

    @Override
    public boolean isValueLineAlignParent() {
        return mValueLineAlignParent;
    }

    public void setValueLineAlignParent(boolean isAlign) {
        this.mValueLineAlignParent = isAlign;
    }

    @Override
    public float getValueLinePart2Offset() {
        return mValueLinePart2Offset;
    }

    public void setValueLinePart2Offset(int offset) {
        this.mValueLinePart2Offset = Utils.convertDpToPixel(offset);
    }

    @Override
    public PieMarkerView getValueMarker() {
        return mValueMarker;
    }

    public void setValueMarker(PieMarkerView marker) {
        this.mValueMarker = marker;
    }

    @Override
    public float getDrawValuesAbove() {
        return mDrawValuesAbove;
    }

    public void setDrawValuesAbove(float mDrawValuesAbove) {
        this.mDrawValuesAbove = mDrawValuesAbove;
    }

    @Override
    public float getSelectionInnerShift() {
        return mInnerShift;
    }

    public void setSelectionInnerShift(float shift) {
        mInnerShift = Utils.convertDpToPixel(shift);
    }

    @Override
    public boolean isUsingSliceColorAsHighlightShadowColor() {
        return mUsingSliceColorAsValueLineColor;
    }

    public void setUsingSliceColorAsHighlightShadowColor(boolean enable) {
        this.mUsingSliceColorAsValueLineColor = enable;
    }

    @Override
    public float getHighlightShadowRadius() {
        return mHighlightShadowRadius;
    }

    @Override
    public float[] getHighlightShadowOffset() {
        return mHighlightShadowOffset;
    }

    @Override
    public int getHighlightShadowColor() {
        return mHighlightShadowColor;
    }

    public void setHighlightShadow(float radius, float dx, float dy) {
        mHighlightShadowRadius = Utils.convertDpToPixel(radius);
        mHighlightShadowOffset[0] = Utils.convertDpToPixel(dx);
        mHighlightShadowOffset[1] = Utils.convertDpToPixel(dy);
    }

    public void setHighlightShadowColor(int color) {
        mHighlightShadowColor = color;
    }

    @Override
    public float getHighlightShadowColorAlpha() {
        return mHighlightShadowColorAlpha;
    }

    public void setHighlightShadowColorAlpha(float alpha) {
        this.mHighlightShadowColorAlpha = alpha;
    }

    public enum ValuePosition {
        INSIDE_SLICE,
        OUTSIDE_SLICE
    }
}
