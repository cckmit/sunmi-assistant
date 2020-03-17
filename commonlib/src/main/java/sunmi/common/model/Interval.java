package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * 区间Model类，多表示时间戳
 *
 * @author yinhui
 * @date 2019-12-24
 */
public class Interval implements Parcelable {

    public long start;
    public long end;

    public Interval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Interval interval = (Interval) o;
        return start == interval.start && end == interval.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @NonNull
    @Override
    public String toString() {
        return "Interval{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    protected Interval(Parcel in) {
        start = in.readLong();
        end = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start);
        dest.writeLong(end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Interval> CREATOR = new Creator<Interval>() {
        @Override
        public Interval createFromParcel(Parcel in) {
            return new Interval(in);
        }

        @Override
        public Interval[] newArray(int size) {
            return new Interval[size];
        }
    };

}
