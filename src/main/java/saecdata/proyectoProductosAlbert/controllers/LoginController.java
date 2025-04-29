package saecdata.proyectoProductosAlbert.controllers;

import saecdata.proyectoProductosAlbert.auth.AuthContext;
import saecdata.proyectoProductosAlbert.config.AppConfig;
import saecdata.proyectoProductosAlbert.models.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            URL url = new URL(AppConfig.BASE_URL + "/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() == 200) {
                InputStream responseStream = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                JwtResponse jwtResponse = mapper.readValue(responseStream, JwtResponse.class);
                AuthContext.jwtToken = jwtResponse.getToken(); // Almacenar token

                // Cambiar a pantalla principal
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/productoView.fxml"));
                AnchorPane root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestión de Productos");
            } else {
                mostrarError("Login fallido: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al intentar iniciar sesión.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Login");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
