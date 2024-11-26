CREATE DATABASE tienda_online;
USE tienda_online;

-- Crear tabla de tienda
CREATE TABLE tienda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL
);


-- Crear tabla de categorías
CREATE TABLE categorias (
    id INT  auto_increment PRIMARY KEY,
    nombre VARCHAR(100)
);

-- Crear tabla de productos
CREATE TABLE productos (
    id INT PRIMARY KEY,
    nombre VARCHAR(100),
    precio DECIMAL(10, 2),
    descripcion TEXT,
    caracteristicas VARCHAR(255),
    inventario INT,
    imagenes BLOB,
    categoriaid INT,
    FOREIGN KEY (categoriaid) REFERENCES categorias(id)
);

-- Crear tabla de usuarios
CREATE TABLE usuarios (
    id INT PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100),
    direccion VARCHAR(100)
);

-- Crear tabla de historial de compras
CREATE TABLE historial_compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    producto_id INT,
    cantidad INT,
    fecha VARCHAR(50),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);


drop database tienda_online;
select * from productos;
show tables;
show columns from categorias;