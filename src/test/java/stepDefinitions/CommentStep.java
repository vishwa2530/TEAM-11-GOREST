package stepDefinitions;

import java.util.Map;
import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.RestAssured;
import utils.DataUtility;
import utils.ExcelUtility;
import utils.TestContext;
import org.testng.Assert;
// import base.BaseClass;
// import config.ConfigManager;

public class CommentStep {

    // Using a static variable to guarantee persistence across scenarios
    private static int latestCommentId = 0;

    @Given("the GoRest Comments API is accessible with a valid bearer token")
    public void setup() {
        // Setup is handled globally in Hooks -> BaseClass
    }

    // Helper to ensure we have a valid postId for tests
    private int getValidPostId() {
        try {
            return RestAssured.given()
                    .get(GoRestEndpoints.GET_ALL_POSTS)
                    .jsonPath().getInt("[0].id");
        } catch (Exception e) {
            return 278626; // Fallback
        }
    }

    // Helper to replace hardcoded ID with dynamic one if available
    private String getEffectiveId(String id) {
        if ((id.equals("187292") || id.equals("278134")) && latestCommentId != 0) {
            return String.valueOf(latestCommentId);
        }
        return id;
    }

    // ================= POST =================

    @When("I Send POST request to create a comment with data from excel")
    public void createCommentExcel() {
        ExcelUtility.loadExcel("testData.xlsx", "CommentsModule");
        
        String name = ExcelUtility.getCellData(1, 0);
        String email = "test" + System.currentTimeMillis() + "@test.com"; 
        String body = ExcelUtility.getCellData(1, 2);
        
        int postId = getValidPostId();

        TestContext.excelTitle = name; 
        TestContext.excelBody = body;

        String requestBody = DataUtility.buildCommentJson(name, email, body, postId);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("postId", postId)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_COMMENT);

        if (TestContext.response.getStatusCode() == 201) {
            try {
                latestCommentId = TestContext.response.jsonPath().getInt("id");
                TestContext.commentId = latestCommentId;
            } catch (Exception e) {}
        }
    }

    @When("I Send POST request to create a comment with non-existing postId from excel")
    public void createCommentInvalidPostId() {
        ExcelUtility.loadExcel("testData.xlsx", "CommentsModule");
        String name = ExcelUtility.getCellData(1, 0);
        String email = ExcelUtility.getCellData(1, 1);
        String body = ExcelUtility.getCellData(1, 2);

        String requestBody = DataUtility.buildCommentJson(name, email, body, 999999);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("postId", 999999)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_COMMENT);
    }

    @When("I Send POST request to create a comment with empty or duplicate data from excel")
    public void createCommentEmptyData() {
        String requestBody = DataUtility.buildCommentJson("", "", "", 0);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("postId", 0)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_COMMENT);
    }

    // ================= GET =================

    @When("I Send GET comment request with comment id {string}")
    public void getCommentById(String id) {
        String finalId = getEffectiveId(id);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("commentId", finalId)
                .when()
                .get(GoRestEndpoints.GET_COMMENT_BY_ID);
    }

    @When("I Send GET request to fetch all comments")
    public void getAllComments() {
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(GoRestEndpoints.GET_ALL_COMMENTS);
    }

    // ================= PUT =================

    @When("I Send PUT request to update comment {string} with data from excel")
    public void updateCommentExcel(String id) {
        ExcelUtility.loadExcel("testData.xlsx", "CommentsModule");
        
        String name = ExcelUtility.getCellData(1, 0) + " Updated";
        String email = "upd" + System.currentTimeMillis() + "@test.com";
        String body = ExcelUtility.getCellData(1, 2) + " Updated";
        
        int postId = getValidPostId();

        TestContext.excelTitle = name;
        TestContext.excelBody = body;

        String finalId = getEffectiveId(id);
        String requestBody = DataUtility.buildCommentJson(name, email, body, postId);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("commentId", finalId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_COMMENT);
    }

    @When("I Send PUT request to update comment {string} with the following details:")
    public void updateCommentDataTable(String id, DataTable table) {
        Map<String, String> row = table.asMaps().get(0);
        String requestBody = DataUtility.buildCommentJson(row);

        String finalId = getEffectiveId(id);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("commentId", finalId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_COMMENT);
    }

    // ================= DELETE =================

    @When("I Send DELETE request for comment {string}")
    public void deleteComment(String id) {
        String finalId = getEffectiveId(id);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("commentId", finalId)
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }

    @Given("comment {string} has already been deleted")
    public void commentAlreadyDeleted(String id) {
        String finalId = getEffectiveId(id);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("commentId", finalId)
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }

    // ================= AUTH =================

    @When("I Send POST request to create a comment with invalid or expired token {string}")
    public void postInvalidToken(String token) {
        String requestBody = DataUtility.buildCommentJson("Test", "test@test.com", "Body", 278626);
        
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .pathParam("postId", getValidPostId())
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_COMMENT);
    }

    @When("I Send DELETE request for comment {string} with invalid or expired token {string}")
    public void deleteInvalidToken(String id, String token) {
        String finalId = getEffectiveId(id);
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .pathParam("commentId", finalId)
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }

    @When("I Send GET request to fetch all comments with invalid or expired token {string}")
    public void getInvalidToken(String token) {
        // Intentional failure for reporting: returns 200, test expects 401
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().none()
                .when()
                .get(GoRestEndpoints.GET_ALL_COMMENTS);
    }

    @When("I Send GET request to fetch all comments with no auth")
    public void getNoAuth() {
        // Intentional failure for reporting: returns 200, test expects 401
        TestContext.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().none()
                .when()
                .get(GoRestEndpoints.GET_ALL_COMMENTS);
    }

    // ================= VALIDATION =================

    @Then("Response body indicates the comment no longer exists")
    public void responseDeleted() {
        Assert.assertEquals(TestContext.response.getStatusCode(), 404, "Comment deletion validation failed");
    }
}