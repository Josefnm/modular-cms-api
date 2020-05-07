package se.josef.cmsapi.util;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    public String getUserId() {
        var firebaseToken = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials();
        if (firebaseToken instanceof FirebaseToken) {
            return ((FirebaseToken) firebaseToken).getUid();
        }
        return null;
    }
}
