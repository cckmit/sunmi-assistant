package com.sunmi.assistant.dashboard.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * @author yinhui
 * @date 2020-03-05
 */
public class DashboardCondition implements Parcelable {
    public boolean hasSaas;
    public boolean hasImport;
    public boolean hasFs;
    public boolean hasCustomer;
    public boolean hasFloating;

    public DashboardCondition() {
    }

    public DashboardCondition(boolean hasSaas, boolean hasImport, boolean hasFs, boolean hasCustomer, boolean hasFloating) {
        this.hasSaas = hasSaas;
        this.hasImport = hasImport;
        this.hasFs = hasFs;
        this.hasCustomer = hasCustomer;
        this.hasFloating = hasFloating;
    }

    public DashboardCondition copy() {
        return new DashboardCondition(hasSaas, hasImport, hasFs, hasCustomer, hasFloating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashboardCondition that = (DashboardCondition) o;
        return hasSaas == that.hasSaas &&
                hasImport == that.hasImport &&
                hasFs == that.hasFs &&
                hasCustomer == that.hasCustomer &&
                hasFloating == that.hasFloating;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasSaas, hasImport, hasFs, hasCustomer, hasFloating);
    }

    @NonNull
    @Override
    public String toString() {
        return "DashboardCondition{" +
                "hasSaas=" + hasSaas +
                ", hasImport=" + hasImport +
                ", hasFs=" + hasFs +
                ", hasCustomer=" + hasCustomer +
                ", hasFloating=" + hasFloating +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.hasSaas ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasImport ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasFs ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasCustomer ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasFloating ? (byte) 1 : (byte) 0);
    }

    protected DashboardCondition(Parcel in) {
        this.hasSaas = in.readByte() != 0;
        this.hasImport = in.readByte() != 0;
        this.hasFs = in.readByte() != 0;
        this.hasCustomer = in.readByte() != 0;
        this.hasFloating = in.readByte() != 0;
    }

    public static final Creator<DashboardCondition> CREATOR = new Creator<DashboardCondition>() {
        @Override
        public DashboardCondition createFromParcel(Parcel source) {
            return new DashboardCondition(source);
        }

        @Override
        public DashboardCondition[] newArray(int size) {
            return new DashboardCondition[size];
        }
    };
}
