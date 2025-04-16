package com.example.uploadImage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // creazione di un bean Spring per configurare
    // l'oggetto S3Client dell'AWS SDK
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // imposta la regione AWS in cui si trova il bucket
                .region(Region.EU_WEST_1)
                // configurazione delle credenziali aws
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY")
                        )
                )
                .build();
    }
}
