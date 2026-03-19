package cr.ac.cenfotec.bl.logic;

public class Tienda {
    private ArbolProducto inventario;
    private ColaClientes colaAtencion;

    public Tienda() {
        this.inventario = new ArbolProducto();
        this.colaAtencion = new ColaClientes();
    }

    public ArbolProducto getInventario() { return inventario; }
    public ColaClientes getColaAtencion() { return colaAtencion; }
}