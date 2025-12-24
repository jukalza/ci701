package ci701.hellojavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Authenticate and determine user role
        UserRole role = authenticateUser(username, password);

        if (role != null) {
            // Store current user session
            SessionManager.setCurrentUser(username, role);

            // Load appropriate view based on role
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Parent root;

                if (role == UserRole.ADMIN) {
                    root = FXMLLoader.load(getClass().getResource("admin-view.fxml"));
                    stage.setTitle("IT Ticketing System - Admin Dashboard");
                } else {
                    root = FXMLLoader.load(getClass().getResource("creator-view.fxml"));
                    stage.setTitle("IT Ticketing System - My Tickets");
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error loading dashboard");
            }

        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact IT administrator to reset your password.");
        alert.showAndWait();
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText("Please contact IT administrator to create a new account.");
        alert.showAndWait();
    }

    private UserRole authenticateUser(String username, String password) {
        // Demo credentials:
        // Admin: username="admin", password="admin123"
        // Creator: username="user", password="user123"

        if (username.equals("admin") && password.equals("admin123")) {
            return UserRole.ADMIN;
        } else if (username.equals("user") && password.equals("user123")) {
            return UserRole.CREATOR;
        }

        return null;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}