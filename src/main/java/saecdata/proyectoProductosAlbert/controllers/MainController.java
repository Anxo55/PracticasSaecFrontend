package saecdata.proyectoProductosAlbert.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class MainController {

    @FXML
    private void handleLoginView(ActionEvent event) {
        navigateToLoginView(event);
    }

    @FXML
    private void handleRegisterView(ActionEvent event) {
        navigateToRegisterView(event);
    }

    private void navigateToLoginView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login de Usuario");
        } catch (IOException e) {
            showError("Error al cargar la vista de login.");
        }
    }

    private void navigateToRegisterView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registerView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Registro de Usuario");
        } catch (IOException e) {
            showError("Error al cargar la vista de registro.");
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
