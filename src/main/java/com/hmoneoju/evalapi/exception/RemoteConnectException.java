package com.hmoneoju.evalapi.exception;

public class RemoteConnectException extends EvalApiException {

    public static final int ERROR_CODE = 102;

    public RemoteConnectException(String message) {
        super(message);
        setErrorCode(ERROR_CODE);
    }

    public RemoteConnectException(String message, Throwable e) {
        super(message, e);
        setErrorCode(ERROR_CODE);
    }
}
