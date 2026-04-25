package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;
import java.io.Serializable;

/**
 * Clase que representa un elemento individual (nodo) dentro de la ListaProductos.
 * Contiene la carga útil (Producto) y la referencia al siguiente elemento en la secuencia.
 */
public class NodoLista implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Objeto de la entidad Producto almacenado en este nodo.
     */
    public Producto producto;

    /**
     * Referencia al siguiente nodo en la lista enlazada.
     */
    public NodoLista siguiente;

    /**
     * Constructor para la creación de un nuevo nodo.
     * @param producto El objeto Producto que se desea almacenar.
     */
    public NodoLista(Producto producto) {
        this.producto = producto;
        this.siguiente = null;
    }
}