package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona el inventario principal de la tienda.
 * Implementa un Árbol Binario de Búsqueda (BST) para optimizar
 * las operaciones de búsqueda y ordenamiento por nombre.
 */
public class ArbolProducto {
    private NodoProducto raiz;

    /**
     * Inserta un nuevo producto en el árbol de forma organizada.
     * @param producto Objeto Producto a almacenar.
     */
    public void insertar(Producto producto) {
        raiz = insertarRecursivo(raiz, producto);
    }

    private NodoProducto insertarRecursivo(NodoProducto actual, Producto producto) {
        if (actual == null) return new NodoProducto(producto);

        int comp = producto.getNombre().compareToIgnoreCase(actual.producto.getNombre());

        if (comp < 0) {
            actual.izq = insertarRecursivo(actual.izq, producto);
        } else if (comp > 0) {
            actual.der = insertarRecursivo(actual.der, producto);
        }
        return actual;
    }

    /**
     * Busca un producto por su nombre (llave del árbol).
     * @param nombre Nombre del producto a buscar.
     * @return El objeto Producto si existe, null en caso contrario.
     */
    public Producto buscar(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Producto buscarRecursivo(NodoProducto actual, String nombre) {
        if (actual == null) return null;

        if (nombre.equalsIgnoreCase(actual.producto.getNombre())) {
            return actual.producto;
        }

        return nombre.compareToIgnoreCase(actual.producto.getNombre()) < 0
                ? buscarRecursivo(actual.izq, nombre)
                : buscarRecursivo(actual.der, nombre);
    }

    /**
     * Elimina un producto del árbol reestructurando los nodos según sea necesario.
     * Maneja los 3 casos: nodo hoja, nodo con un hijo y nodo con dos hijos.
     * @param nombre Nombre del producto a eliminar.
     */
    public void eliminar(String nombre) {
        raiz = eliminarRecursivo(raiz, nombre);
    }

    private NodoProducto eliminarRecursivo(NodoProducto actual, String nombre) {
        if (actual == null) return null;

        int comp = nombre.compareToIgnoreCase(actual.producto.getNombre());

        if (comp < 0) {
            actual.izq = eliminarRecursivo(actual.izq, nombre);
        } else if (comp > 0) {
            actual.der = eliminarRecursivo(actual.der, nombre);
        } else {
            // Caso 1 y 2: El nodo tiene un hijo o ninguno
            if (actual.izq == null) return actual.der;
            if (actual.der == null) return actual.izq;

            // Caso 3: El nodo tiene dos hijos
            // Se busca el sucesor (mínimo del subárbol derecho)
            Producto sucesor = encontrarMinimo(actual.der);
            actual.producto = sucesor;
            // Se elimina el sucesor original
            actual.der = eliminarRecursivo(actual.der, sucesor.getNombre());
        }
        return actual;
    }

    private Producto encontrarMinimo(NodoProducto nodo) {
        return nodo.izq == null ? nodo.producto : encontrarMinimo(nodo.izq);
    }

    /**
     * Genera una lista de productos ordenada alfabéticamente.
     * Utiliza un recorrido In-Order (Izquierda - Raíz - Derecha).
     * @return Lista de productos para mostrar en la UI.
     */
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        recorridoInOrder(raiz, lista);
        return lista;
    }

    private void recorridoInOrder(NodoProducto actual, List<Producto> lista) {
        if (actual != null) {
            recorridoInOrder(actual.izq, lista);
            lista.add(actual.producto);
            recorridoInOrder(actual.der, lista);
        }
    }
}