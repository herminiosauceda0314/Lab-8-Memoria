/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.awt.*;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author hermi
 */
public class VentanaPrincipal extends JFrame{
    
    private JTree arbolArchivos;
    private ModeloArbol modeloArbol;
    private JList<Archivo> listContenido;
    private DefaultListModel<Archivo> modeloLista;
    private JSplitPane splitPane;
    private JToolBar barraHerramientas;
    private JLabel labelRuta;
    
    public VentanaPrincipal(){
        super("Navegador de Archivos");
        inicializarUI();
        configurarEventos();
        setVisible(true);
    }
    
    private void inicializarUI(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        add(crearBarraHerramientas(),BorderLayout.NORTH);
        add(crearBarraRuta(),BorderLayout.SOUTH);
        add(crearSplitPane(),BorderLayout.CENTER);
    }
    
    private JToolBar crearBarraHerramientas() {
        barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);

        JButton btnCrearCarpeta = new JButton("Nueva carpeta");
        JButton btnOrganizar = new JButton("Organizar");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnPegar = new JButton("Pegar");
        JButton btnRenombrar = new JButton("Renombrar");
        
        barraHerramientas.add(btnCrearCarpeta);
        barraHerramientas.add(btnOrganizar);
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnCopiar);
        barraHerramientas.add(btnPegar);
        barraHerramientas.add(btnRenombrar);

        return barraHerramientas;
    }
    
    private JPanel crearBarraRuta() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelRuta = new JLabel("Ruta: ");
        panel.add(labelRuta);
        return panel;
    }
    
    private JSplitPane crearSplitPane() {
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                crearPanelArbol(),
                crearPanelContenido()
        );
        splitPane.setDividerLocation(260);
        splitPane.setDividerSize(4);
        return splitPane;
    }
    
    private JScrollPane crearPanelArbol() {
        File raiz = new File(System.getProperty("user.home") + "/Documentos");
        modeloArbol = new ModeloArbol(raiz);
        arbolArchivos = new JTree(modeloArbol);

        arbolArchivos.setRootVisible(true);
        arbolArchivos.setShowsRootHandles(true);

        arbolArchivos.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(
                    JTree tree, Object value, boolean sel,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {

                super.getTreeCellRendererComponent(
                        tree, value, sel, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
                if (nodo.getUserObject() instanceof Archivo) {
                    Archivo archivo = (Archivo) nodo.getUserObject();
                    setText(archivo.getNombre());
                    setIcon(archivo.isEsCarpeta()
                            ? UIManager.getIcon("FileView.directoryIcon")
                            : UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });

        arbolArchivos.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                DefaultMutableTreeNode nodo= (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                modeloArbol.expandirNodo(nodo);
            }
            @Override
            public void treeCollapsed(TreeExpansionEvent event) { }
        });

        return new JScrollPane(arbolArchivos);
    }
    
    private JScrollPane crearPanelContenido() {
        modeloLista   = new DefaultListModel<>();
        listContenido = new JList<>(modeloLista);

        listContenido.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listContenido.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Archivo) {
                    Archivo archivo = (Archivo) value;
                    setText(String.format("%-40s %10s  %s",
                            archivo.getNombre(),
                            archivo.getTamanoLegible(),
                            archivo.getTipo()));
                    setIcon(archivo.isEsCarpeta()
                            ? UIManager.getIcon("FileView.directoryIcon")
                            : UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });

        return new JScrollPane(listContenido);
    }
    
    private void configurarEventos() {
        arbolArchivos.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodo= (DefaultMutableTreeNode) arbolArchivos.getLastSelectedPathComponent();
            if (nodo == null) return;

            if (nodo.getUserObject() instanceof Archivo) {
                Archivo archivo = (Archivo) nodo.getUserObject();
                if (archivo.isEsCarpeta()) {
                    mostrarContenido(archivo);
                    labelRuta.setText("Ruta: "+archivo.getRuta());
                }
            }
        });
    }
    
    public void mostrarContenido(Archivo carpeta) {
        modeloLista.clear();
        File[] hijos = carpeta.toFile().listFiles();
        if (hijos == null) return;

        for (File hijo:hijos) {
            if (!hijo.isHidden()) {
                modeloLista.addElement(new Archivo(hijo));
            }
        }
    }
    
    public void refrescar() {
        DefaultMutableTreeNode nodoSeleccionado=(DefaultMutableTreeNode) arbolArchivos.getLastSelectedPathComponent();
        modeloArbol.refrescar();

        if (nodoSeleccionado != null &&
                nodoSeleccionado.getUserObject() instanceof Archivo) {
            mostrarContenido((Archivo) nodoSeleccionado.getUserObject());
        }
    }
    
    public Archivo getCarpetaSeleccionada() {
        DefaultMutableTreeNode nodo =
                (DefaultMutableTreeNode) arbolArchivos
                        .getLastSelectedPathComponent();
        if (nodo == null) return null;
        Object obj = nodo.getUserObject();
        return (obj instanceof Archivo) ? (Archivo) obj : null;
    }

    public JTree getArbolArchivos() {
        return arbolArchivos;
    }

    public ModeloArbol getModeloArbol() {
        return modeloArbol;
    }

    public JList<Archivo> getListContenido() {
        return listContenido;
    }
    
    public static void main(String[] args){
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
