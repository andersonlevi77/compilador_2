package Analizador_Sintactico;

/**
 *
 * @author juani
 */
public class Pila {

    private Nodo ultimoNodoIngresado; //ultimo nodo ingresado
    
    public Pila() {
        this.ultimoNodoIngresado = null;
    }

    //metodo para insertar dentro de la pila
    public void insertar(char valor) {
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
    public char extraer() {
        if (ultimoNodoIngresado != null) {
            char informacion = ultimoNodoIngresado.valor; //contiene el simbolo
            ultimoNodoIngresado = ultimoNodoIngresado.siguiente; //apuntar al siguiente nodo
            return informacion;
        }else{
            return Character.MAX_VALUE;
        }
    }
    
    //metodo para saber si la pila esta vacia
    public boolean pilaVacia(){
        return ultimoNodoIngresado == null;
    }
    
}
