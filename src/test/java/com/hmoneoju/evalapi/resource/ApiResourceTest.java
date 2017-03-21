package com.hmoneoju.evalapi.resource;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.model.OperationError;
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
public class ApiResourceTest {

    private static final String EXPRESSION = "2+2";
    private static final String RESULT = "4.0";
    private static final String EVALME_URL = "/eval";
    private static final String API_URL = "/api";
    private static final String EXPRESSION_PARAM_NAME = "expression";

    private static final int INVALID_EXPRESSION_ERROR_CODE = 1001;
    private static final String INVALID_EXPRESSION = "2+2)";
    private static final String INVALID_EXPRESSION_MESSAGE = "[%s] is not a valid expression";
    private static final String NO_EXPRESSION_VALUE = "";
    private static final int PARAMETER_MISSING_ERROR_CODE =101;
    private static final int REMOTE_CONNECT_ERROR_CODE =102;
    public static final int FIXED_DELAY = 1500;

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

    @Test
    public void invalidExpressionJSONResponse() {
        OperationError operationError = new OperationError();
        operationError.setErrorCode(INVALID_EXPRESSION_ERROR_CODE);
        operationError.setMessage(String.format(INVALID_EXPRESSION_MESSAGE, INVALID_EXPRESSION));

        Gson gson = new GsonBuilder().create();
        String expectedJSON = gson.toJson(operationError);

        assertInvalidExpressionResponse(expectedJSON, MediaType.APPLICATION_JSON, ContentType.JSON, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void invalidExpressionXMLResponse() throws JAXBException {
        OperationError operationError = new OperationError();
        operationError.setErrorCode(INVALID_EXPRESSION_ERROR_CODE);
        operationError.setMessage(String.format(INVALID_EXPRESSION_MESSAGE, INVALID_EXPRESSION));
        JAXBContext jaxbContext = JAXBContext.newInstance(OperationError.class);
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(operationError, writer);
        String expectedXML = writer.toString();

        assertInvalidExpressionResponse(expectedXML, MediaType.APPLICATION_XML, ContentType.XML, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void expressionMissingJSONResponse() {
        expressionMissing(
                PARAMETER_MISSING_ERROR_CODE,
                MediaType.APPLICATION_JSON,
                ContentType.JSON,
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @Test
    public void expressionMissingXMLResponse() {
        expressionMissing(
                PARAMETER_MISSING_ERROR_CODE,
                MediaType.APPLICATION_XML,
                ContentType.XML,
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @Test
    public void remoteConnectErrorJSONResponse(){
        assertTimeoutError(
                REMOTE_CONNECT_ERROR_CODE,
                MediaType.APPLICATION_JSON,
                ContentType.JSON,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
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

    private void assertInvalidExpressionResponse(String expectedResult, MediaType mediaType, ContentType contentType, int httpStatusCode) {
        stubFor(post(urlEqualTo(EVALME_URL))
            .withHeader(HttpHeaders.ACCEPT, equalTo(mediaType.toString()))
            .willReturn(aResponse()
                    .withStatus(httpStatusCode)
                    .withHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .withBody(expectedResult)));

        Response response =
            given()
                .formParam(EXPRESSION_PARAM_NAME,INVALID_EXPRESSION)
                .accept(mediaType.toString())
            .when()
                .post(API_URL)
            .then()
                .contentType(contentType)
                .statusCode(httpStatusCode)
            .extract().response();

        assertTrue(response.getBody().print().contains(expectedResult));
    }

    private void expressionMissing(int errorCode, MediaType mediaType, ContentType contentType, int httpStatusCode) {
        Response response =
            given()
                .formParam(EXPRESSION_PARAM_NAME, NO_EXPRESSION_VALUE)
                .accept(mediaType.toString())
            .when()
                .post(API_URL)
            .then()
                .contentType(contentType)
                .statusCode(httpStatusCode)
            .extract().response();

        assertTrue(response.getBody().print().contains(String.valueOf(errorCode)));
    }

    private void assertTimeoutError(int errorCode, MediaType mediaType, ContentType contentType, int httpStatusCode) {
        stubFor(post(urlEqualTo(EVALME_URL))
            .withHeader(HttpHeaders.ACCEPT, equalTo(mediaType.toString()))
            .willReturn(aResponse()
                .withFixedDelay(FIXED_DELAY))
        );

        Response response =
            given()
                .formParam(EXPRESSION_PARAM_NAME,EXPRESSION)
                .accept(mediaType.toString())
            .when()
                .post(API_URL)
            .then()
                .contentType(contentType)
                .statusCode(httpStatusCode)
            .extract().response();

        assertTrue(response.getBody().print().contains(String.valueOf(errorCode)));
    }

}
