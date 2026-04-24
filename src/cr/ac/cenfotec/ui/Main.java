package cr.ac.cenfotec.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static Tienda tienda = new Tienda();
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Punto de entrada de la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
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
                System.out.print("Seleccione una opción: ");

                opcion = Integer.parseInt(br.readLine());

                switch (opcion) {
                    case 1: agregarProductoAlInventario(); break;
                    case 2: mostrarInventario(); break;
                    case 3: registrarCliente(); break;
                    case 4: atenderCliente(); break;
                    case 5: eliminarDelInventario(); break;
                    case 6: gestionarMapa(); break;
                    case 0: System.out.println("Cerrando sistema..."); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
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
        System.out.println("\n--- GESTIÓN DE RED DE ENTREGAS ---");
        System.out.println("1. Agregar nueva ubicación (Vértice)");
        System.out.println("2. Conectar ubicaciones (Arista)");
        System.out.println("3. Ver ubicaciones actuales");
        System.out.print("Seleccione: ");
        int subOpcion = Integer.parseInt(br.readLine());

        if (subOpcion == 1) {
            System.out.print("Nombre de la ubicación: ");
            tienda.getMapaLogistico().agregarVertice(br.readLine());
            System.out.println("Ubicación añadida.");
        } else if (subOpcion == 2) {
            System.out.print("Punto A: "); String a = br.readLine();
            System.out.print("Punto B: "); String b = br.readLine();
            System.out.print("Distancia (Km): "); int dist = Integer.parseInt(br.readLine());
            tienda.getMapaLogistico().agregarArista(a, b, dist);
            System.out.println("Conexión establecida.");
        } else if (subOpcion == 3) {
            System.out.println("Puntos de entrega registrados: " + tienda.getMapaLogistico().getVertices());
        }
    }

    /**
     * Registra un cliente capturando su ubicación geográfica.
     * Garantiza que la ubicación del cliente exista como vértice en el sistema logístico.
     */
    private static void registrarCliente() throws IOException {
        System.out.println("\n--- REGISTRO DE CLIENTE ---");
        System.out.print("ID: "); String id = br.readLine();
        System.out.print("Nombre: "); String nombre = br.readLine();
        System.out.print("Prioridad (1: Básico, 2: Afiliado, 3: Premium): ");
        int prioridad = Integer.parseInt(br.readLine());
        System.out.print("Ubicación de entrega: "); String ubicacion = br.readLine();

        // Sincronización automática con el Grafo
        tienda.getMapaLogistico().agregarVertice(ubicacion);

        Cliente nuevo = new Cliente(id, nombre, prioridad, ubicacion);

        System.out.print("¿Desea realizar una compra ahora? (s/n): ");
        if (br.readLine().equalsIgnoreCase("s")) {
            gestionarCompra(nuevo);
        } else {
            System.out.println("Cliente registrado exitosamente.");
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
        System.out.print("Nombre: "); String nombre = br.readLine();
        System.out.print("Precio: "); double precio = Double.parseDouble(br.readLine());
        System.out.print("Stock inicial: "); int cantidad = Integer.parseInt(br.readLine());
        System.out.print("Categoría: "); String cat = br.readLine();

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
            System.out.print("Producto a comprar: ");
            String prodNombre = br.readLine();
            Producto p = tienda.getInventario().buscar(prodNombre);

            if (p != null) {
                System.out.print("Cantidad (" + p.getCantidad() + " disponibles): ");
                int cant = Integer.parseInt(br.readLine());
                if (cant > 0 && cant <= p.getCantidad()) {
                    Producto item = new Producto(p.getNombre(), p.getPrecio(), p.getCategoria(), p.getFechaVencimiento(), cant);
                    cliente.getCarrito().insertarFinal(item);
                    p.setCantidad(p.getCantidad() - cant); // Actualización en el Árbol
                    System.out.println("Agregado al carrito.");
                } else { System.out.println("Cantidad insuficiente o inválida."); }
            } else { System.out.println("Producto no encontrado."); }

            System.out.print("¿Agregar más productos? (s/n): ");
            continuar = br.readLine();
        }
        tienda.getColaAtencion().encolar(cliente);
        System.out.println("Cliente añadido a la fila de atención por prioridad.");
    }

    private static void eliminarDelInventario() throws IOException {
        System.out.print("Nombre del producto a retirar del sistema: ");
        tienda.getInventario().eliminar(br.readLine());
        System.out.println("Operación completada.");
    }
}