package com.hmoneoju.evalapi.exception;

public class EvalApiException extends RuntimeException {

    private int errorCode;

    public EvalApiException(String message) {
        super(message);
    }

    public EvalApiException(String message, Throwable e ) {
        super(message, e);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
