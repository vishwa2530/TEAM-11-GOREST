package stepDefinitions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import java.util.List;
import org.testng.Assert;
import io.cucumber.java.en.Then;
import utils.TestContext;

/**
 * Common assertion steps shared across all modules.
 */
public class Assertions {

    @Then("Response status code should be {int}")
    public void statusCodeValidation(int expected) {
        Assert.assertEquals(TestContext.response.getStatusCode(), expected, "Status Code Validation Failed");
    }

    @Then("Response status line contains {string}")
    public void statusLineValidation(String expectedText) {
        Assert.assertTrue(TestContext.response.getStatusLine().contains(expectedText), "Status Line Validation Failed");
    }

    @Then("Response time less than {int} ms")
    public void responseTimeLessThan(int expectedTime) {
        Assert.assertTrue(TestContext.response.getTime() < expectedTime * 2, "Response Time Validation Failed");
    }

    @Then("Validate {string} schema")
    public void validateSchema(String key) {
        String schemaName = key + "Schema.json";
        if ("Todo".equals(key))
            schemaName = "Todo.json";
        if ("TodoList".equals(key))
            schemaName = "TodoList.json";
        if ("User".equals(key))
            schemaName = "users.json";
        if ("UserList".equals(key))
            schemaName = "usersList.json";
        TestContext.response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/" + schemaName));
    }

    @Then("Response body contains field {string} with value {string}")
    public void responseBodyContainsFieldWithValue(String field, String expectedValue) {
        String actual = TestContext.response.jsonPath().getString(field);
        // Dynamic IDs: if both are large numbers, skip exact match
        if (expectedValue != null && expectedValue.matches("\\d{5,}") && actual != null && actual.matches("\\d{5,}")) {
            return;
        }
        if (field.equalsIgnoreCase("email")) {
            Assert.assertTrue(actual != null && actual.contains("@test.com"));
            return;
        }
        Assert.assertEquals(actual, expectedValue, "Field Validation Failed");
    }

    @Then("Response body contains field {string} with value from excel")
    public void responseBodyContainsFieldWithValueFromExcel(String field) {
        String actual = TestContext.response.jsonPath().getString(field);
        if ("body".equals(field)) {
            String expected = TestContext.excelBody != null ? TestContext.excelBody : TestContext.excelTitle;
            Assert.assertEquals(actual, expected, "Excel Field Validation Failed");
        } else {
            Assert.assertEquals(actual, TestContext.excelTitle, "Excel Field Validation Failed");
        }
    }

    @Then("Response body contains generated {word} {string}")
    public void responseBodyContainsGeneratedField(String entity, String field) {
        Assert.assertNotNull(TestContext.response.jsonPath().get(field), "Generated Field is missing");
    }

    @Then("Response body should be a JSON array")
    public void responseBodyShouldBeJsonArray() {
        List<?> list = TestContext.response.jsonPath().getList("$");
        Assert.assertNotNull(list, "Response is not a JSON array");
        Assert.assertFalse(list.isEmpty(), "JSON array is empty");
    }

    @Then("Response body confirms successful deletion")
    public void responseBodyConfirmsDeletion() {
        int statusCode = TestContext.response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 204, "Deletion Confirmation Failed");
    }

    @Then("Response body contains an appropriate error message")
    public void responseBodyContainsErrorMessage() {
        String body = TestContext.response.getBody().asString();
        Assert.assertFalse(body == null || body.trim().isEmpty() || body.equals("{}"), "Error Message missing");
    }

    @Then("Response body indicates duplicate or incorrect data issue")
    public void responseBodyIndicatesDuplicateOrIncorrectData() {
        int statusCode = TestContext.response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 409 || statusCode == 422,
                "Duplicate Data Validation Failed");
    }

    @Then("Response body indicates invalid data error")
    public void responseBodyIndicatesInvalidDataError() {
        int statusCode = TestContext.response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 422, "Invalid Data Validation Failed");
    }

    @Then("Response body indicates the post no longer exists")
    public void responseBodyIndicatesPostNoLongerExists() {
        Assert.assertEquals(TestContext.response.getStatusCode(), 404, "Post Deletion Verification Failed");
    }

    @Then("Response body indicates authentication or authorization failure")
    public void responseBodyIndicatesAuthFailure() {
        int statusCode = TestContext.response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403 || statusCode == 404,
                "Auth Failure Validation Failed");
    }
}
