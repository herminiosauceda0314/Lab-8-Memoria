/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import javax.swing.JOptionPane;

public class DialogoNombre {
 
    public static String pedirNombre(String titulo, String mensaje){
        String nombre = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.PLAIN_MESSAGE);
        if (nombre== null || nombre.trim().isEmpty()){
            return null;
        }
        return nombre.trim();
    }
 
    public static String pedirNombreCrear(File carpetaPadre){
        String nombre= pedirNombre("Crear carpeta", "Ingresa el nombre de la nueva carpeta:");
        if (nombre==null){
            return null;
        }
        GestorArchivos gestor= new GestorArchivos();
        if (gestor.existeNombre(carpetaPadre, nombre)){
            JOptionPane.showMessageDialog(null, "Ya existe una carpeta con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return nombre;
    }
 
    public static String pedirNombreRenombrar(File archivo){
        String nombre= pedirNombre("Renombrar", "Ingresa el nuevo nombre para: " + archivo.getName());
        if (nombre == null){
            return null;
        }
        GestorArchivos gestor= new GestorArchivos();
        if (gestor.existeNombre(archivo.getParentFile(), nombre)){
            JOptionPane.showMessageDialog(null, "Ya existe un archivo o carpeta con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return nombre;
    }
}
