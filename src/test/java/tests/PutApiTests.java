/*
Since JSONPlaceholder is a FAKE API so, it always returns 200 for PUT, 
Does NOT validate Content-Type
Does NOT validate ID existence
Does NOT enforce schema
Does NOT return 400 / 404 / 415
 */

package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class PutApiTests extends BaseTest {

    @Test(description = "Verify PUT /posts/{id} updates a post successfully")
    public void shouldUpdatePostSuccessfully() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Updated Title",
                "body", "Updated Body",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Updated Title"))
            .body("body", equalTo("Updated Body"))
            .body("id", equalTo(1));
    }

    @Test(description = "Verify PUT and extract updated ID")
    public void shouldExtractUpdatedIdAfterPut() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Extract PUT",
                "body", "Testing extraction",
                "userId", 1
        );

        int updatedId =
            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        Assert.assertEquals(updatedId, 1);
    }

    @Test(description = "Verify response headers after PUT")
    public void shouldReturnValidHeadersAfterPut() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Header Test",
                "body", "PUT Header",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"));
    }

    @Test(description = "Verify PUT is idempotent")
    public void shouldRemainConsistentWhenPutCalledMultipleTimes() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Idempotent Test",
                "body", "Same payload",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200);

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Idempotent Test"));
    }

    @Test(description = "Verify PUT response time is under 3 seconds")
    public void shouldRespondQuicklyForPut() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Performance PUT",
                "body", "Performance",
                "userId", 1
        );

        long responseTime =
        	    given()
        	        .spec(getRequestSpec())
        	        .body(payload)
        	    .when()
        	        .put("/posts/1")
        	    .then()
        	        .statusCode(200)
        	        .extract()
        	        .time();

        	Assert.assertTrue(responseTime < 3000,
        	        "Response time exceeded threshold: " + responseTime);
    }

    @Test(description = "Verify PUT with missing body field")
    public void shouldHandleMissingFieldInPutRequest() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "title", "Missing Body",
                "userId", 1
        );

        given()
            .spec(getRequestSpec())
            .body(payload)
        .when()
            .put("/posts/1")
        .then()
        	.statusCode(200);
//        It should have returned status code 400
    }

}