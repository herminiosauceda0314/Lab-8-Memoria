/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author hermi
 */
public class ModeloArbol extends DefaultTreeModel{
    
    public ModeloArbol(File carpetaRaiz){
        super(crearNodo(carpetaRaiz));
        cargarHijos((DefaultMutableTreeNode) getRoot());
    }

    private void cargarHijos(DefaultMutableTreeNode nodo){
        Archivo archivo= (Archivo) nodo.getUserObject();
        File carpeta = archivo.toFile();
        
        if (!carpeta.isDirectory())
            return;
        
        File[] contenido = carpeta.listFiles();
        if(contenido==null)
            return;
        
        Arrays.sort(contenido, Comparator
              .comparing(File::isFile)
              .thenComparing(f -> f.getName().toLowerCase()));
        
        for (File hijo:contenido){
            if(hijo.isHidden())
                continue;
            
            DefaultMutableTreeNode nodoHijo=crearNodo(hijo);
            
            if(hijo.isDirectory()){
                nodoHijo.add(new DefaultMutableTreeNode("Cargando ..."));
            }
            nodo.add(nodoHijo);
        }
    }
    
    public void expandirNodo(DefaultMutableTreeNode nodo){
        if(nodo.getChildCount()==1){
            DefaultMutableTreeNode primerHijo = (DefaultMutableTreeNode) nodo.getChildAt(0);
            
            if("Cargando...".equals(primerHijo.getUserObject())){
                nodo.removeAllChildren();
                cargarHijos(nodo);
                reload(nodo);
            }
        }
    }
    
    public void actualizarNodo(DefaultMutableTreeNode nodo){
        nodo.removeAllChildren();
        cargarHijos(nodo);
        reload(nodo);
    }
    
    public void refrescar(){
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) getRoot();
        raiz.removeAllChildren();
        cargarHijos(raiz);
        reload(raiz);
    }
    
    public DefaultMutableTreeNode buscarNodo(String ruta){
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) getRoot();
        return buscarRecursivo(raiz,ruta);
    }
    
    public DefaultMutableTreeNode buscarRecursivo(DefaultMutableTreeNode nodo, String ruta){
        Archivo archivo = (Archivo) nodo.getUserObject();
        
        if(archivo.getRuta().equals(ruta))
            return nodo;
        
        for(int i=0; i<nodo.getChildCount(); i++){
            DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) nodo.getChildAt(i);
            DefaultMutableTreeNode resultado = buscarRecursivo(hijo, ruta);
            
            if(resultado != null)
                return resultado;
        }
        return null;
    }
    
    private static DefaultMutableTreeNode crearNodo(File file){
        return new DefaultMutableTreeNode(new Archivo(file));
    }

}
