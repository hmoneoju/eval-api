package com.hmoneoju.evalapi.exception;

public class ParameterMissingException extends EvalApiException {

    public static final int ERROR_CODE = 101;

    public ParameterMissingException(String message) {
        super(message);
    }

    public ParameterMissingException(String message, Throwable e) {
        super(message, e);
    }
}
