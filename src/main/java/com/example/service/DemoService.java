package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    public String fetchUserData(String userId) {
        logger.info("Fetching data for user: {}", userId);

        try {
            simulateDbCall(userId);
            String enrichedData = enrichUserData(userId);
            return String.format("User data for %s: %s", userId, enrichedData);
        } catch (Exception e) {
            logger.error("Failed to fetch user data for: {}", userId, e);
            throw new RuntimeException("Failed to fetch user data", e);
        }
    }

    public String processData(String data) {
        logger.info("Processing data");

        try {
            validateData(data);
            String transformed = transformData(data);
            storeData(transformed);
            return "Data processed successfully";
        } catch (Exception e) {
            logger.error("Data processing failed", e);
            throw new RuntimeException("Data processing failed", e);
        }
    }

    private void simulateDbCall(String userId) {
        try {
            Thread.sleep(50);
            logger.debug("Database query completed for user: {}", userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String enrichUserData(String userId) {
        try {
            Thread.sleep(150);
            logger.debug("User data enriched for: {}", userId);
            return "enriched_data_" + userId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "default_data_" + userId;
        }
    }

    private void validateData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        logger.debug("Data validation passed");
    }

    private String transformData(String data) {
        try {
            Thread.sleep(30);
            String transformed = data.toUpperCase() + "_TRANSFORMED";
            logger.debug("Data transformation completed");
            return transformed;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return data;
        }
    }

    private void storeData(String data) {
        try {
            Thread.sleep(25);
            logger.debug("Data stored successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}