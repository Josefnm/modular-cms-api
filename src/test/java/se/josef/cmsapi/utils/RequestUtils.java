package se.josef.cmsapi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RequestUtils {

    @Autowired
    private TestRestTemplate restTemplate;

    public <T> ResponseEntity<T> postRequest(String path, Class<T> responseType, T body, int port) {
        var url = "http://localhost:" + port + "/api" + path;

        return restTemplate.postForEntity(url, new HttpEntity<>(body), responseType);
    }

    public <T> ResponseEntity<T> getRequest(String path, Class<T> responseType, int port) {
        var url = "http://localhost:" + port + "/api" + path;

        return restTemplate.getForEntity(url, responseType);
    }
}
