package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa el carrito de compras de cada Cliente.
 * Implementada como una Lista Simple Enlazada.
 */
public class ListaProductos {

    private NodoLista cabeza;

    public ListaProductos() {
        cabeza = null;
    }

    // INSERTAR AL INICIO
    public void insertarInicio(Producto producto) {
        NodoLista nuevo = new NodoLista(producto);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
    }

    // INSERTAR AL FINAL (Usado para llenar el carrito)
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

    // BUSCAR PRODUCTO POR NOMBRE
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

    // MODIFICAR PRODUCTO
    public boolean modificarProducto(String nombre, double nuevoPrecio, int nuevaCantidad) {
        Producto producto = buscarPorNombre(nombre);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
            producto.setCantidad(nuevaCantidad);
            return true;
        }
        return false;
    }

    // MOSTRAR PRODUCTOS
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

    // REPORTE DE COSTOS
    public void imprimirReporteCostos() {
        NodoLista actual = cabeza;
        double totalGeneral = 0;

        if (cabeza == null) {
            System.out.println("No hay productos en el carrito.");
            return;
        }

        while (actual != null) {
            double totalProducto = actual.producto.getPrecio() * actual.producto.getCantidad();
            totalGeneral += totalProducto;

            System.out.println("- " + actual.producto.getNombre() +
                    " | Cant: " + actual.producto.getCantidad() +
                    " | Subtotal: ₡" + totalProducto);
            actual = actual.siguiente;
        }

        System.out.println("================================");
        System.out.println("TOTAL A PAGAR: ₡" + totalGeneral);
        System.out.println("================================");
    }

    // método para sincronizar con la tabla de JavaFX
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