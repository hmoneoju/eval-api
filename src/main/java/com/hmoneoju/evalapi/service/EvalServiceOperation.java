package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.ParameterMissingException;
import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.request.ParamToMultiValueMapConverter;
import com.hmoneoju.evalapi.request.RequestHeadersExtractor;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component("eval")
public class EvalServiceOperation implements RetryableServiceOperation {

    private static final Logger logger = LoggerFactory.getLogger(EvalServiceOperation.class);

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestHeadersExtractor headersExtractor;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Operation execute(HttpServletRequest request) {
        String expression = request.getParameter( serviceConfiguration.getExpressionParameterName() );
        if (StringUtils.isEmpty(expression) )
            throw new ParameterMissingException( serviceConfiguration.getExpressionParameterName() );

        Cache cache = cacheManager.getCache( serviceConfiguration.getCacheName() );
        Cache.ValueWrapper cachedExpression = cache.get(expression);
        Operation operation;
        if ( cachedExpression != null ) {
            operation = new Operation(expression, cachedExpression.get().toString());
        } else {
            operation = callExternalService(request);
            cache.put(operation.getExpression(), operation.getResult() );
        }
        return operation;
    }

    public Operation callExternalService(HttpServletRequest request) {
        HttpHeaders headers = headersExtractor.extract(request);
        MultiValueMap<String, String> params = ParamToMultiValueMapConverter.convert(request.getParameterMap());

        UriComponentsBuilder uriBuilder  = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getServiceUrl() );

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity, Operation.class).getBody();
    }

}
