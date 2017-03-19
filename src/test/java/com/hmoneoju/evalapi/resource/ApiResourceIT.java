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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiResourceIT {

    @Rule
    public WireMockRule evalMeServer = new WireMockRule(8090);

    @Test
    public void successJSONResponse() {
        Gson gson = new GsonBuilder().create();
        Operation operation = new Operation("2+2", "4.0");
        String resultJson = gson.toJson(operation);

        stubFor(post(urlEqualTo("/eval"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withBody(resultJson)));

        Response response =
            given()
                .formParam("expression","2+2")
                .accept(MediaType.APPLICATION_JSON.toString())
            .when()
                .post("/api")
            .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
            .extract().response();

        assertEquals( resultJson, response.getBody().print());
    }

}
