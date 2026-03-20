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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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
    private JList<P4ListaEnlazada.Nodo> listContenido;
    private DefaultListModel<P4ListaEnlazada.Nodo> modeloLista;
    private JSplitPane splitPane;
    private JToolBar barraHerramientas;
    private JLabel labelRuta;
    private JComboBox<String> comboOrden;
    private final GestorArchivos gestor= new GestorArchivos();
    private final Filemanager filemanager= new Filemanager();
    private final P4ListaEnlazada listaActual= new P4ListaEnlazada();
    
    public VentanaPrincipal(){
        super("Navegador de Archivos");
        inicializarUI();
        configurarEventos();
        setVisible(true);
    }
    
    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(crearBarraRuta(),         BorderLayout.NORTH);
        panelSuperior.add(crearBarraHerramientas(), BorderLayout.SOUTH);

        add(panelSuperior,  BorderLayout.NORTH);
        add(crearSplitPane(), BorderLayout.CENTER);
    }
    
    private JToolBar crearBarraHerramientas() {
        barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);

        JButton btnCrearCarpeta = new JButton("Nueva carpeta");
        JButton btnOrganizar = new JButton("Organizar");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnPegar = new JButton("Pegar");
        JButton btnRenombrar = new JButton("Renombrar");
        
        comboOrden = new JComboBox<>(new String[]{
            "Ordenar por...", "Nombre", "Tipo", "Fecha", "Tamaño"
        });
        
        btnCrearCarpeta.addActionListener(e -> accionCrearCarpeta());
        btnOrganizar.addActionListener(e -> accionOrganizar());
        btnCopiar.addActionListener(e -> accionCopiar());
        btnPegar.addActionListener(e -> accionPegar());
        btnRenombrar.addActionListener(e -> accionRenombrar());
        comboOrden.addActionListener(e -> accionOrdenar());
        
        barraHerramientas.add(btnCrearCarpeta);
        barraHerramientas.add(btnOrganizar);
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnCopiar);
        barraHerramientas.add(btnPegar);
        barraHerramientas.add(btnRenombrar);
        barraHerramientas.addSeparator();
        barraHerramientas.add(comboOrden);

        return barraHerramientas;
    }
    
    private JPanel crearBarraRuta(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelRuta = new JLabel("Ruta: ");
        panel.add(labelRuta);
        return panel;
    }
    
    private JSplitPane crearSplitPane(){
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                crearPanelArbol(),
                crearPanelContenido());
        splitPane.setDividerLocation(270);
        splitPane.setDividerSize(4);
        return splitPane;
    }
    
    private JScrollPane crearPanelArbol(){
        File raiz = new File(System.getProperty("user.home") + "/Documentos");
        if (!raiz.exists()) raiz = new File(System.getProperty("user.home"));

        modeloArbol   = new ModeloArbol(raiz);
        arbolArchivos = new JTree(modeloArbol);
        arbolArchivos.setRootVisible(true);
        arbolArchivos.setShowsRootHandles(true);

        arbolArchivos.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(
                    JTree tree, Object value, boolean sel,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
                if (nodo.getUserObject() instanceof Archivo) {
                    Archivo a = (Archivo) nodo.getUserObject();
                    setText(a.getNombre());
                    setIcon(a.isEsCarpeta()
                            ? UIManager.getIcon("FileView.directoryIcon")
                            : UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });

        arbolArchivos.addTreeExpansionListener(new TreeExpansionListener(){
            @Override public void treeExpanded(TreeExpansionEvent e){
                DefaultMutableTreeNode nodo =
                    (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                modeloArbol.expandirNodo(nodo);
            }
            @Override public void treeCollapsed(TreeExpansionEvent e) {}
        });

        return new JScrollPane(arbolArchivos);
    }
    
    private JScrollPane crearPanelContenido(){
        modeloLista   = new DefaultListModel<>();
        listContenido = new JList<>(modeloLista);
        listContenido.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listContenido.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof P4ListaEnlazada.Nodo) {
                    P4ListaEnlazada.Nodo n = (P4ListaEnlazada.Nodo) value;
                    setText(String.format("%-40s  %-20s  %-15s  %s",
                                n.getNombre(),
                                n.getFechaFormateada(),
                                n.getTipo(),
                                n.getTamanioFormateado()));
                    setIcon(n.archivo.isDirectory()
                            ? UIManager.getIcon("FileView.directoryIcon")
                            : UIManager.getIcon("FileView.fileIcon"));
                    setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                }
                return this;
            }
        });

        return new JScrollPane(listContenido);
    }
    
    private void configurarEventos() {
        arbolArchivos.addTreeSelectionListener(e -> {
            Archivo carpeta = getCarpetaSeleccionada();
            if (carpeta != null && carpeta.isEsCarpeta()) {
                mostrarContenido(carpeta.toFile());  // ← lee el disco en el hilo de Swing = se traba
                labelRuta.setText("Ruta: " + carpeta.getRuta());
                comboOrden.setSelectedIndex(0);
            }
        });
    }
    
    private void accionCrearCarpeta() {
        Archivo carpeta = getCarpetaSeleccionada();
        if (carpeta == null || !carpeta.isEsCarpeta()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una carpeta en el árbol primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = DialogoNombre.pedirNombreCrear(carpeta.toFile());
        if (nombre == null) return;

        boolean ok = gestor.crearCarpeta(carpeta.toFile(), nombre);
        if (ok) {
            refrescar();
            JOptionPane.showMessageDialog(this,
                "Carpeta \"" + nombre + "\" creada.", "Listo",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo crear la carpeta.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionOrganizar() {
        Archivo carpeta = getCarpetaSeleccionada();
        if (carpeta == null || !carpeta.isEsCarpeta()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una carpeta en el árbol primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Filemanager.OrganizeResult resultado =
                filemanager.organizeFolder(carpeta.toFile());
            refrescar();
            JOptionPane.showMessageDialog(this,
                resultado.log,
                resultado.tieneErrores() ? "Organización con errores" : "Organización completa",
                resultado.tieneErrores()
                    ? JOptionPane.WARNING_MESSAGE
                    : JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionCopiar() {
        P4ListaEnlazada.Nodo seleccionado = listContenido.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un archivo o carpeta en la lista primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Portapapeles.copiar(seleccionado.archivo);
        JOptionPane.showMessageDialog(this,
            "\"" + seleccionado.getNombre() + "\" copiado al portapapeles.",
            "Copiado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionPegar() {
        if (!Portapapeles.tieneContenido()) {
            JOptionPane.showMessageDialog(this,
                "El portapapeles está vacío. Copia algo primero.",
                "Portapapeles vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Archivo carpeta = getCarpetaSeleccionada();
        if (carpeta == null || !carpeta.isEsCarpeta()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una carpeta destino en el árbol.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = gestor.pegar(carpeta.toFile());
        if (ok) {
            refrescar();
            JOptionPane.showMessageDialog(this,
                "Pegado correctamente.", "Listo",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo pegar. Verifica permisos.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRenombrar() {
        P4ListaEnlazada.Nodo seleccionado = listContenido.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un archivo o carpeta en la lista primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nuevoNombre = DialogoNombre.pedirNombreRenombrar(seleccionado.archivo);
        if (nuevoNombre == null) return;

        boolean ok = gestor.renombre(seleccionado.archivo, nuevoNombre);
        if (ok) {
            refrescar();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo renombrar.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionOrdenar() {
        if (listaActual.estaVacia()) return;
        String criterio = (String) comboOrden.getSelectedItem();
        if (criterio == null || criterio.equals("Ordenar por...")) return;

        switch (criterio) {
            case "Nombre": BubbleSort.ordenarPorNombre(listaActual);  break;
            case "Tipo":   BubbleSort.ordenarPorTipo(listaActual);    break;
            case "Fecha":  MergeSort.ordenarPorFecha(listaActual);    break;
            case "Tamaño": MergeSort.ordenarPorTamanio(listaActual);  break;
        }
        actualizarLista(listaActual.aLista());
    }
    
    public void mostrarContenido(File directorio) {
        listaActual.cargarDirectorio(directorio);
        actualizarLista(listaActual.aLista());
    }
    
    private void actualizarLista(java.util.List<P4ListaEnlazada.Nodo> nodos) {
        modeloLista.clear();
        for (P4ListaEnlazada.Nodo n : nodos) {
            modeloLista.addElement(n);
        }
    }
    
    public void refrescar() {
        Archivo carpeta = getCarpetaSeleccionada();
        modeloArbol.refrescar();
        if (carpeta != null) mostrarContenido(carpeta.toFile());
    }
    
    public Archivo getCarpetaSeleccionada() {
        DefaultMutableTreeNode nodo =
            (DefaultMutableTreeNode) arbolArchivos.getLastSelectedPathComponent();
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

    public JList<P4ListaEnlazada.Nodo> getListContenido() {
        return listContenido;
    }
    
    public static void main(String[] args){
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
