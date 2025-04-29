package saecdata.proyectoProductosAlbert.controllers;

import saecdata.proyectoProductosAlbert.models.Categoria;
import saecdata.proyectoProductosAlbert.models.Producto;
import saecdata.proyectoProductosAlbert.services.ProductoService;
import saecdata.proyectoProductosAlbert.services.CategoriaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    private static final String ERROR_CAMPOS_VACIOS = "Los campos no pueden estar vacíos.";
    private static final String ERROR_NUMERICO = "El precio y el stock deben ser números válidos y no negativos.";

    @FXML private TextField nombreField;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private TableView<Producto> productoTable;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, String> categoriaColumn;

    private final ProductoService productoService = new ProductoService();
    private final CategoriaService categoriaService = new CategoriaService();

    @FXML
    private void initialize() {
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        precioColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecio()));
        stockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStock()));
        categoriaColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCategoria() != null
                    ? cellData.getValue().getCategoria().getNombre() : "Sin categoría")
        );

        productoTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> cargarProductoEnFormulario(newValue));

        cargarCategorias();
        mostrarProductos();
    }

    private void cargarCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        if (categorias != null) {
            categoriaComboBox.getItems().setAll(categorias);
        } else {
            mostrarError("No se pudo obtener la lista de categorías.");
        }
    }

    private void cargarProductoEnFormulario(Producto producto) {
        if (producto != null) {
            nombreField.setText(producto.getNombre());
            precioField.setText(String.valueOf(producto.getPrecio()));
            stockField.setText(String.valueOf(producto.getStock()));
            categoriaComboBox.setValue(producto.getCategoria());
        }
    }

    @FXML
    private void mostrarProductos() {
        List<Producto> productos = productoService.getAllProductos();
        if (productos != null) {
            productoTable.getItems().setAll(productos);
        } else {
            mostrarError("No se pudo obtener la lista de productos.");
        }
    }

    @FXML
    private void crearProducto() {
        if (!validarCampos()) return;

        try {
            double precio = Double.parseDouble(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (precio < 0 || stock < 0) {
                mostrarError(ERROR_NUMERICO);
                return;
            }

            Producto nuevo = new Producto(
                    nombreField.getText(),
                    precio,
                    stock,
                    categoriaComboBox.getValue()
            );
            if (productoService.crearProducto(nuevo)) {
                mostrarProductos();
                limpiarCampos();
            } else {
                mostrarError("No se pudo crear el producto.");
            }
        } catch (NumberFormatException e) {
            mostrarError(ERROR_NUMERICO);
        }
    }

    @FXML
    private void actualizarProducto() {
        Producto seleccionado = productoTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un producto para actualizar.");
            return;
        }

        if (!validarCampos()) return;

        try {
            double precio = Double.parseDouble(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (precio < 0 || stock < 0) {
                mostrarError(ERROR_NUMERICO);
                return;
            }

            seleccionado.setNombre(nombreField.getText());
            seleccionado.setPrecio(precio);
            seleccionado.setStock(stock);
            seleccionado.setCategoria(categoriaComboBox.getValue());

            if (productoService.actualizarProducto(seleccionado)) {
                mostrarProductos();
                limpiarCampos();
            } else {
                mostrarError("No se pudo actualizar el producto.");
            }
        } catch (NumberFormatException e) {
            mostrarError(ERROR_NUMERICO);
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = productoTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un producto para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este producto?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (productoService.eliminarProducto(seleccionado.getId())) {
                mostrarProductos();
                limpiarCampos();
            } else {
                mostrarError("No se pudo eliminar el producto.");
            }
        }
    }

    private boolean validarCampos() {
        String nombre = nombreField.getText();
        String precioStr = precioField.getText();
        String stockStr = stockField.getText();
        Categoria categoria = categoriaComboBox.getValue();

        if (nombre == null || nombre.isEmpty() ||
            precioStr == null || precioStr.isEmpty() ||
            stockStr == null || stockStr.isEmpty() ||
            categoria == null) {
            mostrarError(ERROR_CAMPOS_VACIOS);
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        nombreField.clear();
        precioField.clear();
        stockField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        productoTable.getSelectionModel().clearSelection();
    }

    private void mostrarError(String mensaje) {
        System.err.println("ERROR: " + mensaje);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
        } catch (IOException e) {
            mostrarError("Error al cargar la vista de inicio de sesión.");
        }
    }
}
