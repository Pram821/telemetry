package com.example.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelFixConfig {
    static {
        System.setProperty("otel.java.disabled.resource.providers", 
                          "io.opentelemetry.instrumentation.resources.ManifestResourceProvider");
    }
}
