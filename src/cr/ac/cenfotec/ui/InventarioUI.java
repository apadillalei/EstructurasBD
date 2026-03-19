package cr.ac.cenfotec.ui;

import cr.ac.cenfotec.bl.entities.Producto;
import cr.ac.cenfotec.bl.logic.ArbolProducto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.io.File;

public class  InventarioUI extends Application {

    private final ArbolProducto inventario = new ArbolProducto();
    private final ObservableList<Producto> data = FXCollections.observableArrayList();
    private final TableView<Producto> table = new TableView<>();

    @Override
    public void start(Stage stage) {
        // --- INPUTS ---
        TextField nombreField = new TextField(); nombreField.setPromptText("Nombre");
        TextField categoriaField = new TextField(); categoriaField.setPromptText("Categoría");
        TextField fechaField = new TextField(); fechaField.setPromptText("Fecha Vencimiento");
        TextField precioField = new TextField(); precioField.setPromptText("Precio");
        TextField cantidadField = new TextField(); cantidadField.setPromptText("Cantidad");

        // --- BOTONES ---
        Button agregarBtn = new Button("Agregar");
        Button modificarBtn = new Button("Modificar");
        Button buscarBtn = new Button("Buscar");
        Button eliminarBtn = new Button("Eliminar"); // BOTÓN NUEVO
        eliminarBtn.setStyle("-fx-base: #ff6666;"); // Rojo para advertencia
        Button agregarImagenBtn = new Button("Agregar imagen");

        Label status = new Label("Estado: Listo");

        // --- TABLA ---
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        table.getColumns().addAll(colNombre, colPrecio, colCantidad);
        table.setItems(data);

        // --- IMÁGENES ---
        ListView<String> imagenesList = new ListView<>();
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(200); imagePreview.setPreserveRatio(true);

        // --- LÓGICA DE BOTONES ---

        agregarBtn.setOnAction(e -> {
            try {
                Producto p = new Producto(nombreField.getText(), Double.parseDouble(precioField.getText()),
                        categoriaField.getText(), fechaField.getText(), Integer.parseInt(cantidadField.getText()));
                inventario.insertar(p);
                refrescarTabla();
                status.setText("Producto agregado al árbol ✔");
            } catch (Exception ex) { status.setText("Error: Datos inválidos ❌"); }
        });

        buscarBtn.setOnAction(e -> {
            Producto p = inventario.buscar(nombreField.getText());
            if (p != null) {
                precioField.setText(String.valueOf(p.getPrecio()));
                categoriaField.setText(p.getCategoria());
                status.setText("Producto encontrado 🔍");
            } else { status.setText("No encontrado ❌"); }
        });

        eliminarBtn.setOnAction(e -> {
            String nombre = nombreField.getText();
            if (!nombre.isBlank()) {
                inventario.eliminar(nombre);
                refrescarTabla();
                status.setText("Producto '" + nombre + "' eliminado ✔");
            }
        });

        // --- LAYOUT ---
        HBox inputs = new HBox(10, nombreField, categoriaField, precioField, cantidadField);
        HBox botones = new HBox(10, agregarBtn, modificarBtn, buscarBtn, eliminarBtn, agregarImagenBtn);
        VBox root = new VBox(15, inputs, botones, new HBox(15, table, imagenesList, imagePreview), status);
        root.setPadding(new Insets(20));

        stage.setTitle("Cenfotec - Gestión de Inventario (Árbol BST)");
        stage.setScene(new Scene(root, 950, 600));
        stage.show();
    }

    private void refrescarTabla() {
        data.setAll(inventario.obtenerTodos());
    }

    public static void main(String[] args) { launch(); }
}