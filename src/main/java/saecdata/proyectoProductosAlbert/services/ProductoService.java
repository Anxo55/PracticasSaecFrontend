package saecdata.proyectoProductosAlbert.services;

import saecdata.proyectoProductosAlbert.auth.AuthContext;
import saecdata.proyectoProductosAlbert.config.AppConfig;
import saecdata.proyectoProductosAlbert.models.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoService {

    private final String BASE_URL = AppConfig.BASE_URL + "/productos";

    public List<Producto> getAllProductos() {
        try {
            HttpURLConnection conn = createConnection(BASE_URL, "GET");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error al obtener productos: " + getErrorMessage(conn));
            }

            InputStream responseStream = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseStream, new TypeReference<List<Producto>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean crearProducto(Producto producto) {
        return enviarProducto(producto, "POST", BASE_URL);
    }

    public boolean actualizarProducto(Producto producto) {
        return enviarProducto(producto, "PUT", BASE_URL + "/" + producto.getId());
    }

    public boolean eliminarProducto(Long id) {
        try {
            HttpURLConnection conn = createConnection(BASE_URL + "/" + id, "DELETE");
            int code = conn.getResponseCode();
            return code == 200 || code == 204;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean enviarProducto(Producto producto, String method, String urlString) {
        try {
            HttpURLConnection conn = createConnection(urlString, method);
            conn.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(producto);
    
            // DEBUG: ver qué estás enviando
            System.out.println("JSON enviado al backend:");
            System.out.println(json);
    
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }
    
            int responseCode = conn.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                System.err.println("Error al enviar producto. Código de respuesta: " + responseCode);
                System.err.println("Mensaje de error: " + getErrorMessage(conn));
                return false;
            }
    
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    private HttpURLConnection createConnection(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        if (AuthContext.jwtToken != null && !AuthContext.jwtToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + AuthContext.jwtToken);
            System.out.println("Usando JWT: " + AuthContext.jwtToken); // Para debug
        } else {
            System.err.println("No se ha proporcionado JWT");
        }
        return conn;
    }

    private String getErrorMessage(HttpURLConnection conn) {
        try (InputStream errorStream = conn.getErrorStream()) {
            if (errorStream != null) {
                return new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException ignored) {}
        return "Error desconocido.";
    }
}
