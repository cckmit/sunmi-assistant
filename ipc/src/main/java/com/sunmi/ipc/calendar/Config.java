package com.sunmi.ipc.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author yinhui
 * @date 2019-11-25
 */
public class Config {

    private Calendar minDate;
    private Calendar maxDate;
    private List<Calendar> points = new ArrayList<>();

    Config() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.set(year, month, date);
        maxDate = (Calendar) c.clone();
        c.add(Calendar.MONTH, -3);
        minDate = (Calendar) c.clone();
    }

    public Calendar getMinDate() {
        return minDate;
    }

    public Calendar getMaxDate() {
        return maxDate;
    }

    public List<Calendar> getPoints() {
        return points;
    }

    public static class Builder {
        private Config config;

        public Builder() {
            this.config = new Config();
        }

        public Builder setMinDate(Calendar min) {
            config.minDate = min;
            return this;
        }

        public Builder setMaxDate(Calendar max) {
            config.maxDate = max;
            return this;
        }

        public Builder setPoint(List<Calendar> points) {
            config.points = points;
            return this;
        }

        public Config build() {
            return config;
        }

    }
}
