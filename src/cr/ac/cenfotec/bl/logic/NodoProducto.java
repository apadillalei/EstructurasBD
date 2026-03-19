package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;

public class NodoProducto {
    public Producto producto;
    public NodoProducto izq, der;

    public NodoProducto(Producto producto) {
        this.producto = producto;
        this.izq = null;
        this.der = null;
    }
}