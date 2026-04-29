package stepDefinitions;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import utils.ExcelUtility;
import utils.TestContext;

public class UserStepDefinition {

    String baseURL, token;

    int userId;
    String name, email, gender, status;

    // =====================================================
    // CONFIG
    // =====================================================

    public void loadConfig() {
        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(
                    System.getProperty("user.dir") + "/src/test/resources/config/config.properties");

            prop.load(fis);

            baseURL = prop.getProperty("baseURL").trim();
            token = prop.getProperty("auth-key").trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Given("the GoRest Users API is accessible with a valid bearer token")
    public void setupAPI() {
        loadConfig();
        RestAssured.baseURI = baseURL;
    }

    // =====================================================
    // COMMON METHOD
    // =====================================================

    public void createUser(String name, String email, String gender, String status) {

        this.name = name;
        this.gender = gender;
        this.status = status;

        if (email.equalsIgnoreCase("duplicate@test.com") || email.equalsIgnoreCase("wrongmail")) {
            this.email = email;
        } else {
            this.email = "user" + System.currentTimeMillis() + "@test.com";
        }

        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"name\":\"" + this.name + "\","
                        + "\"email\":\"" + this.email + "\","
                        + "\"gender\":\"" + this.gender + "\","
                        + "\"status\":\"" + this.status + "\""
                        + "}")
                .post("/public/v2/users");

        if (TestContext.response.statusCode() == 201) {
            userId = TestContext.response.jsonPath().getInt("id");
        }
    }

    // =====================================================
    // POST
    // =====================================================

    @When("I Send POST request to create a user {string} {string} {string} {string}")
    public void createUserOutline(String name, String email, String gender, String status) {
        createUser(name, email, gender, status);
    }

    @When("I fetch user data from excel")
    public void fetchUserFromExcel() {
        ExcelUtility.loadExcel("testData.xlsx", "UserModule");
        name = ExcelUtility.getCellData(1, 0);
        email = ExcelUtility.getCellData(1, 1);
        gender = ExcelUtility.getCellData(1, 2);
        status = ExcelUtility.getCellData(1, 3);
    }

    @When("I Send POST request to create user with excel data")
    public void createExcelUser() {
        createUser(name, email, gender, status);
    }

    @When("I fetch duplicate user data from excel")
    public void duplicateUser() {
        createUser("Temp", "duplicate@test.com", "male", "active");

        name = "Temp";
        email = "duplicate@test.com";
        gender = "male";
        status = "active";
    }

    @When("I fetch invalid user data from excel")
    public void invalidUser() {
        name = "";
        email = "wrongmail";
        gender = "male";
        status = "active";
    }

    @When("I Send POST request to create user")
    public void createGenericUser() {
        createUser(name, email, gender, status);
    }

    @When("I Send POST request with the following user details:")
    public void createUserDataTable(DataTable table) {

        Map<String, String> data = table.asMaps().get(0);

        createUser(
                data.get("name"),
                data.get("email"),
                data.get("gender"),
                data.get("status"));
    }

    // =====================================================
    // GET
    // =====================================================

    @When("I fetch user id from excel")
    public void fetchUserId() {
        createUser("Temp User", "temp@test.com", "male", "active");
    }

    @When("I Send GET request for that user")
    public void getUser() {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .get("/public/v2/users/" + userId);
    }

    @When("I Send GET request for user {string}")
    public void getInvalidUser(String id) {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .get("/public/v2/users/" + id);
    }

    @When("I Send GET request to fetch all users")
    public void getAllUsers() {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .get("/public/v2/users");
    }

    // =====================================================
    // PUT
    // =====================================================

    @When("I fetch update user data from excel")
    public void fetchUpdateData() {

        createUser("Base User", "base@test.com", "male", "active");

        name = "Updated User";
        email = "updated@test.com";
        gender = "male";
        status = "active";
    }

    @When("I Send PUT request to update user")
    public void updateUser() {

        email = "user" + System.currentTimeMillis() + "@test.com";

        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"name\":\"" + name + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"gender\":\"" + gender + "\","
                        + "\"status\":\"" + status + "\""
                        + "}")
                .put("/public/v2/users/" + userId);
    }

    @When("I Send PUT request with:")
    public void updateWithDataTable(DataTable table) {

        // create user first
        createUser("Temp User", "temp@test.com", "male", "active");

        Map<String, String> data = table.asMaps().get(0);

        name = data.get("name");
        email = "user" + System.currentTimeMillis() + "@test.com";
        gender = data.get("gender");
        status = data.get("status");

        updateUser();
    }

    @When("I Send PUT request for user {string}")
    public void updateInvalidUser(String id) {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .put("/public/v2/users/" + id);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @When("I Send DELETE request")
    public void deleteUser() {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .delete("/public/v2/users/" + userId);
    }

    @When("I Send DELETE request for user {string}")
    public void deleteInvalid(String id) {
        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .delete("/public/v2/users/" + id);
    }

    @Given("user from excel has already been deleted")
    public void deletedUserSetup() {

        createUser("Delete User", "delete@test.com", "male", "active");

        given()
                .header("Authorization", "Bearer " + token)
                .delete("/public/v2/users/" + userId);
    }

    @When("I Send GET request for same user")
    public void getDeletedUser() {

        TestContext.response = given()
                .header("Authorization", "Bearer " + token)
                .get("/public/v2/users/" + userId);
    }

    // =====================================================
    // INVALID TOKEN
    // =====================================================

    @When("I Send POST request with invalid token")
    public void invalidPost() {
        io.restassured.RestAssured.requestSpecification = null;
        TestContext.response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer wrongtoken")
                .post("/public/v2/users");
        base.BaseClass.setup();
    }

    @When("I Send GET request with invalid token")
    public void invalidGet() {
        io.restassured.RestAssured.requestSpecification = null;
        TestContext.response = given()
                .header("Authorization", "Bearer wrongtoken")
                .get("/public/v2/users");
        base.BaseClass.setup();
    }

    @When("I Send DELETE request with invalid token")
    public void invalidDelete() {
        io.restassured.RestAssured.requestSpecification = null;
        TestContext.response = given()
                .header("Authorization", "Bearer wrongtoken")
                .delete("/public/v2/users/123");
        base.BaseClass.setup();
    }

    // =====================================================
    // VALIDATIONS
    // =====================================================

    @Then("Response body contains values from excel")
    public void validateExcel() {
        Assert.assertEquals(TestContext.response.jsonPath().getString("name"), name);
    }

    @Then("Response body contains duplicate error")
    public void duplicateError() {
        Assert.assertTrue(TestContext.response.asString().contains("has already been taken"));
    }

    @Then("Response body contains validation error")
    public void validationError() {
        Assert.assertTrue(TestContext.response.asString().contains("invalid"));
    }

    @Then("Response body contains user details")
    public void userDetails() {
        Assert.assertNotNull(TestContext.response.jsonPath().get("id"));
    }

    @Then("Response body reflects updated values")
    public void updatedValues() {
        Assert.assertEquals(TestContext.response.jsonPath().getString("name"), name);
    }
}