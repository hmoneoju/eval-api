package com.hmoneoju.evalapi.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HttpHeadersExtractorTest {

    public static final String MY_USER_AGENT = "myUserAgent";
    @Autowired
    private HttpHeadersExtractor extractor;

    @Test
    public void noHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders extractedHeaders = extractor.extract(headers);
        assertEquals(extractedHeaders.size(), 0);
    }

    @Test
    public void noHeadersNamesToForward() {
        ReflectionTestUtils.setField(extractor, "headerNamesToForward", null);
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders extractedHeaders = extractor.extract(headers);
        assertEquals(extractedHeaders.size(), 0);
    }

    @Test
    public void headersFiltered() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put( HttpHeaders.ACCEPT, Arrays.asList(MediaType.APPLICATION_JSON.toString()));
        httpHeaders.put( HttpHeaders.USER_AGENT, Arrays.asList(MY_USER_AGENT));
        HttpHeaders extractedHeaders = extractor.extract(httpHeaders);
        assertEquals(extractedHeaders.size(), 1);
        assertTrue(extractedHeaders.get(HttpHeaders.ACCEPT) != null);
    }

}
