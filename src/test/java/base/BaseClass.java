package base;

import config.ConfigManager;
import io.restassured.RestAssured;

public class BaseClass {

    public static void setup() {
        RestAssured.baseURI = ConfigManager.getData("baseURL");

        RestAssured.requestSpecification = RestAssured
                .given()
                .auth().oauth2(ConfigManager.getData("auth-key"))
                .contentType("application/json")
                .accept("application/json");
    }
}
