package se.josef.cmsapi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RequestUtils {

    @Autowired
    private TestRestTemplate restTemplate;

    public <T> ResponseEntity<T> httpRequest(String path, HttpMethod httpMethod, Class<T> responseType, T body, int port) {
        return restTemplate
                .exchange(
                        "http://localhost:" + port + path,
                        httpMethod,
                        new HttpEntity<>(body),
                        responseType
                );
    }
}
