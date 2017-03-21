package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.RemoteConnectException;
import com.hmoneoju.evalapi.model.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RetryableEvaluatorServiceTest {

    public static final String URL1 = "http://localhost:8090/eval/api";
    public static final String URL2 = "http://localhost:9000/eval/api";
    public static final String EXPRESSION_TO_EVALUATE = "2+2";
    public static final String URLS_FIELD_ATTR_NAME = "urls";
    @Mock
    private ServiceProperties properties;

    @Mock
    private MathEvaluatorService evaluatorService;

    @InjectMocks
    private RetryableEvaluatorService retryableService;

    @Test
    public void noNeedToRetry() {
        prepareForSingleURL();
        when(evaluatorService.evaluate(any(), anyObject(), eq(URL1))).thenReturn( new Operation());
        retryableService.evaluate(EXPRESSION_TO_EVALUATE, new HttpHeaders());
        verify(evaluatorService, times(1)).evaluate(any(), anyObject(), any());
    }

    @Test
    public void oneServiceURLRetriedNAttempts() {
        prepareForSingleURL();
        when(evaluatorService.evaluate(any(), anyObject(), eq(URL1))).thenThrow( new ResourceAccessException("ResourceAccessError"));
        try {
            retryableService.evaluate(EXPRESSION_TO_EVALUATE, new HttpHeaders());
        } catch (RemoteConnectException e ) {
            verify(evaluatorService, times(3)).evaluate(any(), anyObject(), any());
        }
    }

    @Test
    public void multipleServiceURLRetryExhausted() {
        prepareForMultipleURLs();
        ResourceAccessException ex = new ResourceAccessException("ResourceAccessError");
        when(evaluatorService.evaluate(any(), anyObject(), eq(URL1))).thenThrow(ex);
        try {
            retryableService.evaluate(EXPRESSION_TO_EVALUATE, new HttpHeaders());
        } catch (RemoteConnectException e ) {
            verify(evaluatorService, times(6)).evaluate(any(), anyObject(), any());
        }
    }

    private void prepareForSingleURL() {
        when(properties.getServiceUrls()).thenReturn(URL1);
        when(properties.getMaxAttempts()).thenReturn(3);
        List<String> urls = Arrays.asList(URL1);
        Field field = ReflectionUtils.findField(RetryableEvaluatorService.class, URLS_FIELD_ATTR_NAME);
        field.setAccessible(true);
        ReflectionUtils.setField(field, retryableService, urls);
    }

    private void prepareForMultipleURLs() {
        when(properties.getServiceUrls()).thenReturn(URL1+","+URL2);
        when(properties.getMaxAttempts()).thenReturn(3);

        List<String> urls = Arrays.asList(URL1,URL2);
        Field field = ReflectionUtils.findField(RetryableEvaluatorService.class, URLS_FIELD_ATTR_NAME);
        field.setAccessible(true);
        ReflectionUtils.setField(field, retryableService, urls);
    }

}
