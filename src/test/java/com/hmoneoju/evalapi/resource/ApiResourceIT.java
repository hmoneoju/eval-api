package com.hmoneoju.evalapi.resource;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hmoneoju.evalapi.model.Operation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import java.io.StringWriter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiResourceIT {

    private static final String EXPRESSION = "2+2";
    private static final String RESULT = "4.0";
    private static final String EVALME_URL = "/eval";
    private static final String API_URL = "/api";
    private static final String EXPRESSION_PARAM_NAME = "expression";

    @Rule
    public WireMockRule evalMeServer = new WireMockRule(8090);

    @Test
    public void successJSONResponse() {
        Gson gson = new GsonBuilder().create();
        Operation operation = new Operation(EXPRESSION, RESULT);
        String expectedJSON = gson.toJson(operation);

        assertSuccess(expectedJSON, MediaType.APPLICATION_JSON, ContentType.JSON);
    }

    @Test
    public void successXMLResponse() throws JAXBException {
        Operation operation = new Operation(EXPRESSION, RESULT);
        JAXBContext jaxbContext = JAXBContext.newInstance(Operation.class);
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(operation, writer);
        String expectedXML = writer.toString();

        assertSuccess(expectedXML, MediaType.APPLICATION_XML, ContentType.XML);
    }

    private void assertSuccess(String expectedResult, MediaType mediaType, ContentType contentType) {
        stubFor(post(urlEqualTo(EVALME_URL))
            .withHeader(HttpHeaders.ACCEPT, equalTo(mediaType.toString()))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .withBody(expectedResult)));

        Response response =
            given()
                .formParam(EXPRESSION_PARAM_NAME,EXPRESSION)
                .accept(mediaType.toString())
            .when()
                .post(API_URL)
            .then()
                .contentType(contentType)
                .statusCode(HttpStatus.OK.value())
            .extract().response();

        assertEquals( expectedResult, response.getBody().print());
    }

}
