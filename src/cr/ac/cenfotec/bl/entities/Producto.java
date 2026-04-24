package cr.ac.cenfotec.bl.entities;

import java.util.ArrayList;

/**
 * Representa un producto dentro del sistema de inventarios.
 * Contiene información sobre costos, existencias, categorización y
 * recursos visuales asociados.
 */
public class Producto {

    private String nombre;
    private double precio;
    private String categoria;
    private String fechaVencimiento;
    private int cantidad;
    private ArrayList<String> listaImagenes;

    /**
     * Constructor de la clase Producto.
     * * @param nombre           Nombre identificador del producto.
     * @param precio           Costo unitario del producto.
     * @param categoria        Clasificación del producto.
     * @param fechaVencimiento Fecha de caducidad (si aplica).
     * @param cantidad         Unidades disponibles en inventario o carrito.
     */
    public Producto(String nombre, double precio, String categoria,
                    String fechaVencimiento, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidad = cantidad;
        this.listaImagenes = new ArrayList<>();
    }

    /** @return El nombre del producto. */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return El precio unitario. */
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /** @return La categoría del producto. */
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /** @return La fecha de vencimiento almacenada. */
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /** @return La cantidad de unidades disponibles. */
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** @return Lista de rutas o nombres de archivos de imagen. */
    public ArrayList<String> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(ArrayList<String> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    /**
     * Genera una representación textual detallada del producto.
     * Incluye lógica de validación para campos opcionales y cálculo de costo total.
     * * @return String formateado con la información completa del producto.
     */
    @Override
    public String toString() {
        return "==============================\n" +
                "Nombre: " + nombre + "\n" +
                "Categoría: " + categoria + "\n" +
                "Precio: ₡" + precio + "\n" +
                "Cantidad: " + cantidad + "\n" +
                "Fecha de vencimiento: " +
                (fechaVencimiento == null || fechaVencimiento.isEmpty()
                        ? "No aplica"
                        : fechaVencimiento) + "\n" +
                "Imágenes: " + (listaImagenes.isEmpty()
                ? "No hay imágenes registradas"
                : listaImagenes) + "\n" +
                "Costo total (precio x cantidad): ₡" + (precio * cantidad) + "\n" +
                "==============================";
    }

}