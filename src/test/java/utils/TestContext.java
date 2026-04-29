package utils;

import io.restassured.response.Response;

/**
 * Simple shared context to pass data between step definition files.
 * Only holds shared variables - no logic here.
 */
public class TestContext {

    // Shared response object used by all step files and Assertions
    public static Response response;
    public static String postId;
    // Excel data used for validation in Assertions
    public static String excelTitle;
    public static String excelBody;

    public static int commentId;
    public static String userId;
}
