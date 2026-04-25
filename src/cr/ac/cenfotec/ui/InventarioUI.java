package cr.ac.cenfotec.ui;

import cr.ac.cenfotec.bl.entities.Cliente;
import cr.ac.cenfotec.bl.entities.Producto;
import cr.ac.cenfotec.bl.logic.Tienda;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventarioUI extends Application {
    private static final Path ESTADO_ARCHIVO = Path.of("estado_tienda.dat");

    private Tienda tienda;
    private final ObservableList<Producto> data = FXCollections.observableArrayList();
    private final TableView<Producto> table = new TableView<>();
    private final Label status = new Label("Estado: Listo");
    private final TextArea logArea = new TextArea();

    @Override
    public void start(Stage stage) {
        this.tienda = cargarEstado();
        logArea.setEditable(false);
        logArea.setPrefRowCount(10);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(crearTabInventario(), crearTabClientesDespacho(), crearTabMapa());

        VBox root = new VBox(10, tabs, status, logArea);
        root.setPadding(new Insets(12));
        refrescarTabla();

        stage.setTitle("Cenfotec - Gestión Integral (UI alternativa)");
        stage.setScene(new Scene(root, 1100, 720));
        stage.show();
    }

    private Tab crearTabInventario() {
        TextField nombreField = new TextField(); nombreField.setPromptText("Nombre");
        TextField categoriaField = new TextField(); categoriaField.setPromptText("Categoría");
        TextField fechaField = new TextField(); fechaField.setPromptText("Fecha Vencimiento");
        TextField precioField = new TextField(); precioField.setPromptText("Precio");
        TextField cantidadField = new TextField(); cantidadField.setPromptText("Cantidad");

        Button agregarBtn = new Button("Agregar");
        Button modificarBtn = new Button("Modificar");
        Button buscarBtn = new Button("Buscar");
        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setStyle("-fx-base: #ff6666;");
        Button agregarImagenBtn = new Button("Agregar imagen");

        configurarColumnasTabla();
        ListView<String> imagenesList = new ListView<>();
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(240);
        imagePreview.setPreserveRatio(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                nombreField.setText(seleccionado.getNombre());
                categoriaField.setText(seleccionado.getCategoria());
                fechaField.setText(seleccionado.getFechaVencimiento());
                precioField.setText(String.valueOf(seleccionado.getPrecio()));
                cantidadField.setText(String.valueOf(seleccionado.getCantidad()));
                imagenesList.setItems(FXCollections.observableArrayList(seleccionado.getListaImagenes()));
                imagePreview.setImage(null);
            }
        });

        imagenesList.getSelectionModel().selectedItemProperty().addListener((obs, anterior, ruta) -> {
            if (ruta != null && !ruta.isBlank()) {
                try {
                    Image imagen = ruta.startsWith("http://") || ruta.startsWith("https://")
                            ? new Image(ruta)
                            : new Image("file:" + ruta);
                    imagePreview.setImage(imagen);
                    status.setText("Vista previa de imagen cargada");
                } catch (Exception ex) {
                    imagePreview.setImage(null);
                    status.setText("No se pudo cargar la imagen seleccionada");
                }
            }
        });

        agregarBtn.setOnAction(e -> {
            try {
                String nombre = noVacio(nombreField.getText(), "El nombre no puede estar vacío");
                String categoria = noVacio(categoriaField.getText(), "La categoría no puede estar vacía");
                double precio = parseDoublePositivo(precioField.getText(), "Precio inválido");
                int cantidad = parseIntPositivo(cantidadField.getText(), "Cantidad inválida");
                String fecha = fechaField.getText() == null ? "" : fechaField.getText().trim();

                tienda.getInventario().insertar(new Producto(nombre, precio, categoria, fecha, cantidad));
                refrescarTabla();
                status.setText("Producto agregado al inventario");
                log("Inventario: agregado '" + nombre + "'.");
            } catch (Exception ex) {
                status.setText("Error al agregar: " + ex.getMessage());
            }
        });

        buscarBtn.setOnAction(e -> {
            Producto p = tienda.getInventario().buscar(nombreField.getText());
            if (p != null) {
                precioField.setText(String.valueOf(p.getPrecio()));
                categoriaField.setText(p.getCategoria());
                fechaField.setText(p.getFechaVencimiento());
                cantidadField.setText(String.valueOf(p.getCantidad()));
                imagenesList.setItems(FXCollections.observableArrayList(p.getListaImagenes()));
                status.setText("Producto encontrado");
            } else {
                status.setText("Producto no encontrado");
            }
        });

        eliminarBtn.setOnAction(e -> {
            try {
                String nombre = noVacio(nombreField.getText(), "Debe indicar el producto a eliminar");
                Producto existente = tienda.getInventario().buscar(nombre);
                if (existente == null) {
                    status.setText("No existe ese producto");
                    return;
                }
                tienda.getInventario().eliminar(nombre);
                refrescarTabla();
                imagenesList.getItems().clear();
                imagePreview.setImage(null);
                status.setText("Producto eliminado");
                log("Inventario: eliminado '" + nombre + "'.");
            } catch (Exception ex) {
                status.setText("Error al eliminar: " + ex.getMessage());
            }
        });

        modificarBtn.setOnAction(e -> {
            try {
                String nombre = noVacio(nombreField.getText(), "Debe indicar el producto a modificar");
                Producto existente = tienda.getInventario().buscar(nombre);
                if (existente == null) {
                    status.setText("No se encontró el producto");
                    return;
                }
                existente.setCategoria(noVacio(categoriaField.getText(), "Categoría inválida"));
                existente.setFechaVencimiento(fechaField.getText() == null ? "" : fechaField.getText().trim());
                existente.setPrecio(parseDoublePositivo(precioField.getText(), "Precio inválido"));
                existente.setCantidad(parseIntPositivo(cantidadField.getText(), "Cantidad inválida"));
                refrescarTabla();
                status.setText("Producto actualizado");
                log("Inventario: actualizado '" + nombre + "'.");
            } catch (Exception ex) {
                status.setText("Error al modificar: " + ex.getMessage());
            }
        });

        agregarImagenBtn.setOnAction(e -> {
            try {
                String nombre = noVacio(nombreField.getText(), "Debe indicar el producto");
                Producto existente = tienda.getInventario().buscar(nombre);
                if (existente == null) {
                    status.setText("Producto no encontrado");
                    return;
                }
                TextInputDialog dialogo = new TextInputDialog();
                dialogo.setTitle("Agregar imagen");
                dialogo.setHeaderText("Ruta local o URL");
                dialogo.setContentText("Imagen:");
                dialogo.showAndWait().ifPresent(ruta -> {
                    if (ruta != null && !ruta.trim().isBlank()) {
                        existente.getListaImagenes().add(ruta.trim());
                        imagenesList.setItems(FXCollections.observableArrayList(existente.getListaImagenes()));
                        status.setText("Imagen agregada");
                        log("Inventario: imagen agregada a '" + nombre + "'.");
                    }
                });
            } catch (Exception ex) {
                status.setText("Error al agregar imagen: " + ex.getMessage());
            }
        });

        HBox inputs = new HBox(10, nombreField, categoriaField, precioField, cantidadField, fechaField);
        HBox botones = new HBox(10, agregarBtn, modificarBtn, buscarBtn, eliminarBtn, agregarImagenBtn);
        VBox contenido = new VBox(12, inputs, botones, new HBox(12, table, imagenesList, imagePreview));
        contenido.setPadding(new Insets(8));
        return new Tab("Inventario", contenido);
    }

    private Tab crearTabClientesDespacho() {
        TextField idField = new TextField(); idField.setPromptText("ID");
        TextField nombreField = new TextField(); nombreField.setPromptText("Nombre");
        TextField ubicacionField = new TextField(); ubicacionField.setPromptText("Ubicación");
        ComboBox<Integer> prioridadBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3));
        prioridadBox.setPromptText("Prioridad");

        Button registrarSinCompraBtn = new Button("Registrar sin compra");
        Button registrarConCompraBtn = new Button("Registrar con compra");
        Button atenderBtn = new Button("Atender y despachar");

        registrarSinCompraBtn.setOnAction(e -> {
            try {
                Cliente cliente = construirCliente(idField, nombreField, prioridadBox, ubicacionField);
                tienda.getColaAtencion().encolar(cliente);
                status.setText("Cliente registrado en cola");
                log("Clientes: encolado " + cliente.getNombre() + " (sin compra).");
                limpiarCamposCliente(idField, nombreField, ubicacionField, prioridadBox);
            } catch (Exception ex) {
                status.setText("Error al registrar cliente: " + ex.getMessage());
            }
        });

        registrarConCompraBtn.setOnAction(e -> {
            try {
                Cliente cliente = construirCliente(idField, nombreField, prioridadBox, ubicacionField);
                boolean comprando = gestionarCompraDialog(cliente);
                if (!comprando) {
                    status.setText("No se agregó compra. Cliente no encolado.");
                    return;
                }
                tienda.getColaAtencion().encolar(cliente);
                status.setText("Cliente con compra encolado");
                log("Clientes: encolado " + cliente.getNombre() + " con compra.");
                limpiarCamposCliente(idField, nombreField, ubicacionField, prioridadBox);
                refrescarTabla();
            } catch (Exception ex) {
                status.setText("Error al registrar compra: " + ex.getMessage());
            }
        });

        atenderBtn.setOnAction(e -> atenderClienteDesdeUI());

        HBox fila1 = new HBox(10, idField, nombreField, prioridadBox, ubicacionField);
        HBox fila2 = new HBox(10, registrarSinCompraBtn, registrarConCompraBtn, atenderBtn);
        VBox contenido = new VBox(12, fila1, fila2);
        contenido.setPadding(new Insets(8));
        return new Tab("Clientes y despacho", contenido);
    }

    private Tab crearTabMapa() {
        TextField verticeField = new TextField(); verticeField.setPromptText("Nueva ubicación");
        Button addVerticeBtn = new Button("Agregar vértice");

        TextField aField = new TextField(); aField.setPromptText("Punto A");
        TextField bField = new TextField(); bField.setPromptText("Punto B");
        TextField distField = new TextField(); distField.setPromptText("Distancia");
        Button addAristaBtn = new Button("Conectar");
        Button verVerticesBtn = new Button("Ver ubicaciones");

        addVerticeBtn.setOnAction(e -> {
            try {
                String ubicacion = noVacio(verticeField.getText(), "Ubicación vacía");
                tienda.getMapaLogistico().agregarVertice(ubicacion);
                status.setText("Ubicación agregada");
                log("Mapa: vértice '" + ubicacion + "' agregado.");
                verticeField.clear();
            } catch (Exception ex) {
                status.setText("Error en vértice: " + ex.getMessage());
            }
        });

        addAristaBtn.setOnAction(e -> {
            try {
                String a = noVacio(aField.getText(), "Punto A vacío");
                String b = noVacio(bField.getText(), "Punto B vacío");
                int d = parseIntPositivo(distField.getText(), "Distancia inválida");
                tienda.getMapaLogistico().agregarArista(a, b, d);
                status.setText("Conexión establecida");
                log("Mapa: conexión '" + a + "' <-> '" + b + "' (" + d + " km).");
                aField.clear();
                bField.clear();
                distField.clear();
            } catch (Exception ex) {
                status.setText("Error en conexión: " + ex.getMessage());
            }
        });

        verVerticesBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ubicaciones");
            alert.setHeaderText("Vértices registrados");
            alert.setContentText(tienda.getMapaLogistico().getVertices().toString());
            alert.showAndWait();
        });

        HBox fila1 = new HBox(10, verticeField, addVerticeBtn);
        HBox fila2 = new HBox(10, aField, bField, distField, addAristaBtn, verVerticesBtn);
        VBox contenido = new VBox(12, fila1, fila2);
        contenido.setPadding(new Insets(8));
        return new Tab("Mapa logístico", contenido);
    }

    private void configurarColumnasTabla() {
        if (!table.getColumns().isEmpty()) {
            return;
        }
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        table.getColumns().addAll(colNombre, colPrecio, colCantidad);
        table.setItems(data);
    }

    private void atenderClienteDesdeUI() {
        if (tienda.getColaAtencion().estaVacia()) {
            status.setText("No hay clientes en cola.");
            return;
        }
        Cliente proximo = tienda.getColaAtencion().verSiguiente();
        Map<String, Object> rutaInfo = tienda.getMapaLogistico().calcularRutaCorta(
                tienda.getUbicacionSede(),
                proximo.getUbicacion()
        );
        if (rutaInfo == null) {
            status.setText("No existe ruta para " + proximo.getUbicacion());
            return;
        }

        Cliente actual = tienda.getColaAtencion().atender();
        String tipo = (actual.getPrioridad() == 3) ? "PREMIUM" : (actual.getPrioridad() == 2) ? "AFILIADO" : "BÁSICO";
        String contenido = "Cliente: " + actual.getNombre() + " (" + tipo + ")\n" +
                "ID: " + actual.getId() + "\n" +
                "Ruta: " + rutaInfo.get("camino") + "\n" +
                "Distancia: " + rutaInfo.get("distancia") + " km";

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Despacho completado");
        alerta.setHeaderText("Factura y ruta");
        alerta.setContentText(contenido);
        alerta.showAndWait();

        status.setText("Cliente atendido y despachado.");
        log("Despacho: " + actual.getNombre() + " | " + rutaInfo.get("camino"));
    }

    private boolean gestionarCompraDialog(Cliente cliente) {
        if (tienda.getInventario().obtenerTodos().isEmpty()) {
            return false;
        }

        boolean agrego = false;
        while (true) {
            List<String> nombres = tienda.getInventario().obtenerTodos().stream().map(Producto::getNombre).toList();
            if (nombres.isEmpty()) {
                break;
            }

            ChoiceDialog<String> selector = new ChoiceDialog<>(nombres.get(0), nombres);
            selector.setTitle("Carrito");
            selector.setHeaderText("Seleccione producto para " + cliente.getNombre());
            selector.setContentText("Producto:");
            Optional<String> elegido = selector.showAndWait();
            if (elegido.isEmpty()) {
                break;
            }

            Producto p = tienda.getInventario().buscar(elegido.get());
            if (p == null) {
                continue;
            }

            TextInputDialog qtyDialog = new TextInputDialog("1");
            qtyDialog.setTitle("Cantidad");
            qtyDialog.setHeaderText("Disponibles: " + p.getCantidad());
            qtyDialog.setContentText("Cantidad:");
            Optional<String> qty = qtyDialog.showAndWait();
            if (qty.isEmpty()) {
                continue;
            }

            try {
                int cant = parseIntPositivo(qty.get(), "Cantidad inválida");
                if (cant > p.getCantidad()) {
                    status.setText("Stock insuficiente para '" + p.getNombre() + "'.");
                    continue;
                }
                Producto item = new Producto(p.getNombre(), p.getPrecio(), p.getCategoria(), p.getFechaVencimiento(), cant);
                cliente.getCarrito().insertarFinal(item);
                p.setCantidad(p.getCantidad() - cant);
                agrego = true;
            } catch (Exception ex) {
                status.setText(ex.getMessage());
            }

            Alert continuar = new Alert(Alert.AlertType.CONFIRMATION);
            continuar.setTitle("Continuar");
            continuar.setHeaderText("Agregar otro producto?");
            ButtonType si = new ButtonType("Sí");
            ButtonType no = new ButtonType("No");
            continuar.getButtonTypes().setAll(si, no);
            Optional<ButtonType> res = continuar.showAndWait();
            if (res.isEmpty() || res.get() == no) {
                break;
            }
        }
        return agrego;
    }

    private Cliente construirCliente(TextField idField, TextField nombreField, ComboBox<Integer> prioridadBox, TextField ubicacionField) {
        String id = noVacio(idField.getText(), "ID vacío");
        String nombre = noVacio(nombreField.getText(), "Nombre vacío");
        Integer prioridad = prioridadBox.getValue();
        if (prioridad == null || prioridad < 1 || prioridad > 3) {
            throw new IllegalArgumentException("Prioridad inválida");
        }
        String ubicacion = noVacio(ubicacionField.getText(), "Ubicación vacía");
        return new Cliente(id, nombre, prioridad, ubicacion);
    }

    private void limpiarCamposCliente(TextField idField, TextField nombreField, TextField ubicacionField, ComboBox<Integer> prioridadBox) {
        idField.clear();
        nombreField.clear();
        ubicacionField.clear();
        prioridadBox.getSelectionModel().clearSelection();
    }

    private void refrescarTabla() {
        data.setAll(tienda.getInventario().obtenerTodos());
    }

    @Override
    public void stop() {
        guardarEstado();
    }

    private Tienda cargarEstado() {
        if (!Files.exists(ESTADO_ARCHIVO)) {
            return new Tienda();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(ESTADO_ARCHIVO))) {
            Object obj = in.readObject();
            if (obj instanceof Tienda tiendaCargada) {
                return tiendaCargada;
            }
        } catch (Exception e) {
            log("No se pudo cargar estado previo: " + e.getMessage());
        }
        return new Tienda();
    }

    private void guardarEstado() {
        if (tienda == null) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(ESTADO_ARCHIVO))) {
            out.writeObject(tienda);
        } catch (Exception e) {
            log("No se pudo guardar estado: " + e.getMessage());
        }
    }

    private void log(String mensaje) {
        logArea.appendText(mensaje + "\n");
    }

    private static String noVacio(String valor, String mensajeError) {
        if (valor == null || valor.trim().isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor.trim();
    }

    private static int parseIntPositivo(String valor, String mensajeError) {
        try {
            int numero = Integer.parseInt(valor.trim());
            if (numero <= 0) {
                throw new IllegalArgumentException(mensajeError);
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private static double parseDoublePositivo(String valor, String mensajeError) {
        try {
            double numero = Double.parseDouble(valor.trim());
            if (numero <= 0) {
                throw new IllegalArgumentException(mensajeError);
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}