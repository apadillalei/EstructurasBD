package cr.ac.cenfotec.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import cr.ac.cenfotec.bl.entities.Producto;
import cr.ac.cenfotec.bl.entities.Cliente;
import cr.ac.cenfotec.bl.logic.Tienda;

public class Main {

    private static Tienda tienda = new Tienda();
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        int opcion = -1;
        do {
            try {
                System.out.println("\n ===== SISTEMA DE GESTIÓN DE VENTAS Y TIENDA ===== ");
                System.out.println("1. Registrar producto en inventario");
                System.out.println("2. Ver catálogo de productos");
                System.out.println("3. Registro de clientes y compras");
                System.out.println("4. Atender siguiente cliente en fila");
                System.out.println("5. Retirar producto de inventario");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                opcion = Integer.parseInt(br.readLine());

                switch (opcion) {
                    case 1: agregarProductoAlInventario(); break;
                    case 2: mostrarInventario(); break;
                    case 3: registrarCliente(); break;
                    case 4: atenderCliente(); break;
                    case 5: eliminarDelInventario(); break;
                    case 0: System.out.println("Cerrando sistema..."); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void agregarProductoAlInventario() throws IOException {
        System.out.println("\n--- NUEVO PRODUCTO ---");
        System.out.print("Nombre: "); String nombre = br.readLine();
        System.out.print("Precio: "); double precio = Double.parseDouble(br.readLine());
        System.out.print("Stock inicial: "); int cantidad = Integer.parseInt(br.readLine());
        System.out.print("Categoría: "); String cat = br.readLine();

        Producto p = new Producto(nombre, precio, cat, "No aplica", cantidad);
        tienda.getInventario().insertar(p);
        System.out.println("Producto registrado exitosamente.");
    }

    private static void mostrarInventario() {
        System.out.println("\n--- CATÁLOGO DE PRODUCTOS ---");
        System.out.printf("%-15s %-10s %-10s%n", "Nombre", "Precio", "Stock");
        for (Producto p : tienda.getInventario().obtenerTodos()) {
            System.out.printf("%-15s ₡%-9.2f %-10d%n", p.getNombre(), p.getPrecio(), p.getCantidad());
        }
    }

    private static void registrarCliente() throws IOException {
        System.out.println("\n--- REGISTRO DE CLIENTE ---");
        System.out.print("ID: "); String id = br.readLine();
        System.out.print("Nombre: "); String nombre = br.readLine();
        System.out.print("Prioridad (1: Básico, 2: Afiliado, 3: Premium): ");
        int prioridad = Integer.parseInt(br.readLine());

        if (prioridad < 1 || prioridad > 3) {
            System.out.println("Prioridad inválida.");
            return;
        }

        Cliente nuevo = new Cliente(id, nombre, prioridad);

        System.out.print("¿Desea realizar una compra ahora? (s/n): ");
        if (br.readLine().equalsIgnoreCase("s")) {
            gestionarCompra(nuevo);
        } else {
            System.out.println("Cliente registrado en el sistema.");
        }
    }

    private static void gestionarCompra(Cliente cliente) throws IOException {
        String continuar = "s";
        System.out.println("\n--- CARRITO DE COMPRAS: " + cliente.getNombre().toUpperCase() + " ---");

        while (continuar.equalsIgnoreCase("s")) {
            System.out.print("Nombre del producto: ");
            String prodNombre = br.readLine();

            Producto p = tienda.getInventario().buscar(prodNombre);

            if (p != null) {
                System.out.println("Disponible: " + p.getNombre() + " | Stock: " + p.getCantidad());
                System.out.print("Cantidad: ");
                int cantPedida = Integer.parseInt(br.readLine());

                if (cantPedida > 0 && cantPedida <= p.getCantidad()) {
                    Producto itemCompra = new Producto(p.getNombre(), p.getPrecio(), p.getCategoria(), p.getFechaVencimiento(), cantPedida);
                    cliente.getCarrito().insertarFinal(itemCompra);
                    p.setCantidad(p.getCantidad() - cantPedida);
                    System.out.println("Agregado.");
                } else {
                    System.out.println("Stock insuficiente.");
                }
            } else {
                System.out.println("Producto no disponible.");
            }

            System.out.print("¿Seguir comprando? (s/n): ");
            continuar = br.readLine();
        }

        tienda.getColaAtencion().encolar(cliente);
        System.out.println("Compra finalizada. Cliente en fila de espera.");
    }

    private static void atenderCliente() {
        if (tienda.getColaAtencion().estaVacia()) {
            System.out.println("No hay clientes en fila.");
            return;
        }

        Cliente actual = tienda.getColaAtencion().atender();
        String tipo = (actual.getPrioridad() == 3) ? "PREMIUM" : (actual.getPrioridad() == 2) ? "AFILIADO" : "BÁSICO";

        System.out.println("\n************************************");
        System.out.println("FACTURA DE VENTA - " + tipo);
        System.out.println("Cliente: " + actual.getNombre() + " (ID: " + actual.getId() + ")");
        System.out.println("************************************");
        actual.getCarrito().imprimirReporteCostos();
    }

    private static void eliminarDelInventario() throws IOException {
        System.out.print("Nombre del producto a retirar: ");
        String nombre = br.readLine();
        tienda.getInventario().eliminar(nombre);
        System.out.println("Producto retirado del sistema.");
    }
}