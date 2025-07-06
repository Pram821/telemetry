package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class OtelDemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(OtelDemoApplication.class);

    static {
        System.setProperty("otel.javaagent.disabled.resource-providers",
                "io.opentelemetry.instrumentation.resources.ManifestResourceProvider");
    }

    public static void main(String[] args) {
        SpringApplication.run(OtelDemoApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("🚀 OpenTelemetry Demo Application started!");
        logger.info("🔍 Endpoints: /api/hello, /api/users/{id}, /api/process, /api/error");
        logger.info("📈 Actuator: /actuator/health, /actuator/metrics, /actuator/prometheus");
    }
}