package com.tiendaelectronica.tiendaelectronicafinal;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductosForm extends javax.swing.JFrame {

    private List<Categoria> categorias = new ArrayList<>();
    private List<Producto> productos = new ArrayList<>();
    private Producto productoSeleccionado;
    private TiendaForm ventanaPrincipal;
    
    public ProductosForm(TiendaForm ventanaPrincipal) {
        initComponents();
        cargarCategorias();
        configurarEventos();
        Dimensiones();
        
        // Configurar cierre de ventana
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (JOptionPane.showConfirmDialog(ProductosForm.this,
                    "¿Estás seguro de que quieres cerrar la ventana?", "Cerrar Ventana",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                ventanaPrincipal.setVisible(true);
                ProductosForm.this.dispose();
            }
        }
    });
    
    }
    
    
     private void Dimensiones(){
 // Obtener el tamaño de la pantalla
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    // Obtener el tamaño de la ventana
    int windowWidth = this.getWidth();
    int windowHeight = this.getHeight();

    // Calcular la posición para centrar la ventana
    int x = (screenWidth - windowWidth) / 2;
    int y = (screenHeight - windowHeight) / 2;

    // Establecer la ubicación de la ventana
    this.setLocation(x, y);
    }  
    
    
    // Método para cargar categorías desde la base de datos
    private void cargarCategorias() {
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT id, nombre FROM categorias";
            try (PreparedStatement pst = conn.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {
                
                // Limpiar el combo box de categorías
                jComboBox1.removeAllItems();
                jComboBox1.addItem("Seleccione una categoría");
                
                // Cargar categorías
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    
                    Categoria categoria = new Categoria(id, nombre);
                    categorias.add(categoria);
                    
                    jComboBox1.addItem(nombre);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar categorías: " + e.getMessage(), 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para cargar productos por categoría
 private void cargarProductosPorCategoria(int categoriaId) {
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT id, nombre, descripcion, precio, caracteristicas, inventario FROM productos WHERE categoriaid = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, categoriaId);

                try (ResultSet rs = pst.executeQuery()) {
                    jComboBox2.removeAllItems();
                    jComboBox2.addItem("Seleccione un producto");
                    productos.clear();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        String descripcion = rs.getString("descripcion");
                        double precio = rs.getDouble("precio");
                        String caracteristicas = rs.getString("caracteristicas");
                        int inventario = rs.getInt("inventario");

                        Producto producto = new Producto(id, nombre, descripcion, precio, caracteristicas, inventario);
                        productos.add(producto);
                        jComboBox2.addItem(nombre);
                    }

                    if (productos.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "No hay productos para esta categoría",
                                "Sin Productos",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


   // Configurar eventos de los componentes
private void configurarEventos() {
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int indiceCategoria = jComboBox1.getSelectedIndex();
                jTextArea1.setText("");
                jLabel6.setIcon(null);
                jLabel7.setIcon(null);

                if (indiceCategoria > 0) {
                    int categoriaId = categorias.get(indiceCategoria - 1).getId();
                    cargarProductosPorCategoria(categoriaId);
                } else {
                    jComboBox2.removeAllItems();
                    jComboBox2.addItem("Seleccione un producto");
                }
            }
        });
        
  // Evento para selección de producto
jComboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int indiceProducto = jComboBox2.getSelectedIndex();
                jLabel6.setIcon(null);
                jLabel7.setIcon(null);

                if (indiceProducto > 0) {
                    productoSeleccionado = productos.get(indiceProducto - 1);
                    String informacion = String.format(
                            "Nombre: %s\n" +
                            "Descripción: %s\n" +
                            "Precio: %.2f €\n" +
                            "Caracteristicas: %s\n" +
                            "Inventario: %d",
                            productoSeleccionado.getNombre(),
                            productoSeleccionado.getDescripcion(),
                            productoSeleccionado.getPrecio(),
                            productoSeleccionado.getCaracteristicas(),
                            productoSeleccionado.getInventario()
                    );

                    jTextArea1.setText(informacion);
                    cargarImagenesProducto();
                } else {
                    jTextArea1.setText("");
                    productoSeleccionado = null;
                }
            }
        });
    }

// Método para cargar imágenes del producto
private void cargarImagenesProducto() {
        try {
            if (productoSeleccionado == null) {
                return; // Salir del método si no hay producto seleccionado
            }

            String[] imagenes = obtenerImagenesProducto(productoSeleccionado.getId());

            if (imagenes.length > 0 && !imagenes[0].isEmpty()) {
                ImageIcon imagen1 = cargarImagenRedimensionada(imagenes[0], jLabel6);
                if (imagen1 != null) {
                    jLabel6.setIcon(imagen1);
                }
            }

            if (imagenes.length > 1 && !imagenes[1].isEmpty()) {
                ImageIcon imagen2 = cargarImagenRedimensionada(imagenes[1], jLabel7);
                if (imagen2 != null) {
                    jLabel7.setIcon(imagen2);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar imágenes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] obtenerImagenesProducto(int productoId) {
        String[] imagenes = new String[2]; 
        try (Connection conexion = ConexionBBDD.getConnection()) {
            String consulta = "SELECT imagenes FROM productos WHERE id = ?";
            try (PreparedStatement pst = conexion.prepareStatement(consulta)) {
                pst.setInt(1, productoId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String imagenesStr = rs.getString("imagenes");
                        imagenes = imagenesStr.split(",");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener imágenes: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
        return imagenes;
    }



// Método para cargar y redimensionar imagen
private ImageIcon cargarImagenRedimensionada(String rutaImagen, JLabel label) {
    try {
        // Cargar la imagen original
        ImageIcon iconOriginal = new ImageIcon(rutaImagen);
        
        // Obtener dimensiones del label
        int anchoLabel = label.getWidth();
        int altoLabel = label.getHeight();
        
        // Si las dimensiones son 0, usar dimensiones predeterminadas
        if (anchoLabel <= 0) anchoLabel = 110;
        if (altoLabel <= 0) altoLabel = 110;
        
        // Redimensionar imagen
        Image imagenRedimensionada = iconOriginal.getImage().getScaledInstance(
            anchoLabel, altoLabel, Image.SCALE_SMOOTH
        );
        
        return new ImageIcon(imagenRedimensionada);
    } catch (Exception e) {
        System.err.println("No se pudo cargar la imagen: " + rutaImagen);
        return null;
    }
}




    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(970, 700));
        setResizable(false);
        setSize(new java.awt.Dimension(900, 600));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(971, 517));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Seleccione una Categoria:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Seleccione un Producto:");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Datos Productos:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                        .addContainerGap())))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel5)
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Productos");

        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\rafae\\Documents\\NetBeansProjects\\TiendaElectronicaFinal\\images\\cascos.png")); // NOI18N
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(340, 340, 340)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
       
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
