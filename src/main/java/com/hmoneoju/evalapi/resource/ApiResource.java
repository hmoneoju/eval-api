package com.hmoneoju.evalapi.resource;

import com.hmoneoju.evalapi.request.ParamToMultiValueMapConverter;
import com.hmoneoju.evalapi.request.RequestHeadersExtractor;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping (value = "/api")
public class ApiResource {

    @Value("${evalme.service.url}")
    private String evalMeServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestHeadersExtractor headersExtractor;

    @RequestMapping(value="/**", method = RequestMethod.POST )
    @ResponseBody
    public String forwardRequest(HttpServletRequest request) {
        HttpHeaders headers = headersExtractor.extract(request);
        MultiValueMap<String, String> params = ParamToMultiValueMapConverter.convert(request.getParameterMap());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(evalMeServiceUrl);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity, String.class);
        return responseEntity.getBody();
   }

}
