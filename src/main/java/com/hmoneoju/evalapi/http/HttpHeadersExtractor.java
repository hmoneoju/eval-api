package com.hmoneoju.evalapi.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class HttpHeadersExtractor {

    private  static final String HEADERS_SEPARATOR = ",";

    @Value("${request.heades.forward}")
    private String headerNamesToForward;

    private List<String> headersToForward;

    @PostConstruct
    private void init() {
        headersToForward = Arrays.asList(headerNamesToForward.split(HEADERS_SEPARATOR));
        headersToForward.forEach(String::toLowerCase);
    }

    public HttpHeaders extract(HttpHeaders sourceHeaders) {
        HttpHeaders headers = new HttpHeaders();
        sourceHeaders.keySet().stream().
                filter( t -> headersToForward.contains(t.toLowerCase()) ).
                forEach( t -> headers.put( t, sourceHeaders.get(t)));
        return headers;
    }

}
