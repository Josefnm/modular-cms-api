package se.josef.cmsapi.resource;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthCheckResource {

    /**
     * Used by kubernetes health checks
     *
     * @return Status 200
     */
    @GetMapping()
    public ResponseEntity<?> HealthCheck() {
        return ResponseEntity.ok().build();
    }

}
