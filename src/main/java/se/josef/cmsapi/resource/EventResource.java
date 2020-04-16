package se.josef.cmsapi.resource;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/events", produces = APPLICATION_JSON_VALUE)
@Slf4j
public final class EventResource {


    @GetMapping
    public ResponseEntity<Map<String,String>> getEventsByGroup(){
        log.debug(String.valueOf(System.getenv()));

        return ResponseEntity.ok().body(System.getenv());
    }

    @GetMapping(value = "/2")
    public ResponseEntity<String> getEventsByGroup2() throws IOException {
        GoogleCredentials applicationDefault = GoogleCredentials.getApplicationDefault();

        return ResponseEntity.ok().body(applicationDefault.toString());
    }

}