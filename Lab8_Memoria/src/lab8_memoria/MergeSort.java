/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;
import lab8_memoria.P4ListaEnlazada.Nodo;
/**
 *
 * @author gpopo
 */
public class MergeSort {
    public enum Criterio { FECHA, TAMANIO }
    
    public static void ordenar(P4ListaEnlazada lista, Criterio criterio) {
        if(lista == null || lista.estaVacia()) return;
        
        Nodo nuevaCabeza = ordenarRecursivo(lista.getCabeza(), criterio);
        lista.setCabeza(nuevaCabeza);
    }
    
    private static Nodo ordenarRecursivo(Nodo cabeza, Criterio criterio) {
        if(cabeza == null || cabeza.siguente == null) return cabeza;
        
        Nodo medio = encontrarMedio(cabeza);
        Nodo segundaMitad = medio.siguente;
        medio.siguente = null;
        
        Nodo izquierda = ordenarRecursivo(cabeza, criterio);
        Nodo derecha = ordenarRecursivo(segundaMitad, criterio);
        
        return mezclar(izquierda, derecha, criterio);
    }
    
    private static Nodo encontrarMedio(Nodo cabeza) {
        if (cabeza == null) return null;
        
        Nodo tortuga = cabeza;
        Nodo liebre = cabeza.siguente;
        
        while (liebre != null && liebre.siguente != null) {
            tortuga = tortuga.siguente;
            liebre = liebre.siguente.siguente;
        }
        return tortuga;
    }
    
    private static Nodo mezclar(Nodo izquierda, Nodo derecha, Criterio criterio) {
        Nodo dummy = new Nodo(new java.io.File("."));
        Nodo cola = dummy;
        
        while (izquierda != null && derecha != null) {
            if (comparar(izquierda, derecha, criterio) <= 0){
                cola.siguente = izquierda;
                izquierda = izquierda.siguente;
            } else{
                cola.siguente = derecha;
                derecha = derecha.siguente;
            }
            cola = cola.siguente;
        }
        
        cola.siguente = (izquierda != null) ? izquierda : derecha;
        return dummy.siguente;
    }
    
    private static int comparar(Nodo a, Nodo b, Criterio criterio) {
        switch(criterio) {
            case FECHA:
                return Long.compare(b.getFecha(), a.getFecha());
            case TAMANIO: 
                return Long.compare(a.getTamanio(), b.getTamanio());
                
            default: 
                return 0;
        }
    }
    
    public static void ordenarPorFecha(P4ListaEnlazada lista) {
        ordenar(lista, Criterio.FECHA);
    }
    
    public static void ordenarPorTamanio(P4ListaEnlazada lista) {
        ordenar(lista, Criterio.TAMANIO);
    }
}
