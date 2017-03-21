package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.exception.RemoteOperationException;
import com.hmoneoju.evalapi.http.HttpStatusCodeErrorResponse;
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
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

import static com.hmoneoju.evalapi.exception.RemoteOperationException.GENERIC_REMOTE_ERROR;

@Component("eval")
public class EvalmeService implements MathEvaluatorService {

    private static final Logger logger = LoggerFactory.getLogger(EvalmeService.class);

    @Autowired
    private ServiceProperties properties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeadersExtractor httpHeadersExtractor;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Operation evaluate(String expression, HttpHeaders headers, String url) {
        if (StringUtils.isEmpty(expression) )
            throw new ParameterMissingException("Mandatory parameter [" + properties.getExpressionParamName() + "] missing");

        Cache cache = cacheManager.getCache(properties.getCacheName());
        Cache.ValueWrapper cachedExpression = cache.get(expression);
        Operation operation;
        if ( cachedExpression != null ) {
            String result = cachedExpression.get().toString();
            operation = new Operation(expression, result);
            logger.info("Retrieving ["+expression+"="+result+"] from cache");
        } else {
            operation = callExternalService(expression, headers,url);
            cache.put(operation.getExpression(), operation.getResult() );
            logger.info("Writing ["+expression+"="+operation.getResult()+"] into cache");
        }
        return operation;
    }

    private Operation callExternalService(String expression, HttpHeaders sourceHeaders, String url) {
        HttpHeaders headers = httpHeadersExtractor.extract(sourceHeaders);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
        params.put(properties.getExpressionParamName(), Arrays.asList(expression));

        UriComponentsBuilder uriBuilder  = UriComponentsBuilder.fromHttpUrl(url);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, headers);

        ResponseEntity responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity, Operation.class);
            return (Operation) responseEntity.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("Error caught calling external service", e);
            RemoteOperationException ex = prepareRemoteError(e);
            throw ex;
        }
    }

    private RemoteOperationException prepareRemoteError(HttpStatusCodeException clientError) {
        if ( StringUtils.isEmpty( clientError.getResponseBodyAsString()) )
            throw new RemoteOperationException(GENERIC_REMOTE_ERROR, clientError.getRawStatusCode());

        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor<>(
                OperationError.class,
                restTemplate.getMessageConverters()
        );

        ClientHttpResponse clientResponse = new HttpStatusCodeErrorResponse(clientError);
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
