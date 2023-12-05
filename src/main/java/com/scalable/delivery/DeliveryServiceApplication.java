package com.scalable.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import io.micrometer.core.instrument.*;

@SpringBootApplication
public class DeliveryServiceApplication {

	@Bean
	@ConditionalOnClass(name = "io.opentelemetry.javaagent.OpenTelemetryAgent")
	public MeterRegistry otelRegistry() {
		Optional<MeterRegistry> otelRegistry = Metrics.globalRegistry.getRegistries().stream()
			.filter(r -> r.getClass().getName().contains("OpenTelemetryMeterRegistry"))
			.findAny();
		otelRegistry.ifPresent(Metrics.globalRegistry::remove);
		return otelRegistry.orElse(null);
	}

	public static void main(String[] args) {
		SpringApplication.run(DeliveryServiceApplication.class, args);
	}

}
