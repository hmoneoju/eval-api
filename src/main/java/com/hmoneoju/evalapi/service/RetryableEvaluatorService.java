package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.RemoteConnectException;
import com.hmoneoju.evalapi.model.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class RetryableEvaluatorService {

    private static final Logger logger = LoggerFactory.getLogger(RetryableEvaluatorService.class);
    private static final String SEPARATOR = ",";

    @Autowired
    private ServiceProperties properties;

    @Autowired
    private MathEvaluatorService mathEvaluatorService;

    private List<String> urls;

    @PostConstruct
    private void init() {
        urls = Arrays.asList(properties.getServiceUrls().split(SEPARATOR));
    }

    public Operation evaluate(String expression, HttpHeaders headers)  {
        for ( String url: urls ) {
            try {
                return evaluateInternal(expression, headers, mathEvaluatorService, url, 1);
            } catch (RemoteConnectException e ) {
                logger.warn(e.getMessage());
            }
        }
        logger.error("Retry exhausted");
        throw new RemoteConnectException("Could not connect to remote services ["+properties.getServiceUrls()+"]");
    }

    private Operation evaluateInternal(String expression, HttpHeaders headers,
                                       MathEvaluatorService evaluatorService,
                                       String url, int retryAttempt) {

        if ( retryAttempt > properties.getMaxAttempts() )
            throw new RemoteConnectException("Max retries reached for " +url);

        try {
            logger.info("Attempt {} for url {}", retryAttempt, url );
            return evaluatorService.evaluate(expression, headers, url);
        } catch (ResourceAccessException e) {
            logger.warn("Attempt failed", e);
            return evaluateInternal(expression, headers, evaluatorService, url, ++retryAttempt);
        }
    }
}
