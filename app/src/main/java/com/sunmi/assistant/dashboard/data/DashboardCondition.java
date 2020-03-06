package com.sunmi.assistant.dashboard.data;

import java.util.Objects;

/**
 * @author yinhui
 * @date 2020-03-05
 */
public class DashboardCondition {
    public boolean hasSaas;
    public boolean hasImport;
    public boolean hasFs;
    public boolean hasCustomer;
    public boolean isFloatingShow;

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
                isFloatingShow == that.isFloatingShow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasSaas, hasImport, hasFs, hasCustomer, isFloatingShow);
    }

    @Override
    public String toString() {
        return "DashboardCondition{" +
                "hasSaas=" + hasSaas +
                ", hasImport=" + hasImport +
                ", hasFs=" + hasFs +
                ", hasCustomer=" + hasCustomer +
                ", isFloatingShow=" + isFloatingShow +
                '}';
    }
}
