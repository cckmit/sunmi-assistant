package com.sunmi.assistant.dashboard;

/**
 * @author yinhui
 * @date 2019-09-10
 */
public class Constants {

    public static final int PAGE_OVERVIEW = 10;
    public static final int PAGE_CUSTOMER = 11;

    public static final int FLAG_SHOP = 0x1;
    public static final int FLAG_SAAS = 0x2;
    public static final int FLAG_FS = 0x4;
    public static final int FLAG_CUSTOMER = 0x8;
    public static final int FLAG_ALL_MASK = 0xF;

    public static final int TIME_PERIOD_INIT = 0;
    public static final int TIME_PERIOD_TODAY = 1;
    public static final int TIME_PERIOD_WEEK = 2;
    public static final int TIME_PERIOD_MONTH = 3;
    public static final int TIME_PERIOD_YESTERDAY = 4;

    public static final int DATA_MODE_SALES = 0;
    public static final int DATA_MODE_ORDER = 1;

    public static final int DATA_TYPE_RATE = 0;
    public static final int DATA_TYPE_VOLUME = 1;
    public static final int DATA_TYPE_CUSTOMER = 2;

    public static final int DATA_TYPE_NEW_OLD = 10;
    public static final int DATA_TYPE_GENDER = 11;
    public static final int DATA_TYPE_AGE = 12;

    public static final int DATA_TYPE_ALL = 20;
    public static final int DATA_TYPE_NEW = 21;
    public static final int DATA_TYPE_OLD = 22;

    public static final int DATA_SOURCE_AUTH = 0x1;
    public static final int DATA_SOURCE_IMPORT = 0x2;
    public static final int DATA_SOURCE_FS = 0x4;
    public static final int DATA_SOURCE_CUSTOMER = 0x8;

    public static final int IMPORT_NONE = 0;
    public static final int IMPORT_DOING = 1;
    public static final int IMPORT_SUCCESS = 2;
    public static final int IMPORT_FAIL = 3;
    public static final int IMPORT_COMPLETE = -1;

    public static final int NO_CUSTOMER_DATA = 5087;
}
