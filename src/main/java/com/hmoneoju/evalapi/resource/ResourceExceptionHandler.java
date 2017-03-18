package com.hmoneoju.evalapi.resource;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ParameterMissingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody Object handleParameterMissing(ParameterMissingException e) {
        return e.getMessage();
    }

}
