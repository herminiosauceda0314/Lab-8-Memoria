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
public class BubbleSort {
    public enum Criterio {NOMBRE, TIPO}
    
    public static void ordenar(P4ListaEnlazada lista, Criterio criterio) {
        if(lista == null || lista.estaVacia()) return;
        
        boolean huboCambio;
        
        do{
            huboCambio = false;
            Nodo actual = lista.getCabeza();
            
            while (actual != null && actual.siguente != null) {
                Nodo siguente = actual.siguente;
                
                if(comparar(actual, siguente, criterio) > 0) {
                    intercambiarArchivos(actual, siguente);
                    huboCambio = true;
                }
                
                actual = actual.siguente;
            }
        } while(huboCambio);
    }
    
    private static int comparar (Nodo a, Nodo b, Criterio criterio) {
        switch(criterio) {
            
            case NOMBRE: 
                return a.getNombre().compareToIgnoreCase(b.getNombre());
             
            case TIPO: 
                int cmpTipo = a.getTipo().compareToIgnoreCase(b.getTipo());
                return cmpTipo != 0
                        ? cmpTipo
                        : a.getNombre().compareToIgnoreCase(b.getNombre());
            default:
                return 0;
        }
    }
    
    private static void intercambiarArchivos(Nodo a, Nodo b) {
        java.io.File temporal = a.archivo;
        a.archivo = b.archivo;
        b.archivo = temporal;
    }
    
    public static void ordenarPorrNombre(P4ListaEnlazada lista) {
        ordenar(lista, Criterio.NOMBRE);
    }
    
    public static void ordenarPorTipo(P4ListaEnlazada lista){
        ordenar(lista, Criterio.TIPO);
    }
}
