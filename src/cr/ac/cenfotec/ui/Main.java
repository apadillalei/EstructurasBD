package cr.ac.cenfotec.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cr.ac.cenfotec.bl.entities.Producto;
import cr.ac.cenfotec.bl.logic.ListaProductos;

public class Main {

    private static ListaProductos lista = new ListaProductos();
    private static BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {

        int opcion = -1;

        do {
            try {

                System.out.println("\n ===== TIENDA DE ÚTILES ESCOLARES ===== ");
                System.out.println("1. Agregar producto al inicio");
                System.out.println("2. Agregar producto al final");
                System.out.println("3. Mostrar productos");
                System.out.println("4. Modificar producto");
                System.out.println("5. Agregar imagen a producto");
                System.out.println("6. Reporte de costos");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                opcion = Integer.parseInt(br.readLine());

                switch (opcion) {
                    case 1:
                        agregarProducto(true);
                        break;
                    case 2:
                        agregarProducto(false);
                        break;
                    case 3:
                        lista.mostrarProductos();
                        break;
                    case 4:
                        modificarProducto();
                        break;
                    case 5:
                        agregarImagen();
                        break;
                    case 6:
                        lista.imprimirReporteCostos();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }

            } catch (IOException e) {
                System.out.println("Error de entrada de datos.");
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }

        } while (opcion != 0);
    }


    // AGREGAR PRODUCTO
    private static void agregarProducto(boolean esInicio) throws IOException {

        System.out.print("Nombre: ");
        String nombre = br.readLine();

        System.out.print("Precio: ");
        double precio = Double.parseDouble(br.readLine());

        System.out.print("Cantidad: ");
        int cantidad = Integer.parseInt(br.readLine());

        System.out.print("Categoría: ");
        String categoria = br.readLine();

        System.out.print("Fecha de vencimiento (vacío si no aplica): ");
        String fecha = br.readLine();

        Producto producto = new Producto(
                nombre,
                precio,
                categoria,
                fecha,
                cantidad
        );

        if (esInicio) {
            lista.insertarInicio(producto);
        } else {
            lista.insertarFinal(producto);
        }

        System.out.println("Producto agregado correctamente.");
    }


    // MODIFICAR PRODUCTO
    private static void modificarProducto() throws IOException {

        System.out.print("Ingrese nombre del producto a modificar: ");
        String nombre = br.readLine();

        System.out.print("Nuevo precio: ");
        double precio = Double.parseDouble(br.readLine());

        System.out.print("Nueva cantidad: ");
        int cantidad = Integer.parseInt(br.readLine());

        boolean modificado =
                lista.modificarProducto(nombre, precio, cantidad);

        if (modificado) {
            System.out.println("Producto modificado correctamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }


    // AGREGAR IMAGEN
    private static void agregarImagen() throws IOException {

        System.out.print("Ingrese nombre del producto: ");
        String nombre = br.readLine();

        System.out.print("Ruta de la imagen: ");
        String ruta = br.readLine();

        boolean agregado =
                lista.agregarImagenAProducto(nombre, ruta);

        if (agregado) {
            System.out.println("Imagen agregada correctamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }
}
