/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author gpopo
 */
public class P4ListaEnlazada {
    
    public static class Nodo {
        
        public File archivo;
        public Nodo siguente;
        
        public Nodo(File archivo) {
            this.archivo = archivo;
            this.siguente = null;                  
        }
        
        public String getNombre() {
            return archivo.getName();
        }
        
        public String getTipo() {
            if(archivo.isDirectory()) return "Carpeta";
            String n = archivo.getName();
            int dot = n.lastIndexOf('.');
            return (dot == -1 || dot == n.length()-1) 
                    ? "Archivo"
                    : n.substring(dot + 1).toUpperCase();
        }
        
        public long getTamanio() {
            return archivo.isFile()
                    ? archivo.length()
                    : calcularTamanioDirectorio(archivo);
        }
        
        public long getFecha() {
            return archivo.lastModified();
        }
        
        public String getTamanioFormateado() {
                long b = getTamanio();
                if(b < 1_024) return b + "B";
                if(b < 1_048_576) return String.format("%.1f KB", b / 1_024.0);
                if(b < 1_073_741_824) return String.format("%.1f MB", b / 1_048_576.0);
                return String.format("%.1f GB", b / 1_073_741_824.0);
        }
        
        public String getFechaFormateada() {
            java.text.SimpleDateFormat sdf = 
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(new java.util.Date(archivo.lastModified()));
        }
        
        private long calcularTamanioDirectorio(File dir) {
            long total = 0;
            File[] hijos = dir.listFiles();
            if (hijos != null)
                for (File h : hijos)
                    total += h.isFile() ? h.length() : calcularTamanioDirectorio(h);
            return total;
        }
        
        @Override 
        public String toString() {
            return getNombre();
        }
    }

    
    private Nodo cabeza;
    private int tamanio;
    
    public P4ListaEnlazada() {
        cabeza = null;
        tamanio = 0;
    }
    
    public void agregar(File archivo) {
        Nodo nuevo = new Nodo(archivo);
        if(cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguente != null) actual = actual.siguente;
            actual.siguente =  nuevo;
        }
        tamanio++;
    }
    
    public void limpiar() {
        cabeza = null;
        tamanio = 0;
    }
    
    public boolean estaVacia() {return cabeza == null; }
    public int getTamanio() {return tamanio;}
    public Nodo getCabeza() {return cabeza;}
    
    public void setCabeza(Nodo nuevaCabeza) {this.cabeza = nuevaCabeza; }
    
    public List<Nodo> aLista() {
        List<Nodo> resultado = new ArrayList<>();
        Nodo actual = cabeza;
        while(actual != null) {
            resultado.add(actual);
            actual = actual.siguente;
        }
        return resultado;
    }
    public void cargarDirectorio(File directorio) {
        limpiar();
        if(directorio == null || !directorio.isDirectory()) return;
        
        File[] contenido = directorio.listFiles();
        if (contenido == null) return;
        
        for (File f : contenido) if (f.isDirectory()) agregar(f);
        for (File f : contenido) if (f.isFile()) agregar(f);
    }
    
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder("Lista[");
        Nodo actual = cabeza;
        while (actual != null) {
            sb.append(actual.getNombre());
            if (actual.siguente != null) sb.append("->");
            actual = actual.siguente;
        }
        return sb.append("]").toString();
    }
}
