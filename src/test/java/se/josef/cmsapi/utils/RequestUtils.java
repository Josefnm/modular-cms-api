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

    /**
     * Post request for our own controllers
     *
     * @param path         resource path
     * @param responseType expected class of response body
     * @param body         request body
     * @param port         generated by tests
     * @param <T>          request type
     * @param <R>          response type
     * @return response for the request
     */
    public <T, R> ResponseEntity<R> postRequest(String path, Class<R> responseType, T body, int port) {
        var url = "http://localhost:" + port + "/api" + path;

        return restTemplate.postForEntity(url, new HttpEntity<>(body), responseType);
    }

    /**
     * Get request for our own controllers
     *
     * @param path         resource path
     * @param responseType expected class of response body
     * @param port         generated by tests
     * @param <R>          response type
     * @return response for the request
     */
    public <R> ResponseEntity<R> getRequest(String path, Class<R> responseType, int port) {
        var url = "http://localhost:" + port + "/api" + path;

        return restTemplate.getForEntity(url, responseType);
    }

    /**
     * Delete request for our own controllers
     *
     * @param path resource path
     * @param port generated by tests
     */
    public void deleteRequest(String path, int port) {
        var url = "http://localhost:" + port + "/api" + path;

        restTemplate.delete(url);
    }
}
