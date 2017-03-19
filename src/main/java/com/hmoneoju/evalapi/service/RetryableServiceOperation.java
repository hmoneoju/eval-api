package com.hmoneoju.evalapi.service;

import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.net.SocketException;
import java.net.SocketTimeoutException;

public interface RetryableServiceOperation<T> {

    @Retryable(
            include = {SocketException.class, SocketTimeoutException.class} ,
            maxAttemptsExpression="#{${retry.max.attempts}}",
            backoff = @Backoff(delayExpression = "#{${retry.delay}}"))
    T execute(String expression, HttpHeaders httpHeaders);
}
