package com.hmoneoju.evalapi.exception;


import java.text.MessageFormat;

public class ParameterMissingException extends RuntimeException {

    private String paramater;

    public ParameterMissingException(String paramater) {
        super(MessageFormat.format("Mandator parameter %s missing", paramater));
    }
}
