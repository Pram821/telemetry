package com.example.controller;

import com.example.service.DemoService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class MetricsTestController {
    private static final Logger logger = LoggerFactory.getLogger(MetricsTestController.class);

    private final MeterRegistry meterRegistry;
    private final DemoService demoService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("otel-demo-app");
    private final Counter requestCounter;
    private final Timer requestTimer;
    private final Random random = new Random();

    public MetricsTestController(MeterRegistry meterRegistry, DemoService demoService) {
        this.meterRegistry = meterRegistry;
        this.demoService = demoService;

        this.requestCounter = Counter.builder("api.requests.total")
                .description("Total number of API requests")
                .tag("controller", "metrics")
                .register(meterRegistry);

        this.requestTimer = Timer.builder("api.request.duration")
                .description("API request duration")
                .tag("controller", "metrics")
                .register(meterRegistry);
    }

    @GetMapping("/hello")
    @WithSpan("hello-endpoint")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        Span span = Span.current();
        span.setAttribute("user.name", name);

        return requestTimer.record(() -> {
            requestCounter.increment();
            logger.info("Hello endpoint called with name: {}", name);

            try {
                Thread.sleep(random.nextInt(100) + 50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return "Hello, " + name + "!";
        });
    }

    @GetMapping("/user/{userId}")
    @WithSpan("get-user-data")
    public String getUserData(@PathVariable String userId) {
        Span span = Span.current();
        span.setAttribute("user.id", userId);

        return requestTimer.record(() -> {
            requestCounter.increment();
            logger.info("Fetching data for user: {}", userId);

            try {
                return demoService.fetchUserData(userId);
            } catch (Exception e) {
                span.recordException(e);
                meterRegistry.counter("api.errors", "type", "user_fetch").increment();
                throw e;
            }
        });
    }

    @PostMapping("/process")
    @WithSpan("process-data")
    public String processData(@RequestBody String data) {
        Span span = Span.current();
        span.setAttribute("data.length", data.length());

        return requestTimer.record(() -> {
            requestCounter.increment();
            logger.info("Processing data of length: {}", data.length());

            try {
                return demoService.processData(data);
            } catch (Exception e) {
                span.recordException(e);
                meterRegistry.counter("api.errors", "type", "processing").increment();
                throw e;
            }
        });
    }

    @GetMapping("/metrics-test")
    public String testMetrics() {
        for (int i = 0; i < 10; i++) {
            meterRegistry.counter("test.counter", "iteration", String.valueOf(i)).increment();
            meterRegistry.gauge("test.gauge", i * 10);

            Timer.Sample sample = Timer.start(meterRegistry);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sample.stop(meterRegistry.timer("test.timer"));
        }

        return "Metrics generated!";
    }

    @GetMapping("/error")
    @WithSpan("error-endpoint")
    public String simulateError() {
        Span span = Span.current();
        span.setAttribute("error.simulated", true);

        if (random.nextBoolean()) {
            RuntimeException e = new RuntimeException("Simulated error for testing");
            span.recordException(e);
            meterRegistry.counter("api.errors", "type", "simulated").increment();
            throw e;
        }

        return "No error this time!";
    }
}
