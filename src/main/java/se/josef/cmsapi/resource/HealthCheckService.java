package se.josef.cmsapi.resource;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckService {

    /*
    Used by k8 health checks
     */
    @GetMapping()
    public ResponseEntity<?> HealthCheck() {
        return ResponseEntity.ok().build();
    }

}
