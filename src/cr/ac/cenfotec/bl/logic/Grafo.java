package cr.ac.cenfotec.bl.logic;

import java.util.*;

/**
 * Clase que gestiona la red logística mediante un Grafo ponderado no dirigido.
 * Implementa el algoritmo de Dijkstra para determinar la ruta de entrega más corta
 * entre la sede central y la ubicación del cliente.
 */
public class Grafo {
    /** * Representación del grafo mediante lista de adyacencia (Map de Maps).
     * La llave externa es el origen, la interna es el destino y el Integer es el peso (distancia).
     */
    private Map<String, Map<String, Integer>> adyacencia;

    /**
     * Constructor que inicializa la estructura de datos del grafo.
     */
    public Grafo() {
        this.adyacencia = new HashMap<>();
    }

    /**
     * Añade un nuevo nodo al grafo si este no existe previamente.
     * @param nombre Etiqueta identificadora del vértice (ej: Ciudad o Punto de entrega).
     */
    public void agregarVertice(String nombre) {
        adyacencia.putIfAbsent(nombre, new HashMap<>());
    }

    /**
     * Establece una conexión bidireccional entre dos nodos con un peso específico.
     * @param origen  Vértice inicial.
     * @param destino Vértice final.
     * @param peso    Distancia o costo de la arista (debe ser estático y positivo).
     */
    public void agregarArista(String origen, String destino, int peso) {
        agregarVertice(origen);
        agregarVertice(destino);
        adyacencia.get(origen).put(destino, peso);
        adyacencia.get(destino).put(origen, peso); // Propiedad de grafo no dirigido
    }

    /**
     * Implementación del Algoritmo de Dijkstra.
     * Calcula la ruta con el menor costo acumulado entre dos puntos.
     * @param inicio Vértice de partida (Sede de la Tienda).
     * @param fin    Vértice de destino (Ubicación del Cliente).
     * @return Map con la ruta formateada y la distancia total, o null si no hay conexión.
     */
    public Map<String, Object> calcularRutaCorta(String inicio, String fin) {
        if (!adyacencia.containsKey(inicio) || !adyacencia.containsKey(fin)) {
            return null; // Validación de existencia de puntos
        }

        Map<String, Integer> distancias = new HashMap<>();
        Map<String, String> antecesores = new HashMap<>();
        // Cola de prioridad para seleccionar siempre el nodo con la menor distancia conocida
        PriorityQueue<String> colaPrioridad = new PriorityQueue<>(Comparator.comparingInt(distancias::get));

        // Inicialización de distancias al infinito
        for (String vertice : adyacencia.keySet()) {
            distancias.put(vertice, Integer.MAX_VALUE);
        }
        distancias.put(inicio, 0);
        colaPrioridad.add(inicio);

        while (!colaPrioridad.isEmpty()) {
            String actual = colaPrioridad.poll();

            if (actual.equals(fin)) break; // Optimización: detener si llegamos al destino

            // Proceso de relajación de aristas adyacentes
            for (Map.Entry<String, Integer> vecinoEntry : adyacencia.get(actual).entrySet()) {
                String vecino = vecinoEntry.getKey();
                int pesoArista = vecinoEntry.getValue();
                int nuevaDistancia = distancias.get(actual) + pesoArista;

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    antecesores.put(vecino, actual);
                    colaPrioridad.add(vecino);
                }
            }
        }

        // Validación de conectividad (si la distancia sigue siendo infinito, no hay camino)
        if (distancias.get(fin) == Integer.MAX_VALUE) return null;

        // Reconstrucción de la ruta mediante el rastreo de antecesores
        List<String> camino = new LinkedList<>();
        String paso = fin;
        while (paso != null) {
            camino.add(0, paso);
            paso = antecesores.get(paso);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("camino", String.join(" -> ", camino));
        resultado.put("distancia", distancias.get(fin));
        return resultado;
    }

    /**
     * @return El conjunto de todos los nombres de vértices registrados.
     */
    public Set<String> getVertices() {
        return adyacencia.keySet();
    }
}