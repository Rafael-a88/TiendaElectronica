package com.tiendaelectronica.tiendaelectronicafinal;


public class TiendaElectronicaFinal {

    public static void main(String[] args) {
        // Ruta del archivo JSON
        String jsonFilePath = "data.json";

        // Crear instancia de LectorJSON
        LectorJSON lector = new LectorJSON(jsonFilePath);

        // Cargar datos en la base de datos
        lector.cargarDatos();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TiendaForm().setVisible(true);
                
               
            }
        });
    }
}
