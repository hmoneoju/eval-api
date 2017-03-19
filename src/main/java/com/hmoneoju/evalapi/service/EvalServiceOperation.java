package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.request.HeadersExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Component("eval")
public class EvalServiceOperation implements RetryableServiceOperation {

    private static final Logger logger = LoggerFactory.getLogger(EvalServiceOperation.class);

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HeadersExtractor headersExtractor;

    @Autowired
    private CacheManager cacheManager;

    @Override
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

    public Operation callExternalService(String expression, HttpHeaders sourceHeaders) {
        HttpHeaders headers = headersExtractor.extract(sourceHeaders);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
        params.put("expression", Arrays.asList(expression));
        UriComponentsBuilder uriBuilder  = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getServiceUrl() );

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity, Operation.class).getBody();
    }

}
