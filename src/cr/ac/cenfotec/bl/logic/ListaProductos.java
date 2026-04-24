package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el carrito de compras individual de cada cliente.
 * Implementada como una estructura de datos de Lista Simple Enlazada,
 * permitiendo un almacenamiento dinámico de los productos seleccionados.
 */
public class ListaProductos {

    private NodoLista cabeza;

    /**
     * Constructor que inicializa una lista vacía.
     */
    public ListaProductos() {
        cabeza = null;
    }

    /**
     * Inserta un producto al inicio de la lista.
     * @param producto Objeto Producto a añadir.
     */
    public void insertarInicio(Producto producto) {
        NodoLista nuevo = new NodoLista(producto);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
    }

    /**
     * Inserta un producto al final de la lista.
     * Este método es el estándar para mantener el orden de selección en el carrito.
     * @param producto Objeto Producto a añadir.
     */
    public void insertarFinal(Producto producto) {
        NodoLista nuevo = new NodoLista(producto);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoLista actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    /**
     * Realiza una búsqueda secuencial de un producto por su nombre.
     * @param nombre Nombre del producto buscado.
     * @return El objeto Producto si se encuentra, null en caso contrario.
     */
    public Producto buscarPorNombre(String nombre) {
        NodoLista actual = cabeza;
        while (actual != null) {
            if (actual.producto.getNombre().equalsIgnoreCase(nombre)) {
                return actual.producto;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    /**
     * Actualiza los valores de precio y cantidad de un producto existente.
     * @param nombre Nombre del producto a modificar.
     * @param nuevoPrecio Nuevo valor unitario.
     * @param nuevaCantidad Nueva cantidad de unidades.
     * @return true si la modificación fue exitosa, false si el producto no existe.
     */
    public boolean modificarProducto(String nombre, double nuevoPrecio, int nuevaCantidad) {
        Producto producto = buscarPorNombre(nombre);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
            producto.setCantidad(nuevaCantidad);
            return true;
        }
        return false;
    }

    /**
     * Imprime en consola todos los productos contenidos en el carrito.
     */
    public void mostrarProductos() {
        NodoLista actual = cabeza;
        if (cabeza == null) {
            System.out.println("El carrito está vacío.");
            return;
        }
        while (actual != null) {
            System.out.println(actual.producto);
            actual = actual.siguiente;
        }
    }

    /**
     * Genera un reporte detallado y elegante de los costos del carrito.
     * Calcula subtotales por producto y el total general acumulado.
     * Utiliza formato tabular para mejorar la legibilidad.
     */
    public void imprimirReporteCostos() {
        NodoLista actual = cabeza;
        double totalGeneral = 0;

        if (cabeza == null) {
            System.out.println("No hay productos en el carrito.");
            return;
        }

        System.out.printf("%-20s %-10s %-15s %-15s%n", "PRODUCTO", "CANT.", "PRECIO UNIT.", "SUBTOTAL");
        System.out.println("------------------------------------------------------------------");

        while (actual != null) {
            double totalProducto = actual.producto.getPrecio() * actual.producto.getCantidad();
            totalGeneral += totalProducto;

            System.out.printf("%-20s %-10d ₡%-14.2f ₡%-14.2f%n",
                    actual.producto.getNombre(),
                    actual.producto.getCantidad(),
                    actual.producto.getPrecio(),
                    totalProducto);

            actual = actual.siguiente;
        }

        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-47s TOTAL A PAGAR: ₡%.2f%n", "", totalGeneral);
        System.out.println("==================================================================");
    }

    /**
     * Convierte la estructura de lista enlazada interna a una lista estándar de Java.
     * Facilita la integración con componentes de interfaz gráfica como TableView.
     * @return List de productos compatible con colecciones de Java.
     */
    public List<Producto> obtenerProductos() {
        List<Producto> listaJava = new ArrayList<>();
        NodoLista actual = cabeza;
        while (actual != null) {
            listaJava.add(actual.producto);
            actual = actual.siguiente;
        }
        return listaJava;
    }
}