package cr.ac.cenfotec.bl.entities;

import cr.ac.cenfotec.bl.logic.ListaProductos;

/**
 * Entidad que representa a un cliente dentro del sistema.
 * Almacena la información personal, nivel de prioridad para la atención,
 * ubicación geográfica para la logística de entrega y su carrito de compras.
 */
public class Cliente {
    private String id;
    private String nombre;
    private int prioridad; // 1: Básico, 2: Afiliado, 3: Premium
    private String ubicacion;
    private ListaProductos carrito;

    /**
     * Constructor para la clase Cliente.
     * * @param id Identificación única del cliente.
     * @param nombre Nombre completo del cliente.
     * @param prioridad Valor entero (1-3) que define la prioridad en la cola de atención.
     * @param ubicacion Nombre del vértice en el grafo que representa la dirección del cliente.
     */
    public Cliente(String id, String nombre, int prioridad, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.ubicacion = ubicacion;
        this.carrito = new ListaProductos();
    }

    /**
     * @return El identificador único del cliente.
     */
    public String getId() { return id; }

    /**
     * @return El nombre del cliente.
     */
    public String getNombre() { return nombre; }

    /**
     * @return El nivel de prioridad (1, 2 o 3).
     */
    public int getPrioridad() { return prioridad; }

    /**
     * @return El nombre de la ubicación registrada para el grafo.
     */
    public String getUbicacion() { return ubicacion; }

    /**
     * Retorna la instancia de la lista enlazada que representa el carrito.
     * * @return El objeto ListaProductos asociado al cliente.
     */
    public ListaProductos getCarrito() {
        return carrito;
    }

    /**
     * Representación en cadena de texto del cliente para visualización en consola.
     * * @return String formateado con los datos del cliente.
     */
    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " - Prioridad: " + prioridad + " - Ubicación: " + ubicacion;
    }
}