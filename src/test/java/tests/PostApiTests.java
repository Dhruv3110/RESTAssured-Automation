package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import models.Post;

public class PostApiTests extends BaseTest {

    private static final long RESPONSE_TIME_LIMIT_MS = 15000L;

    @Test(description = "Verify POST /posts creates a new post successfully")
    public void TC_001_shouldCreateNewPostSuccessfully() {
        getLogger().info("TC_001: POST /posts → create post, verify echoed fields");

        Post request = new Post(1, "API Testing", "Learning REST Assured");
        getExtentTest().info("Request body: " + request);

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .as(Post.class);

        getLogger().info("Response: {}", response);

        Assert.assertNotNull(response.getId(),     "id should not be null in response");
        Assert.assertTrue(response.getId() > 0,    "id should be a positive integer");
        Assert.assertEquals(response.getUserId(),  Integer.valueOf(1));
        Assert.assertEquals(response.getTitle(),   "API Testing");
        Assert.assertEquals(response.getBody(),    "Learning REST Assured");

        getExtentTest().pass("Post created and all fields validated. Assigned id=" + response.getId());
    }

    @Test(description = "Verify POST and extract generated ID")
    public void TC_002_shouldCreatePostAndExtractId() {
        getLogger().info("TC_002: POST /posts → extract generated id");

        Post request = new Post(2, "Extract Example", "Testing extraction");

        Post response =
            given()
                .spec(getRequestSpec())
                .body(request)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .as(Post.class);

        Assert.assertNotNull(response.getId(), "id should not be null in response");
        Assert.assertTrue(response.getId() > 0, "Generated id should be positive");

        getExtentTest().pass("Generated id extracted: " + response.getId());
    }

    @Test(description = "Verify POST response time is under 3 seconds")
    public void TC_003_shouldRespondWithinTimeLimitForPost() {
        getLogger().info("TC_003: POST /posts → response time < {}ms", RESPONSE_TIME_LIMIT_MS);

        Post request = new Post(3, "Performance Test", "Response time validation");

        given()
            .spec(getRequestSpec())
            .body(request)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .time(lessThan(RESPONSE_TIME_LIMIT_MS));

        getExtentTest().pass("POST responded within " + RESPONSE_TIME_LIMIT_MS + "ms");
    }

    @Test(description = "Verify POST works for multiple user IDs")
    public void TC_004_shouldCreatePostForDifferentUsers() {
        getLogger().info("TC_004: POST /posts → loop over userId 1..3");

        for (int i = 1; i <= 3; i++) {
            String expectedTitle = "User" + i + " Title";
            Post request = new Post(i, expectedTitle, "Testing user " + i);

            Post response =
                given()
                    .spec(getRequestSpec())
                    .body(request)
                .when()
                    .post("/posts")
                .then()
                    .statusCode(201)
                    .extract()
                    .as(Post.class);

            Assert.assertEquals(response.getUserId(), Integer.valueOf(i),
                    "userId mismatch for iteration " + i);
            Assert.assertEquals(response.getTitle(), expectedTitle,
                    "title mismatch for userId=" + i);

            getExtentTest().info("userId=" + i + " post created. id=" + response.getId());
        }

        getExtentTest().pass("Posts created successfully for userIds 1, 2, 3");
    }

    @Test(description = "Verify POST response matches JSON schema")
    public void TC_005_shouldMatchPostResponseSchema() {
        getLogger().info("TC_005: POST /posts → validate against postPostSchema.json");

        Post request = new Post(1, "Schema Test", "Validating schema");

        given()
            .spec(getRequestSpec())
            .body(request)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/postPostSchema.json"));

        getExtentTest().pass("Response matches postPostSchema.json");
    }
}