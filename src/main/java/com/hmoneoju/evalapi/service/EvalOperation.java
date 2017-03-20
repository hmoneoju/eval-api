package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.exception.RemoteOperationException;
import com.hmoneoju.evalapi.http.HttpClientErrorResponse;
import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.model.OperationError;
import com.hmoneoju.evalapi.http.HttpHeadersExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

import static com.hmoneoju.evalapi.exception.RemoteOperationException.GENERIC_REMOTE_ERROR;

@Component
public class EvalOperation {

    private static final Logger logger = LoggerFactory.getLogger(EvalOperation.class);

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeadersExtractor httpHeadersExtractor;

    @Autowired
    private CacheManager cacheManager;

    public Operation execute(String expression, HttpHeaders headers) {
        if (StringUtils.isEmpty(expression) )
            throw new ParameterMissingException( serviceConfiguration.getExpressionParameterName() );

        Cache cache = cacheManager.getCache( serviceConfiguration.getCacheName() );
        Cache.ValueWrapper cachedExpression = cache.get(expression);
        Operation operation;
        if ( cachedExpression != null ) {
            operation = new Operation(expression, cachedExpression.get().toString());
        } else {
            operation = callExternalService(expression, headers);
            cache.put(operation.getExpression(), operation.getResult() );
        }
        return operation;
    }

    private Operation callExternalService(String expression, HttpHeaders sourceHeaders) {
        HttpHeaders headers = httpHeadersExtractor.extract(sourceHeaders);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
        params.put(serviceConfiguration.getExpressionParameterName(), Arrays.asList(expression));
        UriComponentsBuilder uriBuilder  = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getServiceUrl() );

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, headers);

        ResponseEntity responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity, Operation.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error caught calling external service", e);
            return handleHttpClientException(e);
        }

        return (Operation) responseEntity.getBody();
    }

    private Operation handleHttpClientException(HttpClientErrorException clientError) {
        if ( StringUtils.isEmpty( clientError.getResponseBodyAsString()) )
            throw new RemoteOperationException(GENERIC_REMOTE_ERROR, clientError.getRawStatusCode());

        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor<>(
                OperationError.class,
                restTemplate.getMessageConverters()
        );

        ClientHttpResponse clientResponse = new HttpClientErrorResponse(clientError);
        RemoteOperationException remoteOperationException;
        try {
            OperationError operationError = (OperationError) responseExtractor.extractData(clientResponse);
            remoteOperationException = new RemoteOperationException(clientError.getRawStatusCode(), operationError);
        } catch (IOException e) {
            logger.warn("Unknown client response body ", e);
            remoteOperationException = new RemoteOperationException(GENERIC_REMOTE_ERROR, clientError.getRawStatusCode());
        }

        throw remoteOperationException;
    }

}
