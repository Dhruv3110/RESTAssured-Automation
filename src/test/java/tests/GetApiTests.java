package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class GetApiTests extends BaseTest {

    @Test(description = "Verify that GET /posts/{id} returns status 200")
    public void TC_001_shouldReturn200WhenValidPostIdIsRequested() {
//method chaining
        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200);
    }

    @Test(description = "Verify that GET /posts returns a list of posts with expected IDs")
    public void TC_002_shouldReturnListOfPostsWithExpectedIds() {

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("[1].id", equalTo(2))
            .body("id", hasItems(1, 9));
    }

    @Test(description = "Validate status line and response headers")
    public void TC_003_shouldValidateHttpStatusLineAndResponseHeaders() {

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .statusLine("HTTP/1.1 200 OK")
            .header("Content-Type", containsString("application/json"));
    }

    @Test(description = "Verify filtering using query parameters")
    public void TC_004_shouldReturnPostsFilteredByUserIdUsingQueryParams() {

        given()
            .spec(getRequestSpec())
            .queryParam("userId", 1)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("userId", everyItem(equalTo(1)));
    }

    @Test(description = "Verify filtering using path parameters")
    public void TC_005_shouldReturnPostsFilteredByIdUsingPathParams() {

        given()
            .spec(getRequestSpec())
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(1));
    }

    @Test(description = "Verify response time is under 3 seconds")
    public void TC_006_shouldRespondWithinThreeSeconds() {

    	long responseTime =
    		    given()
    		        .spec(getRequestSpec())
    		    .when()
    		        .get("/posts/1")
    		    .then()
    		        .statusCode(200)
    		        .extract()
    		        .time();

    		Assert.assertTrue(responseTime < 3000,
    		        "Response time exceeded threshold: " + responseTime);
    }

    @Test(description = "Verify extraction of post ID")
    public void TC_007_shouldExtractPostIdFromResponse() {

        int postId =
            given()
                .spec(getRequestSpec())
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        Assert.assertEquals(postId, 1);
    }

    @Test(description = "Verify GET /posts returns exactly 100 records")
    public void TC_008_shouldReturnExactlyOneHundredPosts() {

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .body("size()", equalTo(100));
    }

    @Test(description = "Validate multiple response fields")
    public void TC_009_shouldValidateMultipleFieldsInPostResponse() {

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .body("userId", equalTo(1),
                  "id", equalTo(1),
                  "title", notNullValue());
    }

    @Test(description = "Validate response against JSON schema")
    public void TC_010_shouldMatchResponseWithPostSchema() {

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/getPostSchema.json"));
    }

    @Test(description = "Verify GET accepts custom headers")
    public void TC_011_shouldAcceptCustomHeadersAndReturnSuccess() {

        given()
            .spec(getRequestSpec())
            .header("Accept", "application/json")
        .when()
            .get("/posts")
        .then()
            .statusCode(200);
    }
}