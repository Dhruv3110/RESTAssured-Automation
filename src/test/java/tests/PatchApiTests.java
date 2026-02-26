package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class PatchApiTests extends BaseTest {

    @Test(description = "Verify PATCH updates only specified field")
    public void shouldUpdateOnlyTitleUsingPatch() {

        Map<String, Object> payload = Map.of(
                "title", "Patched Title"
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .patch("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Patched Title"));
    }

    @Test(description = "Verify PATCH updates multiple fields")
    public void shouldUpdateMultipleFieldsUsingPatch() {

        Map<String, Object> payload = Map.of(
                "title", "Updated via PATCH",
                "body", "Updated body"
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .patch("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Updated via PATCH"))
            .body("body", equalTo("Updated body"));
    }

    @Test(description = "Verify PATCH handles invalid data types")
    public void shouldHandleInvalidDataTypeInPatch() {

        Map<String, Object> payload = Map.of(
                "title", 12345
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .patch("/posts/1")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    @Test(description = "Verify PATCH with invalid ID")
    public void shouldHandlePatchWithInvalidId() {

        Map<String, Object> payload = Map.of(
                "title", "Invalid ID Test"
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .patch("/posts/9999")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)));
    }

    @Test(description = "Verify PATCH and extract updated title")
    public void shouldExtractUpdatedFieldAfterPatch() {

        Map<String, Object> payload = Map.of(
                "title", "Extract Patch"
        );

        String updatedTitle =
            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .patch("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .path("title");

        Assert.assertEquals(updatedTitle, "Extract Patch");
    }

    @Test(description = "Verify PATCH response time under 3 seconds")
    public void shouldRespondQuicklyForPatch() {

        Map<String, Object> payload = Map.of(
                "title", "Performance Patch"
        );

        long responseTime =
        	    given()
        	        .spec(getRequestSpec())
        	        .body(payload)
        	    .when()
        	        .patch("/posts/1")
        	    .then()
        	        .statusCode(200)
        	        .extract()
        	        .time();

        	Assert.assertTrue(responseTime < 3000,
        	        "Response time exceeded threshold: " + responseTime);
    }
}