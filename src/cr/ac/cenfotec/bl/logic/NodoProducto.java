package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;

public class NodoProducto {

    Producto producto;
    NodoProducto siguiente;

    public NodoProducto(Producto producto) {
        this.producto = producto;
        this.siguiente = null;
    }
}
