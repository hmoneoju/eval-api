package com.hmoneoju.evalapi.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Map;

public class ParamToMultiValueMapConverter {

    public static MultiValueMap<String, String> convert(Map<String, String[]> parameterMap ) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        parameterMap.keySet().stream()
                .forEach( t-> multiValueMap.put( t, Arrays.asList(parameterMap.get(t))) );
        return multiValueMap;
    }

}
