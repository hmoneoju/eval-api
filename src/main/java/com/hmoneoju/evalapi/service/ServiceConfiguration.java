package com.hmoneoju.evalapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfiguration {

    @Value("${evalme.service.url}")
    private String serviceUrl;

    @Value("${expression.parameter.name}")
    private String expressionParameterName;

    @Value("${expressions.cache.name}")
    private String cacheName;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getExpressionParameterName() {
        return expressionParameterName;
    }

    public String getCacheName() {
        return cacheName;
    }

}
