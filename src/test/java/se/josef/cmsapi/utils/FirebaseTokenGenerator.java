package se.josef.cmsapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import se.josef.cmsapi.util.JsonUtil;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FirebaseTokenGenerator {

    @Value("${firebase.user.id}")
    private String uid;
    @Value("${firebase.custom.token.uri}")
    private String customTokenUri;
    @Value("${FIREBASE_API_KEY}")
    private String firebaseApiKey;

    private String accessToken;

    @Autowired
    private FirebaseAuth firebaseAuth;
    private final RestTemplate restTemplate;
    private final JsonUtil jsonUtil;

    public FirebaseTokenGenerator(RestTemplate restTemplate, JsonUtil jsonUtil) {
        this.restTemplate = restTemplate;
        this.jsonUtil = jsonUtil;
    }

    @Bean
    public String getAccessToken() {
        return accessToken;
    }

    @PostConstruct
    public void init() {

        try {
            String customToken = firebaseAuth.getInstance().createCustomToken(uid);
            Map<String, String> tokens = getTokens(customToken);
            accessToken = "Bearer "+tokens.get("idToken");
log.info(accessToken);
        } catch (FirebaseAuthException | JsonProcessingException e) {
            log.error("Error generating access token:" + e.getMessage());
        }
    }

    private Map<String, String> getTokens(String customToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<TokenRequest> httpEntity = new HttpEntity<>(new TokenRequest(customToken,"true"),headers);
        String url = customTokenUri + firebaseApiKey;

        String response = restTemplate
                .exchange(
                        url,
                        HttpMethod.POST,
                        httpEntity,
                        String.class
                ).getBody();

        return jsonUtil.getJsonAsMap(response);
    }

}
