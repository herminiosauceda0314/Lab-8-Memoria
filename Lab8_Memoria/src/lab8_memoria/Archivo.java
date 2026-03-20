/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.util.Date;

/**
 *
 * @author hermi
 */
public class Archivo {
    private String nombre;
    private String ruta;
    private String rutaPadre;
    private boolean esCarpeta;
    private long tamano;
    private Date fechaModificacion;
    private String extension;
    
    public Archivo(File file){
        this.nombre=file.getName();
        this.ruta=file.getAbsolutePath();
        this.rutaPadre=file.getParent();
        this.esCarpeta=file.isDirectory();
        this.tamano=file.isDirectory() ? 0L:file.length();
        this.fechaModificacion=new Date(file.lastModified());
        this.extension=extraerExtension(file.getName());
    }
    
    public Archivo(String nombre, String ruta, boolean esCarpeta){
        this.nombre=nombre;
        this.ruta=ruta;
        this.rutaPadre=extraerPadre(ruta);
        this.esCarpeta=esCarpeta;
        this.tamano=0L;
        this.fechaModificacion=new Date();
        this.extension=esCarpeta ? "":extraerExtension(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public String getRutaPadre() {
        return rutaPadre;
    }

    public boolean isEsCarpeta() {
        return esCarpeta;
    }

    public long getTamano() {
        return tamano;
    }

    public Date getFechaModificacion() {
        return new Date(fechaModificacion.getTime());
    }

    public String getExtension() {
        return extension;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.extension = extraerExtension(nombre);
    }

    public void setRuta(String ruta) {
        this.ruta=ruta;
        this.rutaPadre=extraerPadre(ruta);
    }
    
    public String getTamanoLegible(){
        if(esCarpeta) 
            return "-";
        if(tamano<1024)
            return tamano+" B";
        if(tamano<1048576)
            return String.format("%.1f KB", tamano/1024.0);
        if(tamano<1073741824)
            return String.format("%.1f MB", tamano/1048576.0);
        return String.format("%.2f GB", tamano/1073741824.0);
    }
    
    public String getTipo(){
        if(esCarpeta)
            return "Carpeta";
        switch(extension.toLowerCase()){
            case "jpg","jpeg","png","gif","bmp","webp":
                return "Imagen";
            case "pdf","docx","doc","txt","odt":
                return "Documento";
            case "mp3","wav","flac","aac","ogg":
                return "Musica";
            case "mp4","avi","mkv","mov":
                return "Video";
            case "zip","rar","7z","tar","gz":
                return "Comprimido";
            default:
                return extension.isEmpty() ? "Sin tipo" : extension.toUpperCase();
        }
    }
    
    public File toFile(){
        return new File(ruta);
    }
    
    private String extraerExtension(String nombre){
        if(nombre==null||!nombre.contains("."))
            return "";
        return nombre.substring(nombre.lastIndexOf('.')+1);
    }
    
    private String extraerPadre(String ruta){
        if(ruta==null)
            return "";
        File f= new File(ruta);
        return f.getParent() != null ? f.getParent() : "";
    }
    
    @Override
    public String toString(){
        return String.format("Archivo{nombre='%s', tipo=%s, tamano=%s}",
                nombre, getTipo(), getTamanoLegible());
    }
    
}
