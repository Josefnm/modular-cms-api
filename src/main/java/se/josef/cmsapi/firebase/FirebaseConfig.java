package se.josef.cmsapi.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@Slf4j
@Profile("!test")
public class FirebaseConfig {

    /**
     * loads a firebase sdk instance with given credentials
     */
    public FirebaseConfig() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // loads credentials from path specified by GOOGLE_APPLICATION_CREDENTIALS environmental variable
                var googleCredentials = GoogleCredentials.getApplicationDefault();
                FirebaseOptions options = new FirebaseOptions
                        .Builder()
                        .setCredentials(googleCredentials)
                        .build();
                FirebaseApp.initializeApp(options);
            } catch (FileNotFoundException e) {
                log.error("File Not Found Exception:" + e.getMessage());
            } catch (IOException e) {
                log.error("IO Exception:" + e.getMessage());
            }
        }
    }


    //needed for firebase auth in testing
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
