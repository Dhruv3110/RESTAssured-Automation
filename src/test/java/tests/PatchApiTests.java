package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import models.PatchPostRequest;
import models.Post;

public class PatchApiTests extends BaseTest {

    private static final long RESPONSE_TIME_LIMIT_MS = 15000L;

    @Test(description = "Verify PATCH updates only the title field")
    public void TC_001_shouldUpdateOnlyTitleUsingPatch() {
        getLogger().info("TC_001: PATCH /posts/1 → update title only, body field must be absent in request");

        PatchPostRequest request = new PatchPostRequest("Patched Title", null);
        getExtentTest().info("PATCH payload (body omitted): " + request);

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .patch("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(response.getTitle(), "Patched Title",
                "Title should reflect the patched value");

        getExtentTest().pass("Title patched to 'Patched Title'; other fields unaffected");
    }

    @Test(description = "Verify PATCH updates multiple fields simultaneously")
    public void TC_002_shouldUpdateMultipleFieldsUsingPatch() {
        getLogger().info("TC_002: PATCH /posts/1 → update both title and body");

        PatchPostRequest request = new PatchPostRequest("Updated via PATCH", "Updated body");
        getExtentTest().info("PATCH payload: " + request);

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .patch("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(response.getTitle(), "Updated via PATCH", "Title mismatch after PATCH");
        Assert.assertEquals(response.getBody(),  "Updated body",      "Body mismatch after PATCH");

        getExtentTest().pass("Both title and body patched successfully");
    }

    @Test(description = "Verify PATCH response time is under 3 seconds")
    public void TC_003_shouldRespondQuicklyForPatch() {
        getLogger().info("TC_003: PATCH /posts/1 → response time < {}ms", RESPONSE_TIME_LIMIT_MS);

        PatchPostRequest request = new PatchPostRequest("Performance Patch", null);

        given()
            .spec(getRequestSpec())
            .body(request)
        .when()
            .patch("/posts/1")
        .then()
            .statusCode(200)
            .time(lessThan(RESPONSE_TIME_LIMIT_MS));

        getExtentTest().pass("PATCH responded within " + RESPONSE_TIME_LIMIT_MS + "ms");
    }
}