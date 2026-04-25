package cr.ac.cenfotec.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import cr.ac.cenfotec.bl.entities.Producto;
import cr.ac.cenfotec.bl.entities.Cliente;
import cr.ac.cenfotec.bl.logic.Tienda;

/**
 * Clase principal que actúa como la interfaz de usuario (Consola).
 * Orquesta la interacción entre el usuario y la lógica de negocio de la tienda,
 * gestionando el flujo de inventario, registro de clientes, ventas y logística de despacho.
 */
public class Main {

    private static final Path ESTADO_ARCHIVO = Path.of("estado_tienda.dat");
    private static Tienda tienda;
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Punto de entrada de la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        tienda = cargarEstado();
        menu();
    }

    /**
     * Gestiona el ciclo de vida principal de la aplicación mediante un menú interactivo.
     * Implementa manejo de excepciones para garantizar la estabilidad ante entradas inválidas.
     */
    public static void menu() {
        int opcion = -1;
        do {
            try {
                System.out.println("\n ===== SISTEMA CENTRAL DE VENTAS Y LOGÍSTICA (FINAL) ===== ");
                System.out.println("1. Registrar producto en inventario");
                System.out.println("2. Ver catálogo de productos");
                System.out.println("3. Registro de clientes y compras");
                System.out.println("4. Atender y Despachar (Facturación + Dijkstra)");
                System.out.println("5. Retirar producto de inventario");
                System.out.println("6. Gestionar Mapa de Entregas (Grafo)");
                System.out.println("0. Salir");
                opcion = leerEntero("Seleccione una opción: ");

                switch (opcion) {
                    case 1: agregarProductoAlInventario(); break;
                    case 2: mostrarInventario(); break;
                    case 3: registrarCliente(); break;
                    case 4: atenderCliente(); break;
                    case 5: eliminarDelInventario(); break;
                    case 6: gestionarMapa(); break;
                    case 0:
                        guardarEstado();
                        System.out.println("Cerrando sistema...");
                        break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error de entrada/salida: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error crítico: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    /**
     * Permite la administración dinámica de la red logística.
     * Facilita la creación de vértices y aristas ponderadas en el Grafo.
     */
    private static void gestionarMapa() throws IOException {
        int subOpcion = -1;
        while (subOpcion != 0) {
            System.out.println("\n--- GESTIÓN DE RED DE ENTREGAS ---");
            System.out.println("1. Agregar nueva ubicación (Vértice)");
            System.out.println("2. Conectar ubicaciones (Arista)");
            System.out.println("3. Ver ubicaciones actuales");
            System.out.println("0. Volver al menú principal");
            subOpcion = leerEntero("Seleccione: ");

            if (subOpcion == 1) {
                String ubicacion = leerTextoNoVacio("Nombre de la ubicación: ");
                tienda.getMapaLogistico().agregarVertice(ubicacion);
                System.out.println("Ubicación añadida.");
            } else if (subOpcion == 2) {
                String a = leerTextoNoVacio("Punto A: ");
                String b = leerTextoNoVacio("Punto B: ");
                int dist = leerEntero("Distancia (Km): ");
                try {
                    tienda.getMapaLogistico().agregarArista(a, b, dist);
                    System.out.println("Conexión establecida.");
                } catch (IllegalArgumentException e) {
                    System.out.println("No se pudo crear la conexión: " + e.getMessage());
                }
            } else if (subOpcion == 3) {
                System.out.println("Puntos de entrega registrados: " + tienda.getMapaLogistico().getVertices());
            } else if (subOpcion != 0) {
                System.out.println("Opción no válida.");
            }
        }
    }

    /**
     * Registra un cliente capturando su ubicación geográfica.
     * La ubicación se integra al grafo cuando el cliente entra en la cola de atención.
     */
    private static void registrarCliente() throws IOException {
        System.out.println("\n--- REGISTRO DE CLIENTE ---");
        String id = leerTextoNoVacio("ID: ");
        String nombre = leerTextoNoVacio("Nombre: ");
        int prioridad = leerPrioridad();
        String ubicacion = leerTextoNoVacio("Ubicación de entrega: ");

        Cliente nuevo = new Cliente(id, nombre, prioridad, ubicacion);

        String respuesta = leerSiNo("¿Desea realizar una compra ahora? (s/n): ");
        if ("s".equalsIgnoreCase(respuesta)) {
            gestionarCompra(nuevo);
        } else {
            tienda.getColaAtencion().encolar(nuevo);
            System.out.println("Cliente registrado y añadido a la fila de atención.");
        }
    }

    /**
     * Procesa al siguiente cliente en la Cola de Prioridad.
     * Integra el algoritmo de Dijkstra para validar la ruta antes de la facturación.
     * Si no existe ruta, el cliente permanece en cola hasta que se resuelva la conexión.
     */
    private static void atenderCliente() {
        if (tienda.getColaAtencion().estaVacia()) {
            System.out.println("No hay clientes en fila de atención.");
            return;
        }

        // Inspección del frente de la cola (Peek) para validación logística
        Cliente proximo = tienda.getColaAtencion().verSiguiente();
        Map<String, Object> rutaInfo = tienda.getMapaLogistico().calcularRutaCorta(
                tienda.getUbicacionSede(), proximo.getUbicacion()
        );

        if (rutaInfo == null) {
            System.out.println("\nERROR LOGÍSTICO: No existe ruta hacia '" + proximo.getUbicacion() + "'.");
            System.out.println("Actualice el mapa para proceder con el despacho.");
            return;
        }

        // Ejecución de la atención tras validación exitosa
        Cliente actual = tienda.getColaAtencion().atender();
        String tipo = (actual.getPrioridad() == 3) ? "PREMIUM" : (actual.getPrioridad() == 2) ? "AFILIADO" : "BÁSICO";

        System.out.println("\n************************************************");
        System.out.println("FACTURA DE VENTA Y ORDEN DE DESPACHO");
        System.out.println("************************************************");
        System.out.println("Cliente: " + actual.getNombre() + " (" + tipo + ")");
        System.out.println("ID: " + actual.getId());
        System.out.println("------------------------------------------------");
        actual.getCarrito().imprimirReporteCostos();
        System.out.println("------------------------------------------------");
        System.out.println("LOGÍSTICA DE ENVÍO:");
        System.out.println("Ruta óptima: " + rutaInfo.get("camino"));
        System.out.println("Distancia Total: " + rutaInfo.get("distancia") + " km");
        System.out.println("************************************************\n");
    }

    private static void agregarProductoAlInventario() throws IOException {
        System.out.println("\n--- NUEVO PRODUCTO ---");
        String nombre = leerTextoNoVacio("Nombre: ");
        double precio = leerDoublePositivo("Precio: ");
        int cantidad = leerEnteroPositivo("Stock inicial: ");
        String cat = leerTextoNoVacio("Categoría: ");

        Producto p = new Producto(nombre, precio, cat, "No aplica", cantidad);
        tienda.getInventario().insertar(p);
        System.out.println("Producto registrado en el inventario.");
    }

    private static void mostrarInventario() {
        System.out.println("\n--- CATÁLOGO DE PRODUCTOS (ORDEN ALFABÉTICO) ---");
        System.out.printf("%-15s %-10s %-10s%n", "Nombre", "Precio", "Stock");
        for (Producto p : tienda.getInventario().obtenerTodos()) {
            System.out.printf("%-15s ₡%-9.2f %-10d%n", p.getNombre(), p.getPrecio(), p.getCantidad());
        }
    }

    /**
     * Gestiona la selección de productos y actualización de stock en tiempo real.
     * @param cliente El cliente que realiza la transacción.
     */
    private static void gestionarCompra(Cliente cliente) throws IOException {
        String continuar = "s";
        while (continuar.equalsIgnoreCase("s")) {
            String prodNombre = leerTextoNoVacio("Producto a comprar: ");
            Producto p = tienda.getInventario().buscar(prodNombre);

            if (p != null) {
                int cant = leerEntero("Cantidad (" + p.getCantidad() + " disponibles): ");
                if (cant > 0 && cant <= p.getCantidad()) {
                    Producto item = new Producto(p.getNombre(), p.getPrecio(), p.getCategoria(), p.getFechaVencimiento(), cant);
                    cliente.getCarrito().insertarFinal(item);
                    p.setCantidad(p.getCantidad() - cant); // Actualización en el Árbol
                    System.out.println("Agregado al carrito.");
                } else { System.out.println("Cantidad insuficiente o inválida."); }
            } else { System.out.println("Producto no encontrado."); }

            continuar = leerSiNo("¿Agregar más productos? (s/n): ");
        }
        tienda.getColaAtencion().encolar(cliente);
        System.out.println("Cliente añadido a la fila de atención por prioridad.");
    }

    private static void eliminarDelInventario() throws IOException {
        String nombre = leerTextoNoVacio("Nombre del producto a retirar del sistema: ");
        Producto existente = tienda.getInventario().buscar(nombre);
        if (existente == null) {
            System.out.println("No existe un producto con ese nombre.");
            return;
        }
        tienda.getInventario().eliminar(nombre);
        System.out.println("Producto eliminado correctamente.");
    }

    private static String leerTextoNoVacio(String mensaje) throws IOException {
        while (true) {
            System.out.print(mensaje);
            String valor = br.readLine();
            if (valor != null && !valor.trim().isBlank()) {
                return valor.trim();
            }
            System.out.println("El valor no puede estar vacío.");
        }
    }

    private static int leerEntero(String mensaje) throws IOException {
        while (true) {
            System.out.print(mensaje);
            String entrada = br.readLine();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    private static int leerEnteroPositivo(String mensaje) throws IOException {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El valor debe ser mayor que cero.");
        }
    }

    private static double leerDoublePositivo(String mensaje) throws IOException {
        while (true) {
            System.out.print(mensaje);
            String entrada = br.readLine();
            try {
                double valor = Double.parseDouble(entrada);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("El valor debe ser mayor que cero.");
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número decimal válido.");
            }
        }
    }

    private static int leerPrioridad() throws IOException {
        while (true) {
            int prioridad = leerEntero("Prioridad (1: Básico, 2: Afiliado, 3: Premium): ");
            if (prioridad >= 1 && prioridad <= 3) {
                return prioridad;
            }
            System.out.println("La prioridad debe ser 1, 2 o 3.");
        }
    }

    private static String leerSiNo(String mensaje) throws IOException {
        while (true) {
            System.out.print(mensaje);
            String respuesta = br.readLine();
            if (respuesta != null && (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("n"))) {
                return respuesta;
            }
            System.out.println("Debe responder con 's' o 'n'.");
        }
    }

    private static Tienda cargarEstado() {
        if (!Files.exists(ESTADO_ARCHIVO)) {
            return new Tienda();
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(ESTADO_ARCHIVO))) {
            Object obj = in.readObject();
            if (obj instanceof Tienda tiendaCargada) {
                System.out.println("Estado cargado desde " + ESTADO_ARCHIVO + ".");
                return tiendaCargada;
            }
            System.out.println("El archivo de estado no tiene un formato válido. Se iniciará una tienda nueva.");
        } catch (Exception e) {
            System.out.println("No se pudo cargar el estado previo: " + e.getMessage());
        }
        return new Tienda();
    }

    private static void guardarEstado() {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(ESTADO_ARCHIVO))) {
            out.writeObject(tienda);
            System.out.println("Estado guardado en " + ESTADO_ARCHIVO + ".");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el estado actual: " + e.getMessage());
        }
    }
}