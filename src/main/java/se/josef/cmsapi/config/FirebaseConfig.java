package se.josef.cmsapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    public FirebaseConfig() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // loads credentials from path specified by GOOGLE_APPLICATION_CREDENTIALS environmental variable
                GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
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
}
