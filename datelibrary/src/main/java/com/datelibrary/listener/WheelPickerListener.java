package com.datelibrary.listener;


import com.datelibrary.bean.DateBean;
import com.datelibrary.bean.DateType;

/**
 * Created by codbking on 2016/9/22.
 */

public interface WheelPickerListener {
     void onSelect(DateType type, DateBean bean);
}
