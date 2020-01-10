package com.sunmi.ipc.cash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.CashTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yinhui
 * @date 2020-01-07
 */
public class CashTagManager {

    /**
     * 其他
     */
    private static final int TAG_ID_OTHER = -1;
    /**
     * 飞单
     */
    private static final int TAG_ID_ORDER_MISMATCH = 2;
    /**
     * 钱箱未关
     */
    private static final int TAG_ID_CASH_EXPOSED = 3;
    /**
     * 偷钱
     */
    private static final int TAG_ID_CASH_STOLEN = 4;
    /**
     * 漏扫
     */
    private static final int TAG_ID_SCAN_MISSING = 5;
    /**
     * 偷换条码
     */
    private static final int TAG_ID_CODE_REPLACED = 6;
    /**
     * 交易类型不匹配
     */
    private static final int TAG_ID_TRANSACTION_MISMATCH = 7;

    private SparseArray<CashTag> tags = new SparseArray<>();
    private CashTag other;

    private boolean isInit = false;

    private CashTagManager() {
    }

    private static final class Holder {
        private static final CashTagManager INSTANCE = new CashTagManager();
    }

    public static CashTagManager get(Context context) {
        Holder.INSTANCE.init(context);
        return Holder.INSTANCE;
    }

    private void init(Context context) {
        if (isInit) {
            return;
        }
        other = new CashTag(TAG_ID_OTHER, context.getString(R.string.cash_video_tag_other), "");
        tags.put(TAG_ID_ORDER_MISMATCH, new CashTag(TAG_ID_ORDER_MISMATCH,
                context.getString(R.string.cash_video_tag_order_mismatch),
                context.getString(R.string.cash_video_tag_tip_order_mismatch)));
        tags.put(TAG_ID_CASH_EXPOSED, new CashTag(TAG_ID_CASH_EXPOSED,
                context.getString(R.string.cash_video_tag_cash_exposed),
                context.getString(R.string.cash_video_tag_tip_cash_exposed)));
        tags.put(TAG_ID_CASH_STOLEN, new CashTag(TAG_ID_CASH_STOLEN,
                context.getString(R.string.cash_video_tag_cash_stolen), ""));
        tags.put(TAG_ID_SCAN_MISSING, new CashTag(TAG_ID_SCAN_MISSING,
                context.getString(R.string.cash_video_tag_scan_missing), ""));
        tags.put(TAG_ID_CODE_REPLACED, new CashTag(TAG_ID_CODE_REPLACED,
                context.getString(R.string.cash_video_tag_code_replaced), ""));
        tags.put(TAG_ID_TRANSACTION_MISMATCH, new CashTag(TAG_ID_TRANSACTION_MISMATCH,
                context.getString(R.string.cash_video_tag_transaction_mismatch), ""));
        isInit = true;
    }

    @NonNull
    public CashTag getTag(int id) {
        CashTag tag = tags.get(id);
        if (tag == null) {
            tag = other;
        }
        return tag;
    }

    @NonNull
    public CashTag getTag(int[] ids) {
        if (ids == null || ids.length <= 0) {
            return other;
        } else {
            return getTag(ids[0]);
        }
    }

    public List<CashTag> getTags() {
        List<CashTag> list = new ArrayList<>(tags.size());
        for (int i = 0, size = tags.size(); i < size; i++) {
            list.add(tags.valueAt(i));
        }
        return list;
    }


}
