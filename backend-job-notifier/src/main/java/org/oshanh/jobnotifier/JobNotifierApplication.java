package org.oshanh.jobnotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobNotifierApplication {

    static {
        // Allow Java's PKIX validator to fetch missing intermediate
        // certs via AIA, matching browser/Postman behavior.
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(JobNotifierApplication.class, args);
    }

}
