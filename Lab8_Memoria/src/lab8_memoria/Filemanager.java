/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author Alejandro R
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Filemanager {

    private static final Set<String> IMG_EXT = Set.of(
            "jpg","jpeg","png","gif","bmp","svg","webp","tiff","ico");
    private static final Set<String> DOC_EXT = Set.of(
            "pdf","docx","doc","txt","xlsx","xls","pptx","ppt",
            "odt","ods","csv","rtf","md");
    private static final Set<String> MUS_EXT = Set.of(
            "mp3","wav","ogg","flac","aac","m4a","wma","opus");

    private static final String DIR_IMAGENES   = "Imagenes";
    private static final String DIR_DOCUMENTOS = "Documentos";
    private static final String DIR_MUSICA     = "Musica";

    private final List<File> clipboard = new ArrayList<>();
    private boolean isCut = false;

    private final P4ListaEnlazada fileList = new P4ListaEnlazada();

    public P4ListaEnlazada getFileList() { return fileList; }

    public void loadDirectory(File dir) {
        fileList.limpiar();
        if (dir == null || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files != null)
            for (File f : files) fileList.agregar(f);
    }

    public List<P4ListaEnlazada.Nodo> sortByName() {
        BubbleSort.ordenarPorNombre(fileList);
        return fileList.aLista();
    }
    
    public List<P4ListaEnlazada.Nodo> sortByType() {
        BubbleSort.ordenarPorTipo(fileList);
        return fileList.aLista();
    }
    
    public List<P4ListaEnlazada.Nodo> sortByDate() {
        MergeSort.ordenarPorFecha(fileList);
        return fileList.aLista();
    }
    
    public List<P4ListaEnlazada.Nodo> sortBySize() {
        MergeSort.ordenarPorTamanio(fileList);
        return fileList.aLista();
    }

    public boolean createFolder(File parent, String name) throws IOException {
        if (name == null || name.trim().isEmpty())
            throw new IOException("El nombre no puede estar vacio.");
        String trimmed = name.trim();
        if (trimmed.matches(".*[\\\\/:*?\"<>|].*"))
            throw new IOException("Nombre con caracteres no permitidos: \\ / : * ? \" < > |");
        File newDir = new File(parent, trimmed);
        if (newDir.exists())
            throw new IOException("Ya existe una carpeta llamada \"" + trimmed + "\".");
        if (!newDir.mkdir())
            throw new IOException("No se pudo crear la carpeta. Verifica permisos.");
        return true;
    }

    public boolean rename(File file, String newName) throws IOException {
        if (newName == null || newName.trim().isEmpty())
            throw new IOException("El nombre no puede estar vacio.");
        File dest = new File(file.getParentFile(), newName.trim());
        if (dest.exists())
            throw new IOException("Ya existe un elemento llamado \"" + newName.trim() + "\".");
        if (!file.renameTo(dest))
            throw new IOException("No se pudo renombrar. El archivo puede estar en uso.");
        return true;
    }

    public void copyToClipboard(List<File> files) { clipboard.clear(); clipboard.addAll(files); isCut = false; }
    public void cutToClipboard(List<File> files)  { clipboard.clear(); clipboard.addAll(files); isCut = true;  }
    public List<File> getClipboard() { return Collections.unmodifiableList(clipboard); }
    public boolean hasClipboard()    { return !clipboard.isEmpty(); }
    public boolean isCut()           { return isCut; }

    public void paste(File destination) throws IOException {
        if (clipboard.isEmpty()) return;
        for (File src : clipboard) {
            File dest = resolveDestination(src, destination);
            if (src.isDirectory()) copyDirectory(src.toPath(), dest.toPath());
            else Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (isCut) deleteRecursive(src);
        }
        if (isCut) clipboard.clear();
    }

    public OrganizeResult organizeFolder(File folder) throws IOException {
        if (folder == null || !folder.isDirectory())
            throw new IOException("Seleccione una carpeta valida.");
        File[] files = folder.listFiles(File::isFile);
        if (files == null || files.length == 0)
            return new OrganizeResult(0, 0, 0, "No hay archivos para organizar en esta carpeta.");

        int movidos = 0, omitidos = 0, errores = 0;
        StringBuilder log = new StringBuilder();

        for (File archivo : files) {
            String ext = getExtension(archivo.getName()).toLowerCase();
            String carpetaDestino = determinarCarpetaDestino(ext);

            if (carpetaDestino == null) {
                omitidos++;
                log.append("  Omitido (sin categoria): ").append(archivo.getName()).append("\n");
                continue;
            }

            File subCarpeta = new File(folder, carpetaDestino);
            if (!subCarpeta.exists()) {
                if (subCarpeta.mkdirs())
                    log.append("  Carpeta creada: /").append(carpetaDestino).append("\n");
            }

            File destino = resolveDestination(archivo, subCarpeta);
            if (archivo.renameTo(destino)) {
                movidos++;
                log.append("  OK ").append(archivo.getName()).append(" -> /").append(carpetaDestino).append("\n");
            } else {
                errores++;
                log.append("  ERROR al mover: ").append(archivo.getName()).append("\n");
            }
        }

        String resumen = String.format(
            "Organizacion completada.\n\nMovidos : %d\nOmitidos: %d\nErrores : %d\n\nDetalle:\n%s",
            movidos, omitidos, errores, log);
        return new OrganizeResult(movidos, omitidos, errores, resumen);
    }

    public String determinarCarpetaDestino(String ext) {
        if (IMG_EXT.contains(ext)) return DIR_IMAGENES;
        if (DOC_EXT.contains(ext)) return DIR_DOCUMENTOS;
        if (MUS_EXT.contains(ext)) return DIR_MUSICA;
        return null;
    }

    public static class OrganizeResult {
        public final int movidos, omitidos, errores;
        public final String log;
        public OrganizeResult(int m, int o, int e, String l) { movidos=m; omitidos=o; errores=e; log=l; }
        public boolean tieneErrores() { return errores > 0; }
        public boolean tuvoExito()    { return movidos > 0; }
    }

    private File resolveDestination(File src, File destDir) {
        File dest = new File(destDir, src.getName());
        if (!dest.exists()) return dest;
        String nombre = src.getName();
        int punto = nombre.lastIndexOf('.');
        String base = (punto >= 0) ? nombre.substring(0, punto) : nombre;
        String ext  = (punto >= 0) ? nombre.substring(punto)    : "";
        int c = 1;
        do { dest = new File(destDir, base + "_copia" + (c > 1 ? c : "") + ext); c++; } while (dest.exists());
        return dest;
    }

    private void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(origen -> {
            try { Files.copy(origen, dest.resolve(src.relativize(origen)), StandardCopyOption.REPLACE_EXISTING); }
            catch (IOException e) { throw new UncheckedIOException(e); }
        });
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) { File[] h = file.listFiles(); if (h != null) for (File c : h) deleteRecursive(c); }
        file.delete();
    }

    public static String getExtension(String name) {
        int p = name.lastIndexOf('.');
        return (p >= 0 && p < name.length()-1) ? name.substring(p+1) : "";
    }

    public static String humanSize(long bytes) {
        if (bytes < 1_024)         return bytes + " B";
        if (bytes < 1_048_576)     return String.format("%.1f KB", bytes / 1_024.0);
        if (bytes < 1_073_741_824) return String.format("%.1f MB", bytes / 1_048_576.0);
        return String.format("%.1f GB", bytes / 1_073_741_824.0);
    }
}
