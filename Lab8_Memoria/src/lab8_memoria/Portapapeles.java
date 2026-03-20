/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;

public class Portapapeles {
 
    private static File archivoCopIado= null;
 
    public static void copiar(File archivo){
        archivoCopIado = archivo;
    }
 
    public static File obtener(){
        return archivoCopIado;
    }
 
    public static boolean tieneContenido(){
        return archivoCopIado !=null;
    }
 
    public static void limpiar(){
        archivoCopIado= null;
    }
}
