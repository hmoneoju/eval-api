package com.hmoneoju.evalapi.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpClientErrorResponse implements ClientHttpResponse {

    private HttpClientErrorException clientError;

    public HttpClientErrorResponse(HttpClientErrorException clientError) {
        this.clientError = clientError;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return clientError.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return clientError.getStatusCode().value();
    }

    @Override
    public String getStatusText() throws IOException {
        return clientError.getStatusCode().getReasonPhrase();
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream( clientError.getResponseBodyAsString().getBytes() );
    }

    @Override
    public HttpHeaders getHeaders() {
        return clientError.getResponseHeaders();
    }

}
