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
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

/**
 * Interfaz Gráfica de Usuario (GUI) para la gestión del inventario.
 * Proporciona una visualización dinámica del Árbol Binario de Búsqueda (BST)
 * permitiendo operaciones CRUD (Crear, Leer, Actualizar, Borrar) de productos.
 */
public class InventarioUI extends Application {

    /** Instancia local del árbol que almacena los productos en memoria. */
    private final ArbolProducto inventario = new ArbolProducto();

    /** Lista observable necesaria para vincular los datos del árbol con la TableView de JavaFX. */
    private final ObservableList<Producto> data = FXCollections.observableArrayList();

    /** Componente de tabla para la visualización de los nodos del árbol. */
    private final TableView<Producto> table = new TableView<>();

    /**
     * Inicializa y configura el escenario principal de la aplicación.
     * @param stage El escenario principal proporcionado por la plataforma JavaFX.
     */
    @Override
    public void start(Stage stage) {
        // --- COMPONENTES DE ENTRADA (CONTROLES) ---
        TextField nombreField = new TextField(); nombreField.setPromptText("Nombre");
        TextField categoriaField = new TextField(); categoriaField.setPromptText("Categoría");
        TextField fechaField = new TextField(); fechaField.setPromptText("Fecha Vencimiento");
        TextField precioField = new TextField(); precioField.setPromptText("Precio");
        TextField cantidadField = new TextField(); cantidadField.setPromptText("Cantidad");

        // --- COMPONENTES DE ACCIÓN (BOTONES) ---
        Button agregarBtn = new Button("Agregar");
        Button modificarBtn = new Button("Modificar");
        Button buscarBtn = new Button("Buscar");
        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setStyle("-fx-base: #ff6666;"); // Estilo visual para operaciones destructivas
        Button agregarImagenBtn = new Button("Agregar imagen");

        Label status = new Label("Estado: Listo");

        // --- CONFIGURACIÓN DE COLUMNAS DE LA TABLA ---
        // Se utiliza reflexión (PropertyValueFactory) para vincular atributos del objeto Producto.
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        table.getColumns().addAll(colNombre, colPrecio, colCantidad);
        table.setItems(data);

        // --- COMPONENTES VISUALES ADICIONALES ---
        ListView<String> imagenesList = new ListView<>();
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(200); imagePreview.setPreserveRatio(true);

        // --- LÓGICA DE CONTROLADORES DE EVENTOS ---

        /**
         * Maneja la inserción de un nuevo nodo en el Árbol Producto.
         * Actualiza la tabla automáticamente tras la inserción exitosa.
         */
        agregarBtn.setOnAction(e -> {
            try {
                Producto p = new Producto(nombreField.getText(), Double.parseDouble(precioField.getText()),
                        categoriaField.getText(), fechaField.getText(), Integer.parseInt(cantidadField.getText()));
                inventario.insertar(p);
                refrescarTabla();
                status.setText("Producto agregado al árbol");
            } catch (Exception ex) {
                status.setText("Error: Datos inválidos");
            }
        });

        /**
         * Realiza una búsqueda en el árbol utilizando el nombre como llave.
         * Despliega la información encontrada en los campos de texto.
         */
        buscarBtn.setOnAction(e -> {
            Producto p = inventario.buscar(nombreField.getText());
            if (p != null) {
                precioField.setText(String.valueOf(p.getPrecio()));
                categoriaField.setText(p.getCategoria());
                status.setText("Producto encontrado");
            } else {
                status.setText("No encontrado");
            }
        });

        /**
         * Remueve un nodo del árbol basándose en el nombre proporcionado.
         * Sincroniza la vista de la tabla tras la eliminación.
         */
        eliminarBtn.setOnAction(e -> {
            String nombre = nombreField.getText();
            if (!nombre.isBlank()) {
                inventario.eliminar(nombre);
                refrescarTabla();
                status.setText("Producto '" + nombre + "' eliminado ✔");
            }
        });

        // --- ORGANIZACIÓN DEL LAYOUT ---
        HBox inputs = new HBox(10, nombreField, categoriaField, precioField, cantidadField);
        HBox botones = new HBox(10, agregarBtn, modificarBtn, buscarBtn, eliminarBtn, agregarImagenBtn);
        VBox root = new VBox(15, inputs, botones, new HBox(15, table, imagenesList, imagePreview), status);
        root.setPadding(new Insets(20));

        // --- CONFIGURACIÓN DE LA ESCENA ---
        stage.setTitle("Cenfotec - Gestión de Inventario (Árbol BST)");
        stage.setScene(new Scene(root, 950, 600));
        stage.show();
    }

    /**
     * Sincroniza la colección observable con los datos actuales del árbol.
     * Utiliza el recorrido In-Order del BST para garantizar que la tabla se vea ordenada.
     */
    private void refrescarTabla() {
        data.setAll(inventario.obtenerTodos());
    }

    /**
     * Lanza la aplicación de JavaFX.
     * @param args Argumentos de configuración inicial.
     */
    public static void main(String[] args) {
        launch();
    }
}