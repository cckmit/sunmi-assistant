package sunmi.common.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 时间区间Model类
 *
 * @author yinhui
 * @date 2019-12-24
 */
public class Interval<T> {

    public T start;
    public T end;

    public Interval(T start, T end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interval)) {
            return false;
        }
        Interval<?> i = (Interval<?>) o;
        return Objects.equals(start, i.start) && Objects.equals(end, i.end);
    }

    @Override
    public int hashCode() {
        return (start == null ? 0 : start.hashCode() << 16) ^ (end == null ? 0 : end.hashCode());
    }

    @NotNull
    @Override
    public String toString() {
        return "Interval{start=" + start + ", end=" + end + '}';
    }
}
