package sunmi.common.constant.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 * Created by bruce on 2020/3/20.
 */
public interface ModelType {

    public final static int MODEL_W1 = 0;
    public final static int MODEL_W1S = 1;
    public final static int MODEL_SS = 2;
    public final static int MODEL_FS = 3;
    public final static int MODEL_PRINTER = 4;

    @IntDef({MODEL_W1, MODEL_W1S, MODEL_SS, MODEL_FS, MODEL_PRINTER})
    @Retention(RetentionPolicy.SOURCE)
//指定注解仅存在与源码中,不加入到 class 文件中
    @interface Type {
    }

    //    public final int type;

    // Describes when the annotation will be discarded
//    @Retention(RetentionPolicy.SOURCE)
//    // Enumerate valid values for this interface
//    @IntDef({MODEL_W1, MODEL_W1S, MODEL_SS, MODEL_FS, MODEL_PRINTER})
//    // Create an interface for validating int types
//    public @interface ModelTypeDef {
//    }
//
//    // Mark the argument as restricted to these enumerated types
//    public ModelType(@ModelType.ModelTypeDef int modelType) {
//        this.type = modelType;
//    }
//
//    // get data
//    @ModelType.ModelTypeDef
//    public int getType() {
//        return type;
//    }

}
