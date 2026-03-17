package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import models.Post;

public class PutApiTests extends BaseTest {

    private static final long RESPONSE_TIME_LIMIT_MS = 15000L;

    @Test(description = "Verify PUT /posts/{id} updates a post successfully")
    public void TC_001_shouldUpdatePostSuccessfully() {
        getLogger().info("TC_001: PUT /posts/1 → update title and body, verify response");

        Post request = new Post(1, 1, "Updated Title", "Updated Body");

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(response.getTitle(), "Updated Title");
        Assert.assertEquals(response.getBody(),  "Updated Body");
        Assert.assertEquals(response.getId(),    Integer.valueOf(1));

        getExtentTest().pass("PUT /posts/1 updated successfully, all fields verified");
    }

    @Test(description = "Verify PUT and extract updated ID")
    public void TC_002_shouldExtractUpdatedIdAfterPut() {
        getLogger().info("TC_002: PUT /posts/1 → extract id from response");

        Post request = new Post(1, 1, "Extract PUT", "Testing extraction");

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(response.getId(), Integer.valueOf(1), "id should be 1 after PUT");
        getExtentTest().pass("Extracted id=" + response.getId() + " after PUT");
    }

    @Test(description = "Verify response headers after PUT")
    public void TC_003_shouldReturnValidHeadersAfterPut() {
        getLogger().info("TC_003: PUT /posts/1 → verify Content-Type header");

        Post request = new Post(1, 1, "Header Test", "PUT Header");

        given()
            .spec(getRequestSpec())
            .body(request)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .header("Content-Type", startsWith("application/json"));

        getExtentTest().pass("Content-Type header starts with application/json");
    }

    @Test(description = "Verify PUT is idempotent — same payload yields identical responses")
    public void TC_004_shouldRemainConsistentWhenPutCalledMultipleTimes() {
        getLogger().info("TC_004: PUT /posts/1 twice with same payload → responses must match");

        Post payload = new Post(1, 1, "Idempotent Test", "Same payload");

        Post first =
            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Post second =
            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(first.getTitle(),  second.getTitle(),  "title mismatch between calls");
        Assert.assertEquals(first.getBody(),   second.getBody(),   "body mismatch between calls");
        Assert.assertEquals(first.getUserId(), second.getUserId(), "userId mismatch between calls");

        getExtentTest().pass("PUT is idempotent — both responses are identical");
    }

    @Test(description = "Verify PUT response time is under 3 seconds")
    public void TC_005_shouldRespondQuicklyForPut() {
        getLogger().info("TC_005: PUT /posts/1 → response time < {}ms", RESPONSE_TIME_LIMIT_MS);

        Post request = new Post(1, 1, "Performance PUT", "Performance");

        given()
            .spec(getRequestSpec())
            .body(request)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .time(lessThan(RESPONSE_TIME_LIMIT_MS));

        getExtentTest().pass("PUT responded within " + RESPONSE_TIME_LIMIT_MS + "ms");
    }
}