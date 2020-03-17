package sunmi.common.constant.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 * Created by bruce on 2020/3/16.
 */
public class DeviceType {
    public static final String ROUTER = "ROUTER";
    public static final String IPC = "IPC";
    public static final String PRINTER = "PRINTER";
    public static final String POS = "POS";

    public final String type;

    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @StringDef({ROUTER, IPC, PRINTER, POS})
    // Create an interface for validating int types
    public @interface DeviceTypeDef {
    }

    // Mark the argument as restricted to these enumerated types
    public DeviceType(@DeviceTypeDef String filterColor) {
        this.type = filterColor;
    }

    // get data
    @DeviceTypeDef
    public String getType() {
        return type;
    }

}
