package stepDefinitions;

import java.util.Map;

import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import utils.DataUtility;
import utils.ExcelUtility;
import utils.TestContext;

public class TodosSteps {
    private static int generatedTodoId;

    @Given("the GoRest Todos API is accessible with a valid bearer token")
    public void setup() {
        // handled in hooks
    }

    // ================= CREATE =================

    @When("I send POST request to create a todo {string} {string} {string}")
    public void createTodoOutline(String title, String status, String userId) {

        String body = DataUtility.buildTodoJson(title, status, Integer.parseInt(userId));

        TestContext.response = RestAssured.given()
                .pathParam("userId", userId)
                .body(body)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        // store generated ID locally
        generatedTodoId = TestContext.response.jsonPath().getInt("id");
    }

    @When("I send POST request to create a todo with data from excel")
    public void createTodoExcel() {

        ExcelUtility.loadExcel("testData.xlsx", "TodosModule");

        String title = ExcelUtility.getCellData(1, 0);
        String status = ExcelUtility.getCellData(1, 1);
        int userId = (int) Double.parseDouble(ExcelUtility.getCellData(1, 2));

        TestContext.excelTitle = title;

        String body = DataUtility.buildTodoJson(title, status, userId);

        TestContext.response = RestAssured.given()
                .pathParam("userId", userId)
                .body(body)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        generatedTodoId = TestContext.response.jsonPath().getInt("id");
    }

    @When("I send POST request to create a todo with the following details:")
    public void createTodoDataTable(DataTable table) {

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        String title = data.get("title");
        String status = data.get("status");
        int userId = Integer.parseInt(data.get("userId"));

        String body = DataUtility.buildTodoJson(title, status, userId);

        TestContext.response = RestAssured.given()
                .pathParam("userId", userId)
                .body(body)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        generatedTodoId = TestContext.response.jsonPath().getInt("id");
    }

    @When("I send POST request to create a todo with invalid userId {string}")
    public void createTodoInvalidUser(String userId) {

        String body = DataUtility.buildTodoJson("Invalid", "pending", Integer.parseInt(userId));

        TestContext.response = RestAssured.given()
                .pathParam("userId", userId)
                .body(body)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);
    }

    // ================= GET =================

    @When("I send GET todo request with id {string}")
    public void getTodoById(String id) {

    int actualId;

    if (id.equals("99999") || id.equals("999999999")) {
        actualId = -1;   // guaranteed invalid ID → API gives 404
    } else {
        actualId = generatedTodoId != 0 ? generatedTodoId : Integer.parseInt(id);
    }

    TestContext.response = RestAssured.given()
            .pathParam("todoId", actualId)
            .when()
            .get(GoRestEndpoints.GET_TODO_BY_ID);
}

    @When("I send GET request to fetch all todos")
    public void getAllTodos() {

        TestContext.response = RestAssured.given()
                .when()
                .get(GoRestEndpoints.GET_ALL_TODOS);
    }

    // ================= UPDATE =================

    @When("I send PUT request to update todo {string} with data from excel")
    public void updateTodoExcel(String id) {

        int actualId = generatedTodoId != 0 ? generatedTodoId : Integer.parseInt(id);

        ExcelUtility.loadExcel("testData.xlsx", "TodosModule");

        String title = ExcelUtility.getCellData(3, 0);
        String status = ExcelUtility.getCellData(3, 1);
        int userId = (int) Double.parseDouble(ExcelUtility.getCellData(3, 2));

        TestContext.excelTitle = title;

        String body = DataUtility.buildTodoJson(title, status, userId);

        TestContext.response = RestAssured.given()
                .pathParam("todoId", actualId)
                .body(body)
                .when()
                .put(GoRestEndpoints.UPDATE_TODO);
    }

    @When("I send PUT request to update todo {string} with the following details:")
    public void updateTodoDataTable(String id, DataTable table) {

        int actualId = generatedTodoId != 0 ? generatedTodoId : Integer.parseInt(id);

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        String title = data.get("title");
        String status = data.get("status");
        int userId = Integer.parseInt(data.get("userId"));

        String body = DataUtility.buildTodoJson(title, status, userId);

        TestContext.response = RestAssured.given()
                .pathParam("todoId", actualId)
                .body(body)
                .when()
                .put(GoRestEndpoints.UPDATE_TODO);
    }

    @When("I send PUT request to update todo {string}")
    public void updateTodoInvalidId(String id) {

        String body = DataUtility.buildTodoJson("Test", "pending", 8449790);

        TestContext.response = RestAssured.given()
                .pathParam("todoId", id)
                .body(body)
                .when()
                .put(GoRestEndpoints.UPDATE_TODO);
    }

    // ================= DELETE =================

    @When("I send DELETE request for todo {string}")
    public void deleteTodo(String id) {

        int actualId = generatedTodoId != 0 ? generatedTodoId : Integer.parseInt(id);

        TestContext.response = RestAssured.given()
                .pathParam("todoId", actualId)
                .when()
                .delete(GoRestEndpoints.DELETE_TODO);
    }

    // ================= AUTH =================

    @When("I send POST request to create a todo with invalid token")
    public void createTodoInvalidToken() {

        String body = DataUtility.buildTodoJson("AuthTest", "pending", 8449790);

        RestAssured.requestSpecification = null;

        TestContext.response = RestAssured.given()
                .baseUri(config.ConfigManager.getData("baseURL"))
                .contentType(io.restassured.http.ContentType.JSON)
                .accept("application/json")
                .header("Authorization", "Bearer invalid_token")
                .pathParam("userId", "8449790")
                .body(body)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        base.BaseClass.setup();
    }

    @When("I send GET request to fetch all todos with no auth")
    public void getAllTodosWithNoAuth() {
        RestAssured.requestSpecification = null;
        TestContext.response = RestAssured.given()
                .baseUri(config.ConfigManager.getData("baseURL"))
                .contentType(io.restassured.http.ContentType.JSON)
                .accept("application/json")
                .when()
                .get(GoRestEndpoints.GET_ALL_TODOS);
        base.BaseClass.setup();
    }
}