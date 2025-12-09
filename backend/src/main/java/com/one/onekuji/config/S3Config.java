package com.one.onekuji.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;

import java.net.URI;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client(S3Properties s3Properties) {
    var builder = S3Client.builder();
        if (s3Properties.getRegion() != null && !s3Properties.getRegion().isEmpty()) {
            builder.region(Region.of(s3Properties.getRegion()));
        }
        if (s3Properties.getAccessKeyId() != null && !s3Properties.getAccessKeyId().isEmpty()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Properties.getAccessKeyId(), s3Properties.getSecretAccessKey())));
        }
        if (s3Properties.getEndpointUrl() != null && !s3Properties.getEndpointUrl().isEmpty()) {
            builder.endpointOverride(URI.create(s3Properties.getEndpointUrl()));
        }
        if (s3Properties.isUsePathStyle()) {
            builder.serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }
        return builder.build();
    }

    @Bean
    public S3Utilities s3Utilities(S3Properties s3Properties) {
        if (s3Properties.getRegion() != null && !s3Properties.getRegion().isEmpty()) {
            return S3Utilities.builder().region(Region.of(s3Properties.getRegion())).build();
        }
        return S3Utilities.builder().build();
    }
}
