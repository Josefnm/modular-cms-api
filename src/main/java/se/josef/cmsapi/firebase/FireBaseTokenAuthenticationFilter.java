package se.josef.cmsapi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import se.josef.cmsapi.interfaces.TFunction;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class FireBaseTokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_KEY = "Authorization";

    // Experimenting a bit with functions.
    TFunction<String, FirebaseToken> authenticateFirebaseToken = FirebaseAuth.getInstance()::verifyIdToken;
    TFunction<String, String> getAuthToken = headerValue -> headerValue.substring(7);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String headerValue = request.getHeader(HEADER_KEY);

        if (headerValue != null && !headerValue.isBlank()) {
            try {
                Authentication authentication = getAuthToken
                        .andThen(authenticateFirebaseToken)
                        .andThen(FirebaseAuthenticationToken::new)
                        .applyThrows(headerValue);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new SecurityException("Error authenticating" + e.getLocalizedMessage());
            }
        }
        filterChain.doFilter(request, response);
    }


}
