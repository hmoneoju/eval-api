package com.hmoneoju.evalapi.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpStatusCodeErrorResponse implements ClientHttpResponse {

    private HttpStatusCodeException httpException;

    public HttpStatusCodeErrorResponse(HttpStatusCodeException httpException) {
        this.httpException = httpException;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return httpException.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return httpException.getStatusCode().value();
    }

    @Override
    public String getStatusText() throws IOException {
        return httpException.getStatusCode().getReasonPhrase();
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream( httpException.getResponseBodyAsString().getBytes() );
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpException.getResponseHeaders();
    }

}
