package se.josef.cmsapi.firebase;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;
    private final String principal;
    private FirebaseToken credentials;

    public FirebaseAuthenticationToken(FirebaseToken credentials) {
        super(new ArrayList<>());
        this.principal = credentials.getUid();
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public String getPrincipal() {
        return this.principal;
    }

    public FirebaseToken getCredentials() {
        return this.credentials;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
