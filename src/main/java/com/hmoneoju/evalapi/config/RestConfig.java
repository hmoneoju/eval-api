package com.hmoneoju.evalapi.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Value("${http.client.maxConnections}")
    private int maxConnections;

    @Value("${http.client.soTimeout}")
    private int soTimeOut;

    @Bean
    public HttpClient httpClient() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setSocketTimeout(soTimeOut);

        RequestConfig requestConfig = builder.build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfig);
        clientBuilder.setMaxConnTotal(maxConnections);

        return clientBuilder.build();
    }

    @Bean
    public RestTemplate restTemplate(@Autowired HttpClient httpClient) {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

}
