package se.josef.cmsapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SecurityAuditorAware implements AuditorAware<String> {

    /**
     * Used by mongo auditing to access user id for the @CreatedBY annotation
     * Needs to be mocked in tests
     *
     * @return
     */
    @Override
    public Optional<String> getCurrentAuditor() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(authentication.getPrincipal().toString());
    }
}
