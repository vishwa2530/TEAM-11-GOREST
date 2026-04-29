package stepDefinitions;

import java.util.List;
import java.util.Map;

import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import utils.DataUtility;
import utils.ExcelUtility;
import utils.TestContext;

public class PostStep {

    // ───────────── USER ID RESOLVER ─────────────
    private String resolveUserId(String userId) {

        if ("dynamic".equalsIgnoreCase(userId)) {

            if (TestContext.userId == null) {

                TestContext.response = RestAssured.given()
                        .when()
                        .get(GoRestEndpoints.GET_ALL_USERS);

                List<Map<String, Object>> users =
                        TestContext.response.jsonPath().getList("$");

                if (users != null && !users.isEmpty()) {
                    TestContext.userId = String.valueOf(users.get(0).get("id"));
                } else {
                    throw new RuntimeException("No users found");
                }
            }

            return TestContext.userId;
        }

        return userId; // for negative cases
    }

    // ───────────── POST ID RESOLVER ─────────────
    private String getPostId(String postId) {

        if ("dynamic".equalsIgnoreCase(postId)) {

            if (TestContext.postId == null) {
                throw new RuntimeException("postId is NULL. Run POST first.");
            }

            return TestContext.postId;
        }

        return postId;
    }

    @Given("the GoRest Posts API is accessible with a valid bearer token")
    public void setup() {}

    // ───────────── POST ─────────────

    @When("I Send POST request to create a post {string} {string} {string}")
    public void createPost(String title, String body, String userIdStr) {

        String resolvedUserId = resolveUserId(userIdStr);

        TestContext.response = RestAssured.given()
                .pathParam("userId", resolvedUserId)
                .body(DataUtility.buildPostJson(title, body, Integer.parseInt(resolvedUserId)))
                .when()
                .post(GoRestEndpoints.CREATE_POST);

        // store postId
        if (TestContext.response.getStatusCode() == 201) {
            TestContext.postId = TestContext.response.jsonPath().getString("id");
        }
    }

    @When("I Send POST request to create a post with non-existing userId {string}")
    public void createInvalidPost(String userIdStr) {

        int userId = Integer.parseInt(userIdStr);

        TestContext.response = RestAssured.given()
                .pathParam("userId", userId)
                .body(DataUtility.buildPostJson("Test Post", "Sample body", userId))
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    @When("I Send POST request to create a post with empty title and body for userId {string}")
    public void createEmptyPost(String userIdStr) {

        String resolvedUserId = resolveUserId(userIdStr);

        TestContext.response = RestAssured.given()
                .pathParam("userId", resolvedUserId)
                .body(DataUtility.buildPostJson("", "", Integer.parseInt(resolvedUserId)))
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    // ───────────── GET ─────────────

    @When("I Send GET post request with post id {string}")
    public void getPost(String postId) {

        TestContext.response = RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .when()
                .get(GoRestEndpoints.GET_POST_BY_ID);
    }

    @When("I Send GET request to fetch all posts")
    public void getAllPosts() {

        TestContext.response = RestAssured.given()
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
    }

    // ───────────── PUT ─────────────

    @When("I Send PUT request to update post {string} with data from excel")
    public void updatePostExcel(String postId) {

        ExcelUtility.loadExcel("testData.xlsx", "PostModule");

        int userId = (int) Double.parseDouble(ExcelUtility.getCellData(1, 0));
        String title = ExcelUtility.getCellData(1, 1);
        String body = ExcelUtility.getCellData(1, 2);

        TestContext.excelTitle = title;
        TestContext.excelBody = body;

        TestContext.response = RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .body(DataUtility.buildPostJson(title, body, userId))
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    @When("I Send PUT request to update post {string} with the following details:")
    public void updatePostDT(String postId, DataTable table) {

        Map<String, String> data = table.asMaps().get(0);

        TestContext.response = RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .body(DataUtility.buildPostJson(data))
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    @When("I Send PUT request to update post {string} with invalid data types")
    public void updateInvalid(String postId) {

        String body = "{ \"title\":123, \"body\":null, \"userId\":\"abc\" }";

        TestContext.response = RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .body(body)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    // ───────────── DELETE ─────────────

    @When("I Send DELETE request for post {string}")
    public void deletePost(String postId) {

        TestContext.response = RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    @Given("post {string} has already been deleted")
    public void deleteBefore(String postId) {

        RestAssured.given()
                .pathParam("postId", getPostId(postId))
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    // ───────────── AUTH ─────────────

    @When("I Send POST request to create a post with invalid or expired token")
    public void invalidTokenPost() {

        TestContext.response = RestAssured.given()
                .auth().oauth2("invalid")
                .pathParam("userId", "1")
                .body(DataUtility.buildPostJson("Test", "Body", 1))
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    @When("I Send DELETE request for post {string} with invalid or expired token")
    public void invalidTokenDelete(String postId) {

        TestContext.response = RestAssured.given()
                .auth().oauth2("invalid")
                .pathParam("postId", postId)
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    @When("I Send GET request to fetch all posts with no auth")
    public void noAuth() {

        TestContext.response = RestAssured.given()
                .auth().none()
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
    }
}