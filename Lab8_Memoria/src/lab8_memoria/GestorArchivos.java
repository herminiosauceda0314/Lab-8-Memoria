/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class GestorArchivos {
    
    public boolean crearCarpeta(File carpetaP, String nombre){
        if (nombre==null || nombre.trim().isEmpty()){
            return false;
        }
        
        File nueva= new File(carpetaP, nombre.trim());
        if (nueva.exists()){
            return false;
        }
        return nueva.mkdir();
    }
    
    public boolean renombre(File archivo, String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.trim().isEmpty()){
            return false;
        }
        
        File destino= new File(archivo.getParentFile(), nuevoNombre.trim());
        if (destino.exists()){
            return false;
        }
        return archivo.renameTo(destino);
    }
    
    public boolean copiarArchivo(File origen, File carpetaDestino){
        if (!origen.exists() || !carpetaDestino.isDirectory()){
            return false;
        }
        
        File destino= new File(carpetaDestino, origen.getName());
        if (destino.exists()){
            return false;
        }
        
        try (FileInputStream in = new FileInputStream(origen);
             FileOutputStream out = new FileOutputStream(destino)){
            byte[] buffer= new byte[4096];
            int leidos;
            while ((leidos= in.read(buffer)) != -1){
                out.write(buffer, 0, leidos);
            }
            return true;
        }catch (IOException e){
            return false;
        }  
    }
    
    public boolean copiarCarpeta(File origen, File carpetaDestino){
        if (!origen.isDirectory()){
            return false;
        }
        File nueva = new File(carpetaDestino, origen.getName());
        if (!nueva.mkdir()) {
            return false;
        }
        File[] contenido = origen.listFiles();
        if (contenido==null) {
            return true;
        }
        for (File hijo : contenido){
            if (hijo.isDirectory()){
                copiarCarpeta(hijo, nueva);
            } else {
                copiarArchivo(hijo, nueva);
            }
        }
        return true;
    }
 
    public boolean pegar(File carpetaDestino){
        if (!Portapapeles.tieneContenido()){
            return false;
        }
        File origen = Portapapeles.obtener();
        boolean resultado;
        if (origen.isDirectory()){
            resultado= copiarCarpeta(origen, carpetaDestino);
        } else{
            resultado =copiarArchivo(origen, carpetaDestino);
        }
        return resultado;
    }
 
    public boolean moverArchivo(File archivo, File carpetaDestino){
        File destino= new File(carpetaDestino, archivo.getName());
        return archivo.renameTo(destino);
    }
 
    public boolean existeNombre(File carpetaPadre, String nombre){
        File candidato = new File(carpetaPadre, nombre.trim());
        return candidato.exists();
    }
}
