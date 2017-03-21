package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.exception.RemoteOperationException;
import com.hmoneoju.evalapi.model.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.ResourceAccessException;

public interface MathEvaluatorService {
    Operation evaluate(String expression, HttpHeaders headers, String url)
            throws ResourceAccessException, ParameterMissingException, RemoteOperationException;
}
