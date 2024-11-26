package com.tiendaelectronica.tiendaelectronicafinal;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class HistorialForm extends javax.swing.JFrame {
    
    private TiendaForm ventanaPrincipal;
    
    //Prepara el combo para cargar usuarios
    private void inicializarModeloComboBox() {
    jComboBox1.setModel(new DefaultComboBoxModel<Usuario>());
}
    // Crea la ventana de historial de compras y configura los componentes iniciales.
    public HistorialForm(TiendaForm ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        initComponents();
        inicializarModeloComboBox();
        configurarComboBox();
        Dimensiones();
        
         // Configurar cierre de ventana
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (JOptionPane.showConfirmDialog(HistorialForm.this,
                    "¿Estás seguro de que quieres cerrar la ventana?", "Cerrar Ventana",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                ventanaPrincipal.setVisible(true);
                HistorialForm.this.dispose();
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
    
      // Carga los usuarios al comboBox y permite seleccionar un usuario para ver su historial.
    private void configurarComboBox() {
    Connection conexion = null;
    try {
        conexion = ConexionBBDD.getConnection();
        Statement stmt = conexion.createStatement();
        // Consultamos los usuarios de la tabla Usuarios
        ResultSet rsUsuarios = stmt.executeQuery("SELECT id, nombre FROM usuarios");

        jComboBox1.removeAllItems(); // Limpiar elementos existentes

        jComboBox1.addItem(new Usuario(0, "Seleccione un usuario"));

        // Añadimos los usuarios al comboBox
        while (rsUsuarios.next()) {
            int usuarioId = rsUsuarios.getInt("id");
            String nombreUsuario = rsUsuarios.getString("nombre");
            jComboBox1.addItem(new Usuario(usuarioId, nombreUsuario));
        }

        // Añadir listener para cargar historial
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario usuarioSeleccionado = (Usuario) jComboBox1.getSelectedItem();
                if (usuarioSeleccionado != null && usuarioSeleccionado.getId() > 0) {
                    cargarHistorialCompras(usuarioSeleccionado.getId());
                }
            }
        });
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
    } finally {
        ConexionBBDD.closeConnection(conexion);
    }
}

    
    // Obtiene y muestra el historial de compras de un usuario
    private void cargarHistorialCompras(int usuarioId) {
        Connection conexion = null;
        try {
            conexion = ConexionBBDD.getConnection();
            Statement stmtHistorial = conexion.createStatement();
            
            // Consultar el historial de compras de los usuarios haciendo referencia a las tablas historial de compras y productos.
            ResultSet rsHistorial = stmtHistorial.executeQuery(
                    "SELECT hc.id, hc.producto_id, hc.cantidad, hc.fecha, " +
                    "p.nombre AS nombre_producto, p.precio " +
                    "FROM historial_compras hc " +
                    "JOIN productos p ON hc.producto_id = p.id " +
                    "WHERE hc.usuario_id = " + usuarioId
            );

            StringBuilder historial = new StringBuilder();
            historial.append("Historial de Compras:\n\n");

            // Recorremos las compras
            while (rsHistorial.next()) {
                int idCompra = rsHistorial.getInt("id");
                String nombreProducto = rsHistorial.getString("nombre_producto");
                int cantidad = rsHistorial.getInt("cantidad");
                double precio = rsHistorial.getDouble("precio");
                Date fecha = rsHistorial.getDate("fecha");

                    //Formateamos los detalles de cada compra
                historial.append("ID Compra: ").append(idCompra).append("\n")
                        .append("Producto: ").append(nombreProducto).append("\n")
                        .append("Cantidad: ").append(cantidad).append("\n")
                        .append("Precio Unitario: ").append(String.format("%.2f", precio)).append(" €\n")
                        .append("Fecha: ").append(fecha).append("\n")
                        .append("Total: ").append(String.format("%.2f", precio * cantidad)).append("€\n\n");
            }
            // Mostramos el historial en el TextArea
            jTextArea1.setText(historial.toString());
           // Mas excepciones.
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar historial de compras: " + e.getMessage());
        } finally {
            ConexionBBDD.closeConnection(conexion);
        }
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        label2 = new java.awt.Label();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        label3 = new java.awt.Label();
        label1 = new java.awt.Label();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(970, 700));
        setResizable(false);
        setSize(new java.awt.Dimension(970, 700));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(970, 700));

        jPanel3.setBackground(new java.awt.Color(102, 102, 255));

        label2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label2.setText("Historial de Compras:");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<Usuario>());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(jTextArea1);

        label3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label3.setText("Seleccione una persona:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(115, 115, 115))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(173, Short.MAX_VALUE))
        );

        label1.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setText("Historial Compras");

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\rafae\\Documents\\NetBeansProjects\\TiendaElectronicaFinal\\images\\historial.png")); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(297, 297, 297))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(66, 66, 66)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Usuario> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    // End of variables declaration//GEN-END:variables
}
