package saecdata.proyectoProductosAlbert.services;

import saecdata.proyectoProductosAlbert.auth.AuthContext;
import saecdata.proyectoProductosAlbert.config.AppConfig;
import saecdata.proyectoProductosAlbert.models.JwtResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthService {

    private final RestTemplate restTemplate;

    public AuthService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean login(String username, String password) {
        String url = AppConfig.BASE_URL + "/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JwtResponse jwt = mapper.readValue(response.getBody(), JwtResponse.class);
                AuthContext.jwtToken = jwt.getToken();
                return true;
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error de autenticación: " + e.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(String username, String password) {
        String url = AppConfig.BASE_URL + "/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
