package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Abstract class pulling up all configuration that is needed for production as well as for tests.
 *
 * The subclasses add the differences for prod/test
 */
@Import({
  AnnotationProcessorConfiguration.class,
  ReadAnnotationValueConfiguration.class,
})
public abstract class AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy() {
    return new DefaultCommandExceptionHandlingStrategy(backoffSupplier(), scheduledExecutorService());
  }

  @Bean
  public ScheduledExecutorService scheduledExecutorService() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  @Bean
  public BackoffSupplier backoffSupplier() {
    return new ExponentialBackoffBuilderImpl()
      .maxDelay(1000L)
      .minDelay(50L)
      .backoffFactor(1.5)
      .jitterFactor(0.2)
      .build();
  }
}
