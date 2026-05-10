package com.skillswap.config;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailSSLConfig {

    static {
        try {
            // Disable SSL certificate verification (development only)
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                    (hostname, session) -> true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}