package se.josef.cmsapi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class FirebaseTokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_KEY = "Authorization";

    /**
     * checks headers for auth tokens, checks them against firebase and adds authentication credentials to
     * security context if they are valid
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String headerValue = request.getHeader(HEADER_KEY);
        if (headerValue != null && !headerValue.isBlank()) {
            try {
                // removes bearer prefix
                var token = headerValue.substring(7);
                var firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
                var authentication = new FirebaseAuthenticationToken(firebaseToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Authentication token not valid: " + e.getMessage());

            }
        }
        filterChain.doFilter(request, response);
    }


}
