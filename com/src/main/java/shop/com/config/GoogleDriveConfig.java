package shop.com.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.service.account.key}")
    private String serviceAccountKey;

    @Bean
    public Drive googleDrive() throws IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(serviceAccountKey))
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(credential.getTransport(), credential.getJsonFactory(), credential)
                .setApplicationName("MyAppName")
                .build();
    }
}

