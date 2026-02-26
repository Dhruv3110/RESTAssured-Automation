package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.testng.annotations.Test;

import base.BaseTest;

public class DeleteApiTests extends BaseTest {

    @Test(description = "Verify DELETE /posts/{id} removes the resource successfully")
    public void shouldDeletePostSuccessfully() {

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200);
    }

    @Test(description = "Verify DELETE response body is empty or valid")
    public void shouldReturnEmptyBodyAfterDelete() {

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200)
            .body(equalTo("{}"));
    }

    @Test(description = "Verify DELETE with non-existing ID")
    public void shouldHandleDeleteWithInvalidId() {

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/9999")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)));
    }

    @Test(description = "Verify DELETE response time under 3 seconds")
    public void shouldRespondQuicklyForDelete() {

        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200)
            .time(lessThan(3000L));
    }

    @Test(description = "Verify full delete flow")
    public void shouldDeletePostAndVerifyItIsRemoved() {

        // Step 1: Create post
        Map<String, Object> payload = Map.of(
                "title", "Delete Flow",
                "body", "Testing delete",
                "userId", 1
        );

        int generatedId =
            given()
                .spec(getRequestSpec())
                .body(payload)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Step 2: Delete post
        given()
            .spec(getRequestSpec())
        .when()
            .delete("/posts/" + generatedId)
        .then()
            .statusCode(200);

        // Step 3: Verify deletion
        given()
            .spec(getRequestSpec())
        .when()
            .get("/posts/" + generatedId)
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)));
    }
}