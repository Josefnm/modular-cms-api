package se.josef.cmsapi.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import se.josef.cmsapi.exception.UserException;

@Component
public class UserUtils {

    /**
     * Retrieves user id from security context.
     * Needs to be mocked in tests
     *
     * @return userId
     */
    public String getUserId() {
        try {
            return SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal()
                    .toString();
        } catch (Exception e) {
            throw new UserException(String.format("Can't get user id from security context: %s", e.getMessage()));
        }
    }
}
