package cr.ac.cenfotec.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;

import java.io.File;

import cr.ac.cenfotec.bl.logic.ListaProductos;
import cr.ac.cenfotec.bl.entities.Producto;

public class InventarioUI extends Application {

    private final ListaProductos lista = new ListaProductos();
    private final ObservableList<Producto> data = FXCollections.observableArrayList();
    private final TableView<Producto> table = new TableView<>();

    @Override
    public void start(Stage stage) {

        // ////////////////////////// //
        // TEXTOS EN CUADROS DE INPUT //
        // ////////////////////////// //

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");

        TextField categoriaField = new TextField();
        categoriaField.setPromptText("Categoría");

        TextField fechaField = new TextField();
        fechaField.setPromptText("Fecha de vencimiento (opcional)");

        TextField precioField = new TextField();
        precioField.setPromptText("Precio");

        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad");

        // /////////////////// //
        // CREACION DE BOTONES //
        // /////////////////// //

        Button agregarBtn = new Button("Agregar");
        Button modificarBtn = new Button("Modificar");
        Button buscarBtn = new Button("Buscar");
        Button agregarImagenBtn = new Button("Agregar imagen");

        Label status = new Label("Listo");

        // //////////////////// //
        // CREACION DE COLUMNAS //
        // //////////////////// //

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<Producto, String> colFecha = new TableColumn<>("Vence");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));

        table.getColumns().addAll(colNombre, colCategoria, colPrecio, colCantidad, colFecha);
        table.setItems(data);
        table.setPrefWidth(520);

        // /////////////////////////////// //
        // DIRECCION LOCAL DE LAS IMAGENES //
        // /////////////////////////////// //
        ListView<String> imagenesList = new ListView<>();
        imagenesList.setPrefWidth(320);
        imagenesList.setPlaceholder(new Label("Este producto no tiene imágenes"));
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(300);
        imagePreview.setFitHeight(300);
        imagePreview.setPreserveRatio(true);

        Label previewLabel = new Label("Vista previa de la imagen:");

        // ///////////////////////////////// //
        // ACTUALIZADOR DE LISTA DE IMAGENES //
        // ///////////////////////////////// //

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            imagenesList.getItems().clear();
            if (newSel != null) {
                imagenesList.getItems().addAll(newSel.getListaImagenes());
            }
        });

        // ///////////////////////////////////// //
        // MOSTRADOR DE IMAGENES EN CAJA DERECHA //
        // ///////////////////////////////////// //
        imagenesList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String ruta = imagenesList.getSelectionModel().getSelectedItem();

                if (ruta != null) {
                    File imgFile = new File(ruta);

                    if (!imgFile.exists()) {
                        new Alert(Alert.AlertType.ERROR, "El archivo no existe.").showAndWait();
                        imagePreview.setImage(null);
                        return;
                    }

                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(
                                imgFile.toURI().toString(),
                                false
                        );
                        imagePreview.setImage(img);
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "No se pudo cargar la imagen.").showAndWait();
                        imagePreview.setImage(null);
                    }
                }
            }
        });

        // //////////////////// //
        // FUNCIONES DE BOTONES //
        // //////////////////// //

        agregarBtn.setOnAction(e -> {
            try {
                Producto p = new Producto(
                        nombreField.getText(),
                        Double.parseDouble(precioField.getText()),
                        categoriaField.getText(),
                        fechaField.getText(),
                        Integer.parseInt(cantidadField.getText())
                );

                lista.insertarFinal(p);
                refrescarTabla();
                status.setText("Producto agregado ✔");
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Datos inválidos").showAndWait();
            }
        });

        modificarBtn.setOnAction(e -> {
            try {
                boolean ok = lista.modificarProducto(
                        nombreField.getText(),
                        Double.parseDouble(precioField.getText()),
                        Integer.parseInt(cantidadField.getText())
                );

                if (ok) {
                    refrescarTabla();
                    status.setText("Producto modificado ✔");
                } else {
                    status.setText("Producto no encontrado ❌");
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Datos inválidos").showAndWait();
            }
        });

        buscarBtn.setOnAction(e -> {
            Producto p = lista.buscarPorNombre(nombreField.getText());

            if (p != null) {
                categoriaField.setText(p.getCategoria());
                precioField.setText(String.valueOf(p.getPrecio()));
                cantidadField.setText(String.valueOf(p.getCantidad()));
                fechaField.setText(p.getFechaVencimiento());
                imagenesList.getItems().setAll(p.getListaImagenes());
                status.setText("Producto encontrado 🔍");
            } else {
                imagenesList.getItems().clear();
                status.setText("No existe ese producto ❌");
            }
        });

        agregarImagenBtn.setOnAction(e -> {
            String nombre = nombreField.getText();

            if (nombre == null || nombre.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Ingrese el nombre del producto primero.").showAndWait();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar imagen del producto");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                boolean ok = lista.agregarImagenAProducto(nombre, file.getAbsolutePath());
                if (ok) {
                    Producto p = lista.buscarPorNombre(nombre);
                    if (p != null) {
                        imagenesList.getItems().setAll(p.getListaImagenes());
                    }
                    status.setText("Imagen agregada ✔");
                } else {
                    status.setText("Producto no encontrado ❌");
                }
            }
        });

        // //////////////////// //
        // CAJAS DE LA INTERFAZ //
        // //////////////////// //

        VBox leftPanel = new VBox(10, table);
        VBox rightPanel = new VBox(10,
                new Label("Imágenes del producto (doble clic para ver):"),
                imagenesList,
                previewLabel,
                imagePreview
        );
        HBox center = new HBox(15, leftPanel, rightPanel);

        VBox root = new VBox(10,
                new HBox(10, nombreField, categoriaField, fechaField),
                new HBox(10, precioField, cantidadField),
                new HBox(10, agregarBtn, modificarBtn, buscarBtn, agregarImagenBtn),
                center,
                status
        );

        root.setPadding(new javafx.geometry.Insets(15));

        stage.setTitle("Sistema de Inventario");
        stage.setScene(new Scene(root, 900, 500));
        stage.show();
    }

    private void refrescarTabla() {
        data.setAll(lista.obtenerProductos());
    }

    public static void main(String[] args) {
        launch();
    }
}