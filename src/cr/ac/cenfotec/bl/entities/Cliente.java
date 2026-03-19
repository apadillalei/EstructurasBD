package cr.ac.cenfotec.bl.entities;

import cr.ac.cenfotec.bl.logic.ListaProductos;

public class Cliente {
    private String id;
    private String nombre;
    private int prioridad; // 1: Básico, 2: Afiliado, 3: Premium
    private ListaProductos carrito;

    public Cliente(String id, String nombre, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.carrito = new ListaProductos();
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPrioridad() { return prioridad; }
    public ListaProductos getCarrito() { return carrito; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " - Prioridad: " + prioridad;
    }
}