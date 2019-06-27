package com.sunmi.assistant.dashboard.ui;

import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.ArrayList;
import java.util.List;

public abstract class ChartDataChangeAnimation<T extends Entry, C extends ChartData<? extends IDataSet<T>>> {
    private static final String TAG = "ChartDataChangeAnim";
    private int duration;
    private long startTime;
    private int fps = 30;
    private Handler timerHandler;
    private Chart<C> chart;
    private List<T> oldData;
    private List<T> newData;
    private Interpolator interpolator;

    public ChartDataChangeAnimation(int duration, Chart<C> chart, List<T> oldData, List<T> newData) {
        this.duration = duration;
        this.chart = chart;
        this.oldData = new ArrayList<>(oldData);
        this.newData = new ArrayList<>(newData);
        interpolator = new DecelerateInterpolator();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void run(int fps) {
        this.fps = fps;
        run();
    }

    public void run() {
        startTime = System.currentTimeMillis();
        timerHandler = new Handler();
        Runner runner = new Runner();
        runner.run();
    }

    public abstract T newEntry(T entry, float newValue);

    private class Runner implements Runnable {
        @Override
        public void run() {
            float increment = (float) (System.currentTimeMillis() - startTime) / duration;
            increment = interpolator.getInterpolation(increment < 0f ? 0f : increment > 1f ? 1f : increment);
            int oldSize = oldData.size();
            int newSize = newData.size();
            List<T> newDataSet = new ArrayList<>(newSize);
            for (int i = 0; i < newSize; i++) {
                T entry = newData.get(i);
                float oldY = oldSize > i ? oldData.get(i).getY() : entry.getY();
                float newY = entry.getY();
                newDataSet.add(newEntry(entry, oldY + (newY - oldY) * increment));
            }
            DataSet<T> dataSet = (DataSet<T>) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(newDataSet);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
            if (increment < 1f) {
                timerHandler.postDelayed(this, 1000 / fps);
            }
        }
    }
}
