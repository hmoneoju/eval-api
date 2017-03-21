package com.hmoneoju.evalapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceProperties {

    @Value("${expression.parameter.name}")
    private String expressionParamName;

    @Value("${expressions.cache.name}")
    private String cacheName;

    @Value("${retry.max.attempts}")
    private int maxAttempts;

    @Value("${evalme.service.urls}")
    private String serviceUrls;

    public String getExpressionParamName() {
        return expressionParamName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public String getServiceUrls() {
        return serviceUrls;
    }

}
