package utils;

import java.util.Map;

/**
 * Utility class to build JSON request bodies for each module.
 */
public class DataUtility {

    public static String buildPostJson(String title, String body, int userId) {
        return String.format(
                "{\"user_id\":%d,\"title\":\"%s\",\"body\":\"%s\"}",
                userId, title, body);
    }

    public static String buildPostJson(Map<String, String> row) {
        String userIdStr = row.containsKey("userId") ? row.get("userId") : row.get("user_id");
        return buildPostJson(
                row.get("title"),
                row.get("body"),
                Integer.parseInt(userIdStr));
    }

    public static String buildTodoJson(String title, String status, int userId) {
        return String.format("{\"user_id\":%d,\"title\":\"%s\",\"status\":\"%s\"}", userId, title, status);
    }

    public static String buildUserJson(String name, String email, String gender, String status) {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\",\"gender\":\"%s\",\"status\":\"%s\"}", name, email, gender, status);
    }

    public static String buildCommentJson(String name, String email, String body, int postId) {
        return String.format("{\"post_id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"body\":\"%s\"}", postId, name, email, body);
    }

    public static String buildCommentJson(Map<String, String> row) {
        String postIdStr = row.containsKey("postId") ? row.get("postId") : row.get("post_id");
        int postId = 0;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (Exception e) {}
        return buildCommentJson(
                row.get("name"),
                row.get("email"),
                row.get("body"),
                postId);
    }
}