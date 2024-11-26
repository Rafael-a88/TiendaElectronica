package com.tiendaelectronica.tiendaelectronicafinal;


public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String caracteristicas;
    private int inventario;

    public Producto(int id, String nombre, String descripcion, double precio, String caracteristicas, int inventario) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.caracteristicas= caracteristicas;
        this.inventario= inventario;
    }

    public int getId() { 
        return id; }
    
    public String getNombre() { 
        return nombre; }
    
    public String getDescripcion() { 
        return descripcion; }
    
    public double getPrecio() { 
        return precio; }
    
    public String getCaracteristicas(){
        return caracteristicas;
    }
    
    public int getInventario(){
        return inventario;
    }
}
