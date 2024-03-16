package Analizador_Sintactico;

/**
 *
 * @author juani
 */
public class Pila {

    private class Nodo {

        String valor; //elemento que tiene el nodo
        Nodo siguiente;
    }
    
    private Nodo ultimoNodoIngresado; //ultimo nodo ingresado

    public Pila() {
        this.ultimoNodoIngresado = null;
    }

    //metodo para insertar dentro de la pila
    public void insertar(String valor) {
        Nodo nuevoNodo = new Nodo();
        nuevoNodo.valor = valor;
        if (ultimoNodoIngresado == null) {
            nuevoNodo.siguiente = null;
            ultimoNodoIngresado = nuevoNodo;
        } else {
            nuevoNodo.siguiente = ultimoNodoIngresado;
            ultimoNodoIngresado = nuevoNodo;
        }
    }

    //metodo para extraer datos
    public String extraer() {
        if (ultimoNodoIngresado != null) {
            String informacion = ultimoNodoIngresado.valor; //contiene el simbolo
            ultimoNodoIngresado = ultimoNodoIngresado.siguiente; //apuntar al siguiente nodo
            return informacion;
        } else {
            return null;
        }
    }

    //metodo para saber si la pila esta vacia
    public boolean pilaVacia() {
        return ultimoNodoIngresado == null;
    }
}
