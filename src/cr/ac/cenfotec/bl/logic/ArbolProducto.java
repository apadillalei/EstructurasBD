package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el inventario principal mediante un Árbol Binario de Búsqueda (BST).
 * Esta estructura permite una complejidad promedio de O(log n) para operaciones
 * de inserción, búsqueda y eliminación, utilizando el nombre del producto como llave.
 */
public class ArbolProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    private NodoProducto raiz;

    /**
     * Inserta un nuevo producto en el árbol.
     * La ubicación del nuevo nodo se determina mediante comparación alfabética.
     * * @param producto Objeto Producto con los datos a almacenar.
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
        // Los nombres duplicados no se insertan para mantener la integridad de la llave
        return actual;
    }

    /**
     * Localiza un producto específico dentro del árbol.
     * * @param nombre El nombre del producto a buscar.
     * @return El objeto Producto encontrado o null si no existe.
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
     * Elimina un nodo del árbol basándose en el nombre del producto.
     * Implementa la reestructuración por sucesor para el caso de dos hijos.
     * * @param nombre Nombre del producto que se desea remover.
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
            // Caso de eliminación: El nodo ha sido encontrado
            if (actual.izq == null) return actual.der;
            if (actual.der == null) return actual.izq;

            // Caso con dos hijos: Se busca el sucesor inmediato (valor mínimo a la derecha)
            Producto sucesor = encontrarMinimo(actual.der);
            actual.producto = sucesor;
            // Se elimina el nodo sucesor que fue movido a la posición actual
            actual.der = eliminarRecursivo(actual.der, sucesor.getNombre());
        }
        return actual;
    }

    /**
     * Encuentra el producto con el valor alfabético más bajo en un subárbol.
     * * @param nodo El punto de inicio de la búsqueda (subárbol derecho).
     * @return El producto con el nombre menor.
     */
    private Producto encontrarMinimo(NodoProducto nodo) {
        return nodo.izq == null ? nodo.producto : encontrarMinimo(nodo.izq);
    }

    /**
     * Recupera todos los productos del inventario en orden alfabético.
     * Realiza un recorrido In-Order para garantizar la secuencia correcta.
     * * @return List de productos ordenados alfabéticamente.
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