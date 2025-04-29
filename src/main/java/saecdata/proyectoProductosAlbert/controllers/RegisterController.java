package saecdata.proyectoProductosAlbert.controllers;

import saecdata.proyectoProductosAlbert.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Los campos no pueden estar vacíos.");
            return;
        }

        boolean isRegistered = authService.register(username, password);

        if (isRegistered) {
            navigateToLoginView();
        } else {
            showError("Ya existe un usuario con ese nombre.");
        }
    }

    private void navigateToLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login de Usuario");
        } catch (IOException e) {
            showError("Error al cargar la vista de login.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
