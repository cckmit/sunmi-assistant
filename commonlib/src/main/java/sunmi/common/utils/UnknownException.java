package sunmi.common.utils;

import android.content.Context;
import android.os.Looper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import sunmi.common.utils.log.LogHelper;

public class UnknownException implements UncaughtExceptionHandler {
    private Context mContext = null;
    private UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private static UnknownException instance;
    private boolean isSaveLog;

    private UnknownException() {
    }

    public static UnknownException getInstance() {
        if (instance == null) {
            synchronized (UnknownException.class) {
                if (instance == null) {
                    instance = new UnknownException();
                }
            }
        }
        return instance;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogHelper.exportLog(CommonHelper.getCName(new Exception()),
                        CommonHelper.getMName(new Exception()), "InterruptedException:" + e.getMessage(), isSaveLog);
            }
        }
    }

    /**
     * 捕获全局异常
     *
     * @param context 上下文
     */
    public void init(Context context, boolean isSaveLog) {
        this.mContext = context;
        this.isSaveLog = isSaveLog;

        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null)
            return true;

        String msg = throwable.getLocalizedMessage();
        if (msg == null)
            return false;
        ToastUtils.toastForShort(mContext, "Sorry,app generates unknown error,please retry few minutes later");
        LogHelper.exportLog(CommonHelper.getCName(new Exception()),
                CommonHelper.getMName(new Exception()), getErrorInfo(throwable), isSaveLog);
        return true;
    }

    private String getErrorInfo(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        String errorInfo = writer.toString();
        return errorInfo;
    }

}

