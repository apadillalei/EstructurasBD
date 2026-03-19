package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;

public class NodoLista {
    public Producto producto;
    public NodoLista siguiente;

    public NodoLista(Producto producto) {
        this.producto = producto;
        this.siguiente = null;
    }
}