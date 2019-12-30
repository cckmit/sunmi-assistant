package com.sunmi.ipc.utils;

import android.support.annotation.ArrayRes;
import android.util.SparseArray;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.CashTag;

import sunmi.common.base.BaseApplication;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-27.
 */
public class CashAbnormalTagUtils {

    private SparseArray<CashTag> cashTags = new SparseArray<>();


    private CashAbnormalTagUtils() {
        String[] tags = getStrArray(R.array.video_tag);
        String[] descriptions = getStrArray(R.array.video_description);
        String[] tips = getStrArray(R.array.video_tip);
        for (int i = 0; i < tips.length; i++) {
            int tag = Integer.parseInt(tags[i]);
            CashTag cashTag = new CashTag();
            cashTag.setTag(tag);
            cashTag.setDescription(descriptions[i]);
            cashTag.setTip(tips[i]);
            cashTags.put(tag, cashTag);
        }
        for (int i = tips.length; i < tags.length; i++) {
            int tag = Integer.parseInt(tags[i]);
            CashTag cashTag = new CashTag();
            cashTag.setTag(tag);
            cashTag.setDescription(descriptions[i]);
            cashTag.setTip("");
            cashTags.put(tag, cashTag);
        }
    }

    private static final class Singleton {
        private static final CashAbnormalTagUtils INSTANCE = new CashAbnormalTagUtils();
    }

    public CashAbnormalTagUtils getInstance() {
        return Singleton.INSTANCE;
    }

    public CashTag getCashTag(int tag) {
        return cashTags.get(tag);
    }

    
    private String[] getStrArray(@ArrayRes int id) {
        return BaseApplication.getInstance().getResources().getStringArray(id);
    }

}
