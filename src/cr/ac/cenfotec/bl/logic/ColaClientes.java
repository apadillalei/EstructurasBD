package cr.ac.cenfotec.bl.logic;

import cr.ac.cenfotec.bl.entities.Cliente;
import java.util.LinkedList;

public class ColaClientes {
    private LinkedList<Cliente> cola;

    public ColaClientes() {
        this.cola = new LinkedList<>();
    }

    public void encolar(Cliente nuevoCliente) {
        if (cola.isEmpty()) {
            cola.add(nuevoCliente);
        } else {
            boolean insertado = false;
            for (int i = 0; i < cola.size(); i++) {
                // si el nuevo tiene mayor priioridad que el que está en la posición i
                if (nuevoCliente.getPrioridad() > cola.get(i).getPrioridad()) {
                    cola.add(i, nuevoCliente);
                    insertado = true;
                    break;
                }
            }
            if (!insertado) {
                cola.addLast(nuevoCliente); // si tiene la menor o igual, va al final
            }
        }
    }

    public Cliente atender() {
        return cola.poll(); // saca al que tiene más prioridad (al frente)
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }
}