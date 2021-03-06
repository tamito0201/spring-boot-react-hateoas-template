package com.example.demo.rest;

import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.steps.LoginSteps;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "db-test", "db-init"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorHandlingRestTest {

    @LocalServerPort
    int port;

    @Inject private LoginSteps loginSteps;
    @Inject private RestProvider restProvider;

    @Before
    public void setup() throws Exception {
        restProvider.init("http://localhost", port);
        loginSteps.login("bob", "password");
    }

    @Test
    public void should_be_not_found() {
        Response response =
                given()
                        .header(JwtTokenProvider.AUTH_HEADER, loginSteps.authToken())
                        .get("/api/1/persons/{key}", "unknown");

        System.out.println(response.body().asString());

        response
                .then()
                .body("apierror.status", equalTo("NOT_FOUND"))
                .and()
                .statusCode(404);
    }
}
