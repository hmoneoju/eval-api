package com.hmoneoju.evalapi.resource;
import com.hmoneoju.evalapi.exception.EvalApiException;
import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.exception.RemoteConnectException;
import com.hmoneoju.evalapi.exception.RemoteOperationException;
import com.hmoneoju.evalapi.model.OperationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ResourceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceExceptionHandler.class);

    @ExceptionHandler(ParameterMissingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    Object handleParameterMissing(ParameterMissingException e) {
        OperationError error = buildOperationError(e);
        return error;
    }

    @ExceptionHandler(RemoteOperationException.class)
    @ResponseBody
    ResponseEntity handleRemoteOperationError(RemoteOperationException e) {
        OperationError error = buildOperationError(e);
        ResponseEntity<OperationError> entity = new ResponseEntity(error, HttpStatus.valueOf(e.getRemoteHttpStatusCode()));
        return entity;
    }

    @ExceptionHandler(RemoteConnectException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    Object handleRemoteConnectionException(RemoteConnectException e) {
        OperationError error = buildOperationError(e);
        return error;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    Object handleUncaughtException(Throwable e) {
        EvalApiException evalApiException = new EvalApiException("Unknown error", e);
        OperationError error = buildOperationError(evalApiException);
        return error;
    }

    private OperationError buildOperationError(EvalApiException e) {
        logger.error("Error thrown", e);

        OperationError operationError = new OperationError();
        operationError.setErrorCode(e.getErrorCode());
        operationError.setMessage(e.getMessage());

        return operationError;
    }

}
