package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;

/**
 * Representa un nodo individual dentro de la estructura del ArbolProducto.
 * Cada nodo almacena un objeto Producto y referencias a sus nodos hijos
 * para facilitar la navegación jerárquica del árbol binario.
 */
public class NodoProducto {
    /**
     * El objeto Producto almacenado en este nodo del árbol.
     */
    public Producto producto;

    /**
     * Referencia al hijo izquierdo (contiene productos con nombres alfabéticamente menores).
     */
    public NodoProducto izq;

    /**
     * Referencia al hijo derecho (contiene productos con nombres alfabéticamente mayores).
     */
    public NodoProducto der;

    /**
     * Constructor para inicializar un nodo con un producto específico.
     * Los hijos se inicializan como nulos por defecto.
     * @param producto El objeto Producto que será la carga útil del nodo.
     */
    public NodoProducto(Producto producto) {
        this.producto = producto;
        this.izq = null;
        this.der = null;
    }
}