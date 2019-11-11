package sunmi.common.exception;

/**
 * @author yinhui
 * @date 2019-11-11
 */
public class TimeDateException extends Exception {

    public static final int CODE_PERIOD_ERROR = 1;
    public static final int CODE_PARSE_ERROR = 2;

    private int code;
    private String detail;

    /**
     * Constructs a TimeDateException with the specified detail message.
     * Extra message will be saved in code & detail of members.
     *
     * @param s      the detail message
     * @param code   extra error code
     * @param detail extra error detail
     */
    public TimeDateException(String s, int code, String detail) {
        super(s);
        this.code = code;
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }
}
