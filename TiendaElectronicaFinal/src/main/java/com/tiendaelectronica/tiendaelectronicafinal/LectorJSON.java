package com.tiendaelectronica.tiendaelectronicafinal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LectorJSON {

    private String jsonFilePath;

    public LectorJSON(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public void cargarDatos() {
        Connection connection = null;

        try {
            connection = ConexionBBDD.getConnection();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            // Comprobar si el nodo raíz es válido
            if (rootNode == null || rootNode.isMissingNode()) {
                System.out.println("No se pudo leer el archivo JSON.");
                return;
            } else {
                System.out.println("Contenido del archivo JSON:");
                System.out.println(rootNode.toString());
            }

            if (!datosYaCargados(connection)) {
                insertarTienda(connection, rootNode);
                insertarCategorias(connection, rootNode);
                insertarProductos(connection, rootNode);
                insertarUsuarios(connection, rootNode);
                insertarCompras(connection, rootNode);
            } else {
                System.out.println("Los datos ya fueron insertados en la Base de datos.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            ConexionBBDD.closeConnection(connection);
        }
    }

    private boolean datosYaCargados(Connection connection) throws SQLException {
        // Comprobar si hay datos en las tablas relevantes
        String query = "SELECT COUNT(*) FROM tienda"; // Cambia esto según sea necesario
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true si hay al menos una fila
            }
        }
        return false;
    }

    private void insertarTienda(Connection connection, JsonNode rootNode) throws SQLException {
        JsonNode tiendasNode = rootNode.get("tienda");
        if (tiendasNode != null) {
            String tiendaNombre = tiendasNode.get("nombre").asText();
            insertarTienda(connection, tiendaNombre);
        } else {
            System.out.println("No se encontró información de la tienda en el archivo JSON.");
        }
    }

    private void insertarTienda(Connection connection, String tiendaNombre) throws SQLException {
        String insertTiendaSQL = "INSERT INTO tienda (nombre) VALUES (?)";
        try (PreparedStatement tiendasStmt = connection.prepareStatement(insertTiendaSQL)) {
            tiendasStmt.setString(1, tiendaNombre);
            tiendasStmt.executeUpdate();
        }
    }

    private void insertarCategorias(Connection connection, JsonNode rootNode) throws SQLException {
        JsonNode tiendasNode = rootNode.get("tienda");
        JsonNode categoriasNode = tiendasNode.get("categorias");
        if (categoriasNode != null && categoriasNode.isArray() && categoriasNode.size() > 0) {
            for (JsonNode categoriaNode : categoriasNode) {
                int categoriaId = categoriaNode.get("id").asInt();
                String categoriaNombre = categoriaNode.get("nombre").asText();
                insertarCategoria(connection, categoriaId, categoriaNombre);
            }
        } else {
            System.out.println("No se encontraron categorías en el archivo JSON.");
        }
    }

    private void insertarCategoria(Connection connection, int categoriaId, String categoriaNombre) throws SQLException {
        String insertCategoriaSQL = "INSERT INTO categorias (id, nombre) VALUES (?, ?)";
        try (PreparedStatement categoriaStmt = connection.prepareStatement(insertCategoriaSQL)) {
            categoriaStmt.setInt(1, categoriaId);
            categoriaStmt.setString(2, categoriaNombre);
            categoriaStmt.executeUpdate();
        }
    }

    private void insertarProductos(Connection connection, JsonNode rootNode) throws SQLException, IOException {
    JsonNode tiendasNode = rootNode.get("tienda");
    JsonNode categoriasNode = tiendasNode.get("categorias");
    if (categoriasNode != null && categoriasNode.isArray() && categoriasNode.size() > 0) {
        for (JsonNode categoriaNode : categoriasNode) {
            JsonNode productosNode = categoriaNode.get("productos");
            if (productosNode != null && productosNode.isArray() && productosNode.size() > 0) {
                for (JsonNode productoNode : productosNode) {
                    int productoId = productoNode.get("id").asInt();
                    String productoNombre = productoNode.get("nombre").asText();
                    double productoPrecio = productoNode.get("precio").asDouble();
                    String productoDescripcion = productoNode.get("descripcion").asText();
                    String productoCaracteristicas = productoNode.get("caracteristicas").asText();
                    
                    // Obtener las imágenes
                    JsonNode imagenesNode = productoNode.get("imagenes");
                    String[] productoImagenes = new String[imagenesNode.size()];
                    for (int i = 0; i < imagenesNode.size(); i++) {
                        String imagenNombre = imagenesNode.get(i).asText();
                        productoImagenes[i] = cargarImagen(imagenNombre); // Cargar la URL
                    }
                    
                    int productoInventario = productoNode.get("inventario").asInt();
                    int categoriaId = categoriaNode.get("id").asInt();
                    insertarProducto(connection, productoId, productoNombre, productoPrecio, productoDescripcion, productoCaracteristicas, productoImagenes, productoInventario, categoriaId);
                }
            } else {
                System.out.println("No se encontraron productos en la categoría.");
            }
        }
    } else {
        System.out.println("No se encontraron categorías en el archivo JSON.");
    }
}


    private String cargarImagen(String imagenNombre) throws IOException {
        String imagenPath = "images/" + imagenNombre;
        File imagenFile = new File(imagenPath);
        if (imagenFile.exists()) {
            return imagenPath;
        } else {
            System.out.println("No se encontró la imagen: " + imagenNombre);
            return "";
        }
    }

    private void insertarProducto(Connection connection, int productoId, String productoNombre, double productoPrecio, String productoDescripcion, String productoCaracteristicas, String[] productoImagenes, int productoInventario, int categoriaId) throws SQLException {
        String insertProductoSQL = "INSERT INTO productos (id, nombre, precio, descripcion, caracteristicas, imagenes, inventario, categoriaId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement productoStmt = connection.prepareStatement(insertProductoSQL)) {
            productoStmt.setInt(1, productoId);
            productoStmt.setString(2, productoNombre);
            productoStmt.setDouble(3, productoPrecio);
            productoStmt.setString(4, productoDescripcion);
            productoStmt.setString(5, productoCaracteristicas);
            productoStmt.setString(6, String.join(",", productoImagenes));
            productoStmt.setInt(7, productoInventario);
            productoStmt.setInt(8, categoriaId);
            productoStmt.executeUpdate();
        }
    }

    private void insertarUsuarios(Connection connection, JsonNode rootNode) throws SQLException {
        JsonNode tiendasNode = rootNode.get("tienda");
        JsonNode usuariosNode = tiendasNode.get("usuarios");
        if (usuariosNode != null && usuariosNode.isArray() && usuariosNode.size() > 0) {
            for (JsonNode usuarioNode : usuariosNode) {
                int usuarioId = usuarioNode.get("id").asInt();
                String usuarioNombre = usuarioNode.get("nombre").asText();
                String usuarioEmail = usuarioNode.get("email").asText();
                String usuarioDireccion = usuarioNode.get("direccion").asText();
                insertarUsuario(connection, usuarioId, usuarioNombre, usuarioEmail, usuarioDireccion);
            }
        } else {
            System.out.println("No se encontraron usuarios en el archivo JSON.");
        }
    }

    private void insertarUsuario(Connection connection, int usuarioId, String usuarioNombre, String usuarioEmail, String usuarioDireccion) throws SQLException {
        String insertUsuarioSQL = "INSERT INTO usuarios (id, nombre, email, direccion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement usuarioStmt = connection.prepareStatement(insertUsuarioSQL)) {
            usuarioStmt.setInt(1, usuarioId);
            usuarioStmt.setString(2, usuarioNombre);
            usuarioStmt.setString(3, usuarioEmail);
            usuarioStmt.setString(4, usuarioDireccion);
            usuarioStmt.executeUpdate();
        }
    }

    private void insertarCompras(Connection connection, JsonNode rootNode) throws SQLException {
        JsonNode tiendaNode = rootNode.get("tienda");
        JsonNode usuariosNode = tiendaNode.get("usuarios");

        if (usuariosNode != null && usuariosNode.isArray()) {
            for (JsonNode usuarioNode : usuariosNode) {
                int usuarioId = usuarioNode.get("id").asInt(); // Obtener el ID del usuario
                JsonNode comprasNode = usuarioNode.get("historialCompras");

                if (comprasNode != null && comprasNode.isArray()) {
                    for (JsonNode compraNode : comprasNode) {
                        try {
                            int productoId = compraNode.get("productoId").asInt();
                            int cantidad = compraNode.get("cantidad").asInt();
                            String fecha = compraNode.get("fecha").asText();
                            insertarCompra(connection, usuarioId, productoId, cantidad, fecha);
                        } catch (SQLException e) {
                            System.out.println("Error al insertar la compra: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("No se encontraron compras para el usuario: " + usuarioNode.get("nombre").asText());
                }
            }
        } else {
            System.out.println("No se encontraron usuarios en el archivo JSON.");
        }
    }

    private void insertarCompra(Connection connection, int usuarioId, int productoId, int cantidad, String fecha) throws SQLException {
        String insertCompraSQL = "INSERT INTO historial_compras (usuario_id, producto_id, cantidad, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement compraStmt = connection.prepareStatement(insertCompraSQL)) {
            compraStmt.setInt(1, usuarioId);
            compraStmt.setInt(2, productoId);
            compraStmt.setInt(3, cantidad);
            compraStmt.setString(4, fecha);
            compraStmt.executeUpdate();
        }
    }
}
