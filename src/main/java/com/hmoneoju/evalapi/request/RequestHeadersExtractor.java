package com.hmoneoju.evalapi.request;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RequestHeadersExtractor {

    private  static final String HEADERS_SEPARATOR = ",";

    @Value("${request.heades.filter}")
    private String headerNamesToFilter;

    private List<String> headersToFilter;

    @PostConstruct
    public void init() {
        headersToFilter = Arrays.asList(headerNamesToFilter.split(HEADERS_SEPARATOR));
    }

    public HttpHeaders extract(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        headerNames.stream().
                filter( t-> !headersToFilter.contains(t) ).
                forEach( t -> headers.put( t,  Collections.list(request.getHeaders(t) ) ));
        return headers;
    }

}
