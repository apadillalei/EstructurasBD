package cr.ac.cenfotec.bl.logic;

/**
 * Clase controladora de la lógica de negocio que centraliza las estructuras de datos.
 * Administra el inventario (Árbol), la fila de clientes (Cola) y la red de
 * distribución (Grafo), además de definir el punto de origen de los despachos.
 */
public class Tienda {
    private ArbolProducto inventario;
    private ColaClientes colaAtencion;
    private Grafo mapaLogistico;
    private String ubicacionSede;

    /**
     * Constructor que inicializa las estructuras de datos y configura el estado inicial.
     * Establece la Sede Central como punto de partida y carga el mapa base.
     */
    public Tienda() {
        this.inventario = new ArbolProducto();
        this.colaAtencion = new ColaClientes();
        this.mapaLogistico = new Grafo();
        this.ubicacionSede = "Sede Central"; // Punto de origen para el algoritmo de Dijkstra
        inicializarMapaBase();
    }

    /**
     * Configura las conexiones iniciales del grafo para asegurar que el sistema
     * tenga una red funcional desde el arranque.
     */
    private void inicializarMapaBase() {
        // Conexiones predefinidas con sus respectivas distancias en kilómetros
        mapaLogistico.agregarArista("Sede Central", "San Pedro", 3);
        mapaLogistico.agregarArista("Sede Central", "Escazu", 8);
        mapaLogistico.agregarArista("San Pedro", "Curridabat", 4);
        mapaLogistico.agregarArista("Escazu", "Santa Ana", 5);
        mapaLogistico.agregarArista("Curridabat", "Tres Rios", 6);
    }

    /** @return La instancia del Árbol Binario de Búsqueda de productos. */
    public ArbolProducto getInventario() { return inventario; }

    /** @return La instancia de la Cola de Prioridad de clientes. */
    public ColaClientes getColaAtencion() { return colaAtencion; }

    /** @return La instancia del Grafo logístico de la tienda. */
    public Grafo getMapaLogistico() { return mapaLogistico; }

    /** @return El nombre del vértice que representa la sede de la tienda. */
    public String getUbicacionSede() { return ubicacionSede; }
}