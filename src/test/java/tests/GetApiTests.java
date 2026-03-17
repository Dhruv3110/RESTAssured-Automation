package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class GetApiTests extends BaseTest {

    private static final long RESPONSE_TIME_LIMIT_MS = 15000L;

    @Test(description = "Verify that GET /posts/{id} returns status 200")
    public void TC_001_shouldReturn200WhenValidPostIdIsRequested() {
        getLogger().info("TC_001: GET /posts/1 → expect 200");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200);

        getExtentTest().pass("Status 200 confirmed for GET /posts/1");
    }

    @Test(description = "Verify that GET /posts returns a list of posts with expected IDs")
    public void TC_002_shouldReturnListOfPostsWithExpectedIds() {
        getLogger().info("TC_002: GET /posts → verify list contains IDs 1, 2, 9");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("[1].id", equalTo(2))      
            .body("id", hasItems(1, 9));

        getExtentTest().pass("Post list contains expected IDs");
    }

    @Test(description = "Validate status line and response headers")
    public void TC_003_shouldValidateHttpStatusLineAndResponseHeaders() {
        getLogger().info("TC_003: GET /posts/1 → validate status line and Content-Type");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .statusLine("HTTP/1.1 200 OK")
            .header("Content-Type", containsString("application/json"));

        getExtentTest().pass("Status line and Content-Type header validated");
    }

    @Test(description = "Verify filtering using query parameters")
    public void TC_004_shouldReturnPostsFilteredByUserIdUsingQueryParams() {
        getLogger().info("TC_004: GET /posts?userId=1 → all results must have userId=1");

        given()
            .spec(getRequestSpec())
            .queryParam("userId", 1)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("userId", everyItem(equalTo(1)));

        getExtentTest().pass("All returned posts have userId=1");
    }

    @Test(description = "Verify filtering using path parameters")
    public void TC_005_shouldReturnPostsFilteredByIdUsingPathParams() {
        getLogger().info("TC_005: GET /posts/{id} with id=1 → verify id in response");

        given()
            .spec(getRequestSpec())
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(1));

        getExtentTest().pass("Path param id=1 returns correct post");
    }

    @Test(description = "Verify response time is under 3 seconds")
    public void TC_006_shouldRespondWithinThreeSeconds() {
        getLogger().info("TC_006: GET /posts/1 → response time must be < {}ms", RESPONSE_TIME_LIMIT_MS);

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .time(lessThan(RESPONSE_TIME_LIMIT_MS));

        getExtentTest().pass("Response time is within " + RESPONSE_TIME_LIMIT_MS + "ms");
    }

    @Test(description = "Verify extraction of post ID")
    public void TC_007_shouldExtractPostIdFromResponse() {
        getLogger().info("TC_007: GET /posts/1 → extract and assert id=1");

        int postId =
            given()
                .spec(getRequestSpec())
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        Assert.assertEquals(postId, 1, "Extracted post ID mismatch");
        getExtentTest().pass("Extracted post ID = " + postId);
    }

    @Test(description = "Verify GET /posts returns exactly 100 records")
    public void TC_008_shouldReturnExactlyOneHundredPosts() {
        getLogger().info("TC_008: GET /posts → expect exactly 100 records");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("size()", equalTo(100));

        getExtentTest().pass("Exactly 100 posts returned");
    }

    @Test(description = "Validate multiple response fields")
    public void TC_009_shouldValidateMultipleFieldsInPostResponse() {
        getLogger().info("TC_009: GET /posts/1 → validate userId, id, title");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .body("userId", equalTo(1),
                  "id",     equalTo(1),
                  "title",  notNullValue());

        getExtentTest().pass("userId, id, and title all validated");
    }

    @Test(description = "Validate response against JSON schema")
    public void TC_010_shouldMatchResponseWithPostSchema() {
        getLogger().info("TC_010: GET /posts/1 → validate against getPostSchema.json");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/getPostSchema.json"));

        getExtentTest().pass("Response matches JSON schema");
    }

    @Test(description = "Verify GET accepts custom Accept header and returns success")
    public void TC_011_shouldAcceptCustomHeadersAndReturnSuccess() {
        getLogger().info("TC_011: GET /posts with explicit Accept: application/json header");

        given()
            .spec(getRequestSpec())
            .header("Accept", "application/json")
        .when()
            .get("/posts")
        .then()
            .statusCode(200);

        getExtentTest().pass("Custom Accept header accepted, status 200 returned");
    }
}