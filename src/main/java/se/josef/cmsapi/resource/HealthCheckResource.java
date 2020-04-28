package se.josef.cmsapi.resource;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api")
@RestController
public class HealthCheckResource {

    /*
    Used by k8 health checks
     */
    @GetMapping()
    public ResponseEntity<?> HealthCheck() {
        return ResponseEntity.ok().build();
    }

}
