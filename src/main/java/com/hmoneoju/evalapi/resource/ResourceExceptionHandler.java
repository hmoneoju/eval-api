package com.hmoneoju.evalapi.resource;
import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.exception.RemoteOperationException;
import com.hmoneoju.evalapi.model.OperationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ParameterMissingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    Object handleParameterMissing(ParameterMissingException e) {
        OperationError error = new OperationError();
        error.setErrorCode(e.getErrorCode());
        error.setMessage(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(RemoteOperationException.class)
    @ResponseBody
    ResponseEntity handleRemoteOperationError(RemoteOperationException e) {
        OperationError error = new OperationError();
        error.setErrorCode(e.getErrorCode());
        error.setMessage(e.getMessage());

        ResponseEntity<OperationError> entity = new ResponseEntity(error, HttpStatus.valueOf(e.getRemoteHttpStatusCode()));
        return entity;
    }

}
