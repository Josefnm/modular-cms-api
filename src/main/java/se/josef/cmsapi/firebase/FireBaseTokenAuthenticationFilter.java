package se.josef.cmsapi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class FireBaseTokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_KEY = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String headerValue = request.getHeader(HEADER_KEY);


        if (headerValue != null && !headerValue.isBlank()) {
            try {
                Authentication authentication = getAndValidateAuthentication(headerValue);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new SecurityException("Error authenticating" + e.getLocalizedMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication getAndValidateAuthentication(String headerValue) throws Exception {
        String authToken = headerValue.substring(7);
        FirebaseToken firebaseToken = authenticateFirebaseToken(authToken);
        String userId = firebaseToken.getUid();
        return new FirebaseAuthenticationToken(userId, firebaseToken, new ArrayList<>());
    }

    private FirebaseToken authenticateFirebaseToken(String authToken) throws Exception {
        return FirebaseAuth
                .getInstance()
                .verifyIdToken(authToken);
    }

}
