package home.gabe.tvword.controllers;

public class TVWordException extends RuntimeException {
    public static final int EC_GENERAL_ERROR = 999;
    public static final int EC_CAMPAIGN_NOTACTIVE = 101;
    public static final int EC_INVALID_START = 102;
    public static final int EC_INVALID_EXPIRY = 103;
    public static final int EC_TEXT_MISSING = 104;
    public static final int EC_INVALID_COLORCODE = 105;
    public static final int EC_NAME_MISSING = 106;
    public static final int EC_DISPLAY_NOT_ENABLED = 107;
    public static final int EC_UNSUPPORTED_FILE = 108;
    private int errorCode;

    public TVWordException(int errorCode) {
        this.errorCode = errorCode;
    }

    public TVWordException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TVWordException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public TVWordException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TVWordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
