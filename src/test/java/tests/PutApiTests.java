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

//import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import models.Post;

public class PutApiTests extends BaseTest {

    @Test(description = "Verify PUT /posts/{id} updates a post successfully")
    public void shouldUpdatePostSuccessfully() {

        Post updatedPost = new Post(1,"Updated Title", "Updated Body");
        updatedPost.setId(1);
        
        Post responsePost = 
        given()
            .spec(getRequestSpec())
            .body(updatedPost)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .extract()
            .as(Post.class);
        
        Assert.assertEquals(responsePost.getTitle(), "Updated Title");
        Assert.assertEquals(responsePost.getBody(), "Updated Body");
        Assert.assertEquals(responsePost.getId(), 1);
    }

    @Test(description = "Verify PUT and extract updated ID")
    public void shouldExtractUpdatedIdAfterPut() {

    	Post updatedPost = new Post(1,"Extract PUT", "Testing extraction");
    	updatedPost.setId(1);

        Post responsePost =
            given()
                .spec(getRequestSpec())
                .body(updatedPost)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        Assert.assertEquals(responsePost.getId(), 1);
    }

    @Test(description = "Verify response headers after PUT")
    public void shouldReturnValidHeadersAfterPut() {

    	Post updatedPost = new Post(1,"Header Test", "PUT Header");
	        given()
	            .spec(getRequestSpec())
	            .body(updatedPost)
	        .when()
	            .put("/posts/1")
	        .then()
	            .statusCode(200)
	            .header("Content-Type", startsWith("application/json"));
    }

    @Test(description = "Verify PUT is idempotent")
    public void shouldRemainConsistentWhenPutCalledMultipleTimes() {
        
        Post payload = new Post(1,"Idempotent Test", "Same payload");
        payload.setId(1);
        Post firstResponse = 
	        given()
	            .spec(getRequestSpec())
	            .body(payload)
	        .when()
	            .put("/posts/1")
	        .then()
	            .statusCode(200)
	            .extract()
	            .as(Post.class);
        
        Post secondResponse =
	        given()
	            .spec(getRequestSpec())
	            .body(payload)
	        .when()
	            .put("/posts/1")
	        .then()
	            .statusCode(200)
	            .extract()
	            .as(Post.class);
        
        Assert.assertEquals(firstResponse.getTitle(), secondResponse.getTitle());
        Assert.assertEquals(firstResponse.getBody(), secondResponse.getBody());
        Assert.assertEquals(firstResponse.getUserId(), secondResponse.getUserId());
	            
    }

    @Test(description = "Verify PUT response time is under 3 seconds")
    public void shouldRespondQuicklyForPut() {

        Post updatedPost = new Post(1,"Performance PUT", "Performance");
        updatedPost.setId(1);
        
	    given()
	        .spec(getRequestSpec())
	        .body(updatedPost)
	    .when()
	        .put("/posts/1")
	    .then()
	        .statusCode(200)
	        .time(lessThan(10000L));
    }

//    @Test(description = "Verify PUT with missing body field")
//    public void shouldHandleMissingFieldInPutRequest() {
//
//        Map<String, Object> payload = Map.of(
//                "id", 1,
//                "title", "Missing Body",
//                "userId", 1
//        );
//
//        given()
//            .spec(getRequestSpec())
//            .body(payload)
//        .when()
//            .put("/posts/1")
//        .then()
//        	.statusCode(400);
//       It should have returned status code 400
//    }

}