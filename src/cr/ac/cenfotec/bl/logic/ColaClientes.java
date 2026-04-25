package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Cliente;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Gestiona la fila de atención mediante una Cola de Prioridad.
 * Utiliza una estructura de LinkedList para permitir inserciones en posiciones
 * intermedias basadas en el nivel de prioridad del cliente (1-3).
 */
public class ColaClientes implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Cliente> cola;
    private Grafo mapaLogistico;

    /**
     * Constructor que inicializa la lista enlazada para la cola.
     */
    public ColaClientes() {
        this.cola = new LinkedList<>();
        this.mapaLogistico = null;
    }

    /**
     * Constructor que permite integrar la cola con el grafo logístico.
     * Al encolar un cliente, su ubicación se registra automáticamente como vértice.
     * @param mapaLogistico Grafo de ubicaciones utilizado para despachos.
     */
    public ColaClientes(Grafo mapaLogistico) {
        this.cola = new LinkedList<>();
        this.mapaLogistico = mapaLogistico;
    }

    /**
     * Inserta un cliente en la posición correspondiente según su prioridad.
     * Si la prioridad es mayor a los elementos existentes, se coloca adelante.
     * Si las prioridades son iguales, se respeta el orden de llegada (FIFO).
     * * @param nuevoCliente Objeto Cliente que ingresa a la fila.
     */
    public void encolar(Cliente nuevoCliente) {
        if (mapaLogistico != null && nuevoCliente.getUbicacion() != null && !nuevoCliente.getUbicacion().isBlank()) {
            mapaLogistico.agregarVertice(nuevoCliente.getUbicacion());
        }

        if (cola.isEmpty()) {
            cola.add(nuevoCliente);
        } else {
            boolean insertado = false;
            for (int i = 0; i < cola.size(); i++) {
                // Validación de prioridad: Los valores más altos (Premium: 3)
                // se insertan antes que los valores más bajos.
                if (nuevoCliente.getPrioridad() > cola.get(i).getPrioridad()) {
                    cola.add(i, nuevoCliente);
                    insertado = true;
                    break;
                }
            }
            // Si no se insertó por prioridad alta, se añade al final (FIFO)
            if (!insertado) {
                cola.addLast(nuevoCliente);
            }
        }
    }

    /**
     * Recupera el cliente al frente de la cola sin removerlo de la estructura.
     * Útil para validaciones logísticas antes de procesar la factura.
     * * @return El objeto Cliente en la primera posición o null si está vacía.
     */
    public Cliente verSiguiente() {
        return cola.peekFirst();
    }

    /**
     * Remueve y retorna al cliente que se encuentra al frente de la cola.
     * Representa la acción de pasar a caja o despacho.
     * * @return El cliente atendido.
     */
    public Cliente atender() {
        return cola.poll();
    }

    /**
     * Verifica si existen clientes pendientes en la cola.
     * * @return true si la cola no tiene elementos, false de lo contrario.
     */
    public boolean estaVacia() {
        return cola.isEmpty();
    }
}