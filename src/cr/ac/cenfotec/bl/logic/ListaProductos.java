package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Producto;

public class ListaProductos {

    private NodoProducto cabeza;

    public ListaProductos() {
        cabeza = null;
    }


    // INSERTAR AL INICIO
    public void insertarInicio(Producto producto) {
        NodoProducto nuevo = new NodoProducto(producto);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
    }


    // INSERTAR AL FINAL
    public void insertarFinal(Producto producto) {
        NodoProducto nuevo = new NodoProducto(producto);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoProducto actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }


    // BUSCAR PRODUCTO POR NOMBRE
    public Producto buscarPorNombre(String nombre) {
        NodoProducto actual = cabeza;

        while (actual != null) {
            if (actual.producto.getNombre().equalsIgnoreCase(nombre)) {
                return actual.producto;
            }
            actual = actual.siguiente;
        }

        return null;
    }


    // MODIFICAR PRODUCTO
    public boolean modificarProducto(String nombre, double nuevoPrecio, int nuevaCantidad) {

        Producto producto = buscarPorNombre(nombre);

        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
            producto.setCantidad(nuevaCantidad);
            return true;
        }

        return false;
    }


    // AGREGAR IMAGEN A PRODUCTO
    public boolean agregarImagenAProducto(String nombre, String rutaImagen) {
        Producto producto = buscarPorNombre(nombre);

        if (producto != null) {
            producto.getListaImagenes().add(rutaImagen);
            return true;
        }

        return false;
    }


    // MOSTRAR PRODUCTOS
    public void mostrarProductos() {
        NodoProducto actual = cabeza;

        if (cabeza == null) {
            System.out.println("La lista está vacía.");
            return;
        }

        while (actual != null) {
            System.out.println(actual.producto);
            actual = actual.siguiente;
        }
    }


    // REPORTE DE COSTOS
    public void imprimirReporteCostos() {
        NodoProducto actual = cabeza;
        double totalGeneral = 0;

        if (cabeza == null) {
            System.out.println("No hay productos registrados.");
            return;
        }

        while (actual != null) {

            double totalProducto = actual.producto.getPrecio() *
                    actual.producto.getCantidad();

            totalGeneral += totalProducto;

            System.out.println("Producto: " + actual.producto.getNombre());
            System.out.println("Costo total: ₡" + totalProducto);
            System.out.println("--------------------------------");

            actual = actual.siguiente;
        }

        System.out.println("TOTAL GENERAL INVENTARIO: ₡" + totalGeneral);
    }
}
