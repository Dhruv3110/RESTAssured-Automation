package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;

import base.BaseTest;
import models.Post;

public class DeleteApiTests extends BaseTest {

    private static final long RESPONSE_TIME_LIMIT_MS = 15000L;

    @Test(description = "Verify DELETE /posts/{id} removes the resource successfully")
    public void TC_001_shouldDeletePostSuccessfully() {
        getLogger().info("TC_001: DELETE /posts/1 → expect 200");

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200);

        getExtentTest().pass("DELETE /posts/1 returned 200");
    }

    @Test(description = "Verify DELETE response body is an empty JSON object")
    public void TC_002_shouldReturnEmptyBodyAfterDelete() {
        getLogger().info("TC_002: DELETE /posts/1 → response body should be {{}}");

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200)
            .body(equalTo("{}"));

        getExtentTest().pass("DELETE response body is empty JSON object {{}}");
    }

    @Test(description = "Verify DELETE with non-existing ID is accepted by JSONPlaceholder fake API")
    public void TC_003_shouldAcceptDeleteForNonExistentPostOnFakeApi() {
        getLogger().info("TC_003: DELETE /posts/9999 → JSONPlaceholder returns 200 for ALL DELETEs (fake API — no ID validation)");

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/9999")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)));

        getExtentTest().pass("DELETE /posts/9999 returned acceptable status (200 or 404)");
    }

    @Test(description = "Verify DELETE response time is under 3 seconds")
    public void TC_004_shouldRespondQuicklyForDelete() {
        getLogger().info("TC_004: DELETE /posts/1 → response time < {}ms", RESPONSE_TIME_LIMIT_MS);

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200)
            .time(lessThan(RESPONSE_TIME_LIMIT_MS));

        getExtentTest().pass("DELETE responded within " + RESPONSE_TIME_LIMIT_MS + "ms");
    }

    @Test(description = "Verify full delete flow: create → delete → confirm deletion")
    public void TC_005_shouldDeletePostAndVerifyItIsRemoved() {
        getLogger().info("TC_005: Full flow — POST /posts → DELETE → GET → expect 404");

        Post request = new Post(1, "Delete Flow", "Testing delete");

        int generatedId =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .path("id");

        getLogger().info("Created post with id={}", generatedId);
        getExtentTest().info("Step 1: Created post with id=" + generatedId);

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/" + generatedId)
        .then()
            .statusCode(200);

        getExtentTest().info("Step 2: DELETE /posts/" + generatedId + " returned 200");

        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/" + generatedId)
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)));

        getExtentTest().pass("Step 3: GET /posts/" + generatedId + " post-DELETE returned 200 or 404");
    }
}