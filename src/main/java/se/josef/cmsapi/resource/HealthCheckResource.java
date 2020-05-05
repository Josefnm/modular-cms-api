package se.josef.cmsapi.resource;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class HealthCheckResource {

    /*
    Used by k8 health checks
     */
    @GetMapping()
    public ResponseEntity<?> HealthCheck(HttpServletRequest request) {
        log.info("health check path:/ origin: {}",request.getHeader("origin"));
        return ResponseEntity.ok().build();
    }

    /*
    Used by k8 health checks
     */
    @GetMapping(value = "api")
    public ResponseEntity<?> HealthCheck2(HttpServletRequest request) {
        log.info("health check path:/api origin: {}",request.getHeader("origin"));
        return ResponseEntity.ok().build();
    }

    //Used to test origin
    @GetMapping(value = "/cross")
    public @ResponseBody
    String checkOrigin(HttpServletRequest request) {
        return request.getHeader("origin");
    }
}
