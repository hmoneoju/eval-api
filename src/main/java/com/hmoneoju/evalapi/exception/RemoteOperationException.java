package com.hmoneoju.evalapi.exception;

import com.hmoneoju.evalapi.model.OperationError;

public class RemoteOperationException extends EvalApiException {

    public static final int UNKNOWN_REMOTE_ERROR_CODE = 100;
    public static final String GENERIC_REMOTE_ERROR = "Error returned when calling remote service";

    private int remoteHttpStatusCode;

    public RemoteOperationException(String message) {
        super(message);
        setErrorCode(UNKNOWN_REMOTE_ERROR_CODE);
    }

    public RemoteOperationException(String message, int remoteStatusCode ) {
        super(message);
        this.remoteHttpStatusCode = remoteStatusCode;
        setErrorCode(UNKNOWN_REMOTE_ERROR_CODE);
    }

    public RemoteOperationException(int remoteStatusCode, OperationError operationError) {
        this(operationError.getMessage());
        this.setErrorCode(operationError.getErrorCode());
        this.remoteHttpStatusCode = remoteStatusCode;
    }

    public int getRemoteHttpStatusCode() {
        return remoteHttpStatusCode;
    }

}
