package com.hmoneoju.evalapi.exception;

public class EvalApiException extends RuntimeException {

    public static final int ERROR_CODE = 100;

    private int errorCode;

    public EvalApiException(String message) {
        super(message);
        setErrorCode(ERROR_CODE);
    }

    public EvalApiException(String message, Throwable e ) {
        super(message, e);
        setErrorCode(ERROR_CODE);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
