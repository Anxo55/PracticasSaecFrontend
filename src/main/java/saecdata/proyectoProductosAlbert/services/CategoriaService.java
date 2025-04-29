package saecdata.proyectoProductosAlbert.services;

import saecdata.proyectoProductosAlbert.auth.AuthContext;
import saecdata.proyectoProductosAlbert.config.AppConfig;
import saecdata.proyectoProductosAlbert.models.Categoria;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;

public class CategoriaService {

    private final String BASE_URL = AppConfig.BASE_URL + "/categorias";

    public List<Categoria> getAllCategorias() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // ✅ Añadir token JWT
            if (AuthContext.jwtToken != null && !AuthContext.jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + AuthContext.jwtToken);
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error en la conexión: " + conn.getResponseCode());
            }

            InputStream responseStream = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            List<Categoria> categorias = mapper.readValue(responseStream, new TypeReference<List<Categoria>>() {});
            conn.disconnect();
            return categorias;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
