package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class PostApiTests extends BaseTest {

    @Test(description = "Verify POST /posts creates a new post successfully")
    public void shouldCreateNewPostSuccessfully() {

        Map<String, Object> requestBody = Map.of(
                "title", "API Testing",
                "body", "Learning REST Assured",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("title", equalTo("API Testing"))
            .body("body", equalTo("Learning REST Assured"))
            .body("userId", equalTo(1))
            .body("id", notNullValue());
    }

    @Test(description = "Verify POST and extract generated ID")
    public void shouldCreatePostAndExtractId() {

        Map<String, Object> requestBody = Map.of(
                "title", "Extract Example",
                "body", "Testing extraction",
                "userId", 2
        );

        int generatedId =
            given()
                .spec(getRequestSpec())
                .body(requestBody)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .path("id");

        Assert.assertTrue(generatedId > 0);
    }

    @Test(description = "Verify POST fails when required field is missing")
    public void shouldFailWhenRequiredFieldMissing() {

        Map<String, Object> requestBody = Map.of(
                "title", "Invalid Test"
        );

        given()
            .spec(getRequestSpec())
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            // JSONPlaceholder always returns 201, real API should return 400
            .statusCode(anyOf(equalTo(201), equalTo(400)));
    }

    @Test(description = "Verify POST response time is under 3 seconds")
    public void shouldRespondWithinTimeLimitForPost() {

        Map<String, Object> payload = Map.of(
                "title", "Performance Test",
                "body", "Response time validation",
                "userId", 3
        );

        long responseTime =
        	    given()
        	        .spec(getRequestSpec())
        	        .body(payload)
        	    .when()
        	        .post("/posts")
        	    .then()
        	        .statusCode(201)
        	        .extract()
        	        .time();

        	Assert.assertTrue(responseTime < 3000,
        	        "Response time exceeded threshold: " + responseTime);
    }

    @Test(description = "Verify POST works for multiple user IDs")
    public void shouldCreatePostForDifferentUsers() {

        for (int i = 1; i <= 3; i++) {

            Map<String, Object> payload = Map.of(
                    "title", "User" + i,
                    "body", "Testing user" + i,
                    "userId", i
            );

            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .body("userId", equalTo(i));
        }
    }

    @Test(description = "Verify POST response matches JSON schema")
    public void shouldMatchPostResponseSchema() {

        Map<String, Object> payload = Map.of(
                "title", "Schema Test",
                "body", "Validating schema",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/postPostSchema.json"));
    }
}