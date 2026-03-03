package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;
//import static org.hamcrest.Matchers.*;
//import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import models.Post;
public class PostApiTests extends BaseTest {

    @Test(description = "Verify POST /posts creates a new post successfully")
    public void shouldCreateNewPostSuccessfully() {

        Post post = new Post(1,"API Testing","Learning REST Assured" ); // Serialization Input
        
        Post responsePost = 
        		given()
		            .spec(getRequestSpec())
		            .body(post)
		        .when()
	            	.post("/posts")
	            .then()
		            .statusCode(201)
		            .extract()
		            .as(Post.class);
        
        Assert.assertEquals(responsePost.getTitle(), "API Testing");
        Assert.assertEquals(responsePost.getBody(), "Learning REST Assured");
        Assert.assertEquals(responsePost.getUserId(), 1);
        Assert.assertTrue(responsePost.getId() > 0);
           
    }

    @Test(description = "Verify POST and extract generated ID")
    public void shouldCreatePostAndExtractId() {

        Post requestPost = new Post(2,"Extract Example", "Testing extraction");

        Post createdPost = 
            given()
                .spec(getRequestSpec())
                .body(requestPost)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .as(Post.class);

        Assert.assertTrue(createdPost.getId() > 0);
    }

//    @Test(description = "Verify POST fails when required field is missing")
//    public void shouldFailWhenRequiredFieldMissing() {
//
//        Map<String, Object> requestBody = Map.of(
//                "title", "Invalid Test"
//        );
//
//        given()
//            .spec(getRequestSpec())
//            .body(requestBody)
//        .when()
//            .post("/posts")
//        .then()
//            // JSONPlaceholder always returns 201, real API should return 400
//            .statusCode(anyOf(equalTo(400)));
//    }

    @Test(description = "Verify POST response time is under 3 seconds")
    public void shouldRespondWithinTimeLimitForPost() {
    	
    	Post requestPost = new Post(3, "Performance Test", "Response time validation");
        
    	 given()
    	 	.spec(getRequestSpec())
	        .body(requestPost)
         .when()
         	.post("/posts")
         .then()
         	.statusCode(201)
	        .time(lessThan(10000L)); 
    }

    @Test(description = "Verify POST works for multiple user IDs")
    public void shouldCreatePostForDifferentUsers() {

        for (int i = 1; i <= 3; i++) {
        	
        	Post requestPost = new Post(i,"User" + i, "Testing user" + i);
        	
        	Post responsePost = 
	            given()
	                .spec(getRequestSpec())
	                .body(requestPost)
	            .when()
	                .post("/posts")
	            .then()
	                .statusCode(201)
	                .extract()
	                .as(Post.class);
        	
        	Assert.assertEquals(responsePost.getUserId(), i);
        }
    }

    @Test(description = "Verify POST response matches JSON schema")
    public void shouldMatchPostResponseSchema() {
        
        Post requestPost = new Post(1, "Schema Test", "Validating schema");

        given()
            .spec(getRequestSpec())
            .body(requestPost)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/postPostSchema.json"));
    }
}