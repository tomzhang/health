package com.dachen.health.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import static com.codahale.metrics.MetricRegistry.name;
import com.codahale.metrics.Slf4jReporter;


public class MetricBuilder {
	private static final MetricRegistry metricRegistry = new MetricRegistry();

//	private static final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry)
//			.convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();

	private static final Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(metricRegistry)
			.outputTo(LoggerFactory.getLogger(MetricBuilder.class)).convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.MILLISECONDS).build();

	static {
		startReport();
	}

	private static void startReport() {
		// Console Reporter
//		consoleReporter.start(1, TimeUnit.MINUTES);

		slf4jReporter.start(1, TimeUnit.MINUTES);
	}

	public static Histogram requestSpentHistogram = metricRegistry.histogram(name("com.dachen.health", "request.spent-histogram"));
}
