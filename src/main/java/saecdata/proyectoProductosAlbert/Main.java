package saecdata.proyectoProductosAlbert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginView.fxml")); // Asegúrate de que la ruta sea correcta
        AnchorPane root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestión de Productos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
