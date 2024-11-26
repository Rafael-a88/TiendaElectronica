package com.tiendaelectronica.tiendaelectronicafinal;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String direccion;
    
    public Usuario(int id, String nombre){
        this.id= id;
        this.nombre= nombre;
    }

    public Usuario(int id, String nombre, String email, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
    }

    public int getId() { 
        return id; 
    }

    public String getNombre() {
        return nombre; 
    }

    public String getEmail() {
        return email; 
    }

    public String getDireccion() {
        return direccion; 
    }
    
    @Override
    public String toString() {
        return nombre;
    }

   
}
