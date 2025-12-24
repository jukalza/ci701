package ci701.hellojavafx;

public class SessionManager {
    private static String currentUsername;
    private static UserRole currentUserRole;

    public static void setCurrentUser(String username, UserRole role) {
        currentUsername = username;
        currentUserRole = role;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static UserRole getCurrentUserRole() {
        return currentUserRole;
    }

    public static void clearSession() {
        currentUsername = null;
        currentUserRole = null;
    }
}
